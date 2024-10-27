package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 此方法用于：新增菜品和对应的口味数据
     *
     * @param dishDTO 菜品数据
     * @return 插入的行数
     */
    @Override
    @Transactional
    public int saveWithFlavor(DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);

        // 向菜品表插入 1 条数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        int num1 = dishMapper.insert(dish);

        // 向菜品口味表，插入 n 条数据
        int num2 = 0;
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            Long dishId = dish.getId(); // 获取菜品的 id
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
            num2 = dishFlavorMapper.insertBatch(flavors);
        }

        return num1 + num2;
    }
}
