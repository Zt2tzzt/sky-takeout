package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.HashMap;
import java.util.List;

public interface DishService {
    /**
     * 此方法用于：新增菜品和对应的口味
     *
     * @param dishDTO 菜品数据
     * @return 插入记录数
     */
    int saveWithFlavor(DishDTO dishDTO);

    /**
     * 此方法用于：分页查询菜品
     *
     * @param dishPageQueryDTO 分页查询参数
     * @return 分页结果
     */
    PageResult<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 此方法用于：根据 id 批量删除菜品
     *
     * @param ids 菜品 Id
     * @return 删除结果
     */
    HashMap<String, Integer> removeBatch(List<Long> ids);

    /**
     * 此方法用于：根据菜品 id 查询菜品和对应的口味
     *
     * @param id 菜品 Id
     * @return DishVO
     */
    DishVO dishWithFlavors(Long id);

    /**
     * 此方法用于：修改菜品
     *
     * @param dishDTO 菜品数据
     * @return 修改记录数
     */
    int modifyWithFlavor(DishDTO dishDTO);

    /**
     * 此方法用于：条件查询菜品和口味
     *
     * @param categoryId 分类 id
     * @return List<DishVO>
     */
    List<DishVO> listWithFlavorByCategoryId(Long categoryId);
}
