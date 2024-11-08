package com.sky.service.impl;

import com.sky.entity.Setmeal;
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

    @Autowired
    public SetmealServiceImpl(SetmealMapper setmealMapper) {
        this.setmealMapper = setmealMapper;
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
}
