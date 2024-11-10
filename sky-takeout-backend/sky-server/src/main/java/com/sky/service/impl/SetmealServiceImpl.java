package com.sky.service.impl;

import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 此方法用于：套餐业务实现
 */
@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {
    private final SetmealMapper setmealMapper;
    private final SetmealDishMapper setmealDishMapper;

    @Autowired
    public SetmealServiceImpl(SetmealMapper setmealMapper, SetmealDishMapper setmealDishMapper) {
        this.setmealMapper = setmealMapper;
        this.setmealDishMapper = setmealDishMapper;
    }

    /**
     * 此方法用于：条件查询套餐
     *
     * @param setmeal 套餐
     * @return List<Setmeal>
     */
    public List<Setmeal> list(Setmeal setmeal) {
        return setmealMapper.list(setmeal);
    }

    /**
     * 此方法用于：根据 id 查询菜品选项
     *
     * @param id 套餐 Id
     * @return List<DishItemVO>
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.selectDishItemBySetmealId(id);
    }

    /**
     * 此方法用于：新增套餐
     *
     * @param setmealDTO 套餐
     */
    @Override
    public int saveWithDish(SetmealDTO setmealDTO) {
        int num1 = setmealMapper.insertWithDish(setmealDTO);
        int num2 = setmealDishMapper.insertBatch(setmealDTO.getSetmealDishes());
        return num1 + num2;
    }
}
