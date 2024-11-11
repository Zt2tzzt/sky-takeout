package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
     *
     * @param ids 菜品 id 集合
     * @return 菜品 id 集合关联的套餐数量
     */
    int countByDishId(List<Long> ids);

    /**
     * 此方法用于：批量插入套餐菜品关系数据
     *
     * @param setmealDishes 套餐菜品关系数据集合
     * @return int
     */
    int insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 此方法用于：根据套餐 id 批量删除套餐菜品关系数据
     *
     * @param ids 套餐 id 集合
     * @return int
     */
    int deleteBatchBySetmealId(List<Long> ids);

    /**
     * 此方法用于：根据套餐 id 查询套餐菜品关系数据
     *
     * @param setmealId 套餐 id
     * @return SetmealDish
     */
    @Select("SELECT id, setmeal_id, dish_id, name, price, copies FROM setmeal_dish WHERE setmeal_id = #{setmealId}")
    List<SetmealDish> selectBySetmealId(Long setmealId);
}
