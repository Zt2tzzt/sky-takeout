package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Tag(name = "菜品相关接口")
public class DishController {
    private final DishService dishService;
    private final RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    public DishController(DishService dishService, RedisTemplate<Object, Object> redisTemplate) {
        this.dishService = dishService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId 分类 id
     * @return Result<List < DishVO>>
     */
    @GetMapping("/list")
    @Operation(summary = "根据分类id查询菜品")
    @SuppressWarnings("unchecked")
    public Result<List<DishVO>> list(Long categoryId) {
        log.info("根据分类id查询菜品，分类id为：{}", categoryId);

        // 构造 redis 中的 key；规则：dish_{categoruId}
        String key = "dish_" + categoryId;

        // 查询 redis 中是否存在菜品数据
        ValueOperations<Object, Object> valueOperations = redisTemplate.opsForValue();
        List<DishVO> dishVOList = (List<DishVO>) valueOperations.get(key);

        // 如果存在，直接返回；
        if (dishVOList != null && !dishVOList.isEmpty())
            return Result.success(dishVOList);

        // 如果不存在，查询数据库，并将查询结果放入 redis 中。
        dishVOList = dishService.listWithFlavorByCategoryId(categoryId);
        log.info("查询结果：{}", dishVOList);
        valueOperations.set(key, dishVOList);

        return Result.success(dishVOList);
    }
}
