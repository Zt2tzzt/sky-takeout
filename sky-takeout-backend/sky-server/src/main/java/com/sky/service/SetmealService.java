package com.sky.service;

import com.sky.entity.Setmeal;
import com.sky.vo.DishItemVO;

import java.util.List;

public interface SetmealService {

    /**
     * 此方法用于：条件查询
     *
     * @param setmeal 套餐
     * @return List<Setmeal>
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 此方法用于：根据id查询菜品选项
     *
     * @param id 菜品 id
     * @return List<DishItemVO>
     */
    List<DishItemVO> getDishItemById(Long id);
}
