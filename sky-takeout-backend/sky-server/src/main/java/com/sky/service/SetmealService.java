package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

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

    /**
     * 此方法用于：新增套餐
     *
     * @param setmealDTO 套餐
     */
    int saveWithDish(SetmealDTO setmealDTO);

    /**
     * 此方法用于：分页查询
     *
     * @param setmealPageQueryDTO 分页查询条件
     * @return PageResult<Setmeal>
     */
    PageResult<Setmeal> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 此方法用于：批量删除套餐
     *
     * @param ids 套餐 id
     * @return int
     */
    int deleteBatch(List<Long> ids);

    /**
     * 此方法用于：根据 id 查询套餐
     *
     * @param id 套餐 id
     * @return int
     */
    SetmealVO setmealById(Long id);

    /**
     * 此方法用于：修改套餐
     *
     * @param setmealDTO 套餐
     * @return int
     */
    int modify(SetmealDTO setmealDTO);

    /**
     * 此方法用于：启用停售套餐
     *
     * @param status 状态
     * @param id     套餐 id
     * @return int
     */
    int startOrStop(int status, Long id);
}
