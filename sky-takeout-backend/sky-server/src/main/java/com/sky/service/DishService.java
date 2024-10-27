package com.sky.service;

import com.sky.dto.DishDTO;

public interface DishService {
    /**
     * 此方法用于：新增菜品和对应的口味
     *
     * @param dishDTO 菜品数据
     * @return 插入记录数
     */
    int saveWithFlavor(DishDTO dishDTO);
}
