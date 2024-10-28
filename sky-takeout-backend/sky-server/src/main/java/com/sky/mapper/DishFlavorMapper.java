package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 此方法用于：批量插入菜品口味数据
     *
     * @param flavors 菜品口味集合
     * @return 插入的条数
     */
    int insertBatch(List<DishFlavor> flavors);

    /**
     * 此方法用于：根据菜品id删除对应的口味数据
     * @param ids 菜品 Id 集合
     * @return 删除的条数
     */
    int deleteBatchByDishId(List<Long> ids);

    /**
     * 此方法用于：根据菜品 id 查询对应的口味
     * @param id 菜品 Id
     * @return List<DishFlavor>
     */
    @Select("SELECT id, dish_id, name, value FROM dish_flavor WHERE dish_id = #{id}")
    List<DishFlavor> selectByDishId(Long id);
}
