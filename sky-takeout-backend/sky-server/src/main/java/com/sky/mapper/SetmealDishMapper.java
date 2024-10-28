package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 此方法用于：根据菜品 id 查询套餐 id
     *
     * @param dishIds 菜品 id 集合
     * @return List<Long>
     */
    List<Long> selectSetmealIdsByDishId(List<Long> dishIds);

    /**
     * 此方法用于：根据菜品 id 查询套餐数量
     * @param ids 菜品 id 集合
     * @return 菜品 id 集合关联的套餐数量
     */
    int countByDishId(List<Long> ids);
}
