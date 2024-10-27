package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

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
}
