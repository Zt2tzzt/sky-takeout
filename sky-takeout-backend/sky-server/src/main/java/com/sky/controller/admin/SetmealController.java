package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminSetmealController")
@RequestMapping("/admin/setmeal")
@Tag(name = "套餐管理接口")
@Slf4j
public class SetmealController {
    private final SetmealService setmealService;

    @Autowired
    public SetmealController(SetmealService setmealService) {
        this.setmealService = setmealService;
    }

    @PostMapping
    @Operation(summary = "新增套餐")
    @CachePut(cacheNames = "SetmealCache", key = "#setmealDTO.categoryId")
    public Result<String> save(@RequestBody SetmealDTO setmealDTO) {
        int num = setmealService.saveWithDish(setmealDTO);
        return Result.success("成功插入" + num + "条数据");
    }

    @GetMapping("/page")
    @Operation(summary = "套餐分页查询")
    public Result<PageResult<Setmeal>> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("套餐分页查询 Controller：{}", setmealPageQueryDTO);
        PageResult<Setmeal> pageResult = setmealService.pageQuery(setmealPageQueryDTO);

        return Result.success(pageResult);
    }

    @DeleteMapping
    @Operation(summary = "删除套餐")
    @CacheEvict(cacheNames = "SetmealCache", allEntries = true)
    public Result<String> deleteById(List<Long> ids) {
        int i = setmealService.deleteBatch(ids);
        return i > 0 ? Result.success("成功删除" + i + "条数据") : Result.error("删除失败", null);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据id查询套餐")
    public Result<SetmealVO> setmealById(Long id) {
        SetmealVO setmealVO = setmealService.setmealById(id);
        return Result.success(setmealVO);
    }

    @PutMapping
    @Operation(summary = "修改套餐")
    @CacheEvict(cacheNames = "SetmealCache", allEntries = true)
    public Result<String> modify(@RequestBody SetmealDTO setmealDTO) {
        int i = setmealService.modify(setmealDTO);
        return i > 0 ? Result.success("成功修改" + i + "条数据") : Result.error("修改失败", null);
    }

    @PostMapping("/status/{status}")
    @CacheEvict(cacheNames = "SetmealCache", allEntries = true)
    public Result<String> startOrStop(@PathVariable int status, Long id) {
        int i = setmealService.startOrStop(status, id);
        return i > 0 ? Result.success("成功修改" + i + "条数据") : Result.error("修改失败", null);
    }
}
