package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

@RestController
@RequestMapping("/admin/dish")
@Tag(name = "菜品相关接口")
public class DishController {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(DishController.class);
    private final DishService dishService;
    private final RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    public DishController(DishService dishService, RedisTemplate<Object, Object> redisTemplate) {
        this.dishService = dishService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 此方法用于：新增菜品
     *
     * @param dishDTO 前端提交的菜品数据
     * @return Result<String>
     */
    @PostMapping
    @Operation(summary = "新增菜品")
    public Result<String> save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);

        int num = dishService.saveWithFlavor(dishDTO);

        // 清理 Redis 缓存
        if (num <= 0)
            return Result.error("插入失败", null);

        // 构造 key
        String key = "dish_" + dishDTO.getCategoryId();
        redisTemplate.delete(key);
        return Result.success("成功插入" + num + "条数据");
    }

    /**
     * 此方法用于：分页查询菜品
     *
     * @param dishPageQueryDTO 前端提交的查询参数
     * @return Result<PageResult < DishVO>>
     */
    @GetMapping("/page")
    @Operation(summary = "菜品分页查询")
    public Result<PageResult<DishVO>> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询，查询参数为：{}", dishPageQueryDTO);

        PageResult<DishVO> pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 此方法用于：删除菜品
     *
     * @param ids 菜品id
     * @return Result<String>
     */
    @DeleteMapping
    @Operation(summary = "删除菜品")
    public Result<String> remove(@RequestParam List<Long> ids) {
        log.info("删除菜品 {}", ids);

        HashMap<String, Integer> map = dishService.removeBatch(ids);

        // 将 redis 中所有的菜品缓存数据清理掉：所有以 dish_ 开头的 key
        Set<Object> keys = redisTemplate.keys("dish_*"); // 查询时支持通配符；删除时不支持通配符
        redisTemplate.delete(keys);

        StringJoiner sj = new StringJoiner("；");
        map.forEach((name, num) -> sj.add(name + "已删除" + num + "条"));
        return Result.success(sj.toString());
    }

    /**
     * 此方法用于：根据 id 查询菜品
     *
     * @param id 菜单 Id
     * @return Result<DishVO>
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询菜品")
    public Result<DishVO> dish(@PathVariable Long id) {
        log.info("根据id查询菜品，菜品id为：{}", id);

        DishVO dishVO = dishService.dishWithFlavors(id);
        return Result.success(dishVO);
    }

    /**
     * 此方法用于：修改菜品
     *
     * @param dishDTO 前端提交的菜品数据
     * @return Result<String>
     */
    @PutMapping
    @Operation(summary = "修改菜品")
    public Result<String> modify(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品：{}", dishDTO);

        int num = dishService.modifyWithFlavor(dishDTO);

        if (num <= 0)
            return Result.error("修改失败", null);

        // 将 redis 中所有的菜品缓存数据清理掉：所有以 dish_ 开头的 key
        Set<Object> keys = redisTemplate.keys("dish_*"); // 查询时支持通配符；删除时不支持通配符
        redisTemplate.delete(keys);

        return Result.success("成功修改" + num + "条数据");
    }
}
