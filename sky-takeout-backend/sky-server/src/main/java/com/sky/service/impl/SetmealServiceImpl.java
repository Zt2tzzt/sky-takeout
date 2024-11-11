package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 此方法用于：分页查询
     *
     * @param setmealPageQueryDTO 分页查询条件
     * @return PageResult<Setmeal>
     */
    @Override
    public PageResult<Setmeal> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());

        Page<Setmeal> pageSetmeals = setmealMapper.selectWithPage(setmealPageQueryDTO);
        return new PageResult<>(pageSetmeals.getTotal(), pageSetmeals.getResult());
    }

    /**
     * 此方法用于：批量删除套餐
     *
     * @param ids 套餐 id
     * @return int
     */
    @Transactional
    @Override
    public int deleteBatch(List<Long> ids) {
        // 删除套餐与菜品的关联
        int num1 = setmealDishMapper.deleteBatchBySetmealId(ids);
        // 删除套餐
        int num2 = setmealMapper.deleteBatchById(ids);
        return num1 + num2;
    }

    /**
     * 此方法用于：根据 id 查询套餐
     *
     * @param id 套餐 id
     * @return int
     */
    @Override
    public SetmealVO setmealById(Long id) {
        SetmealVO setmealVO = setmealMapper.selectById(id);
        List<SetmealDish> setmealDishes = setmealDishMapper.selectBySetmealId(id);

        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 此方法用于：修改套餐
     *
     * @param setmealDTO 套餐
     * @return int
     */
    @Override
    public int modify(SetmealDTO setmealDTO) {
        return setmealMapper.update(setmealDTO);
    }

    /**
     * 此方法用于：启用停售套餐
     *
     * @param status 状态
     * @param id     套餐 id
     * @return int
     */
    @Override
    public int startOrStop(int status, Long id) {
        return setmealMapper.updateStatus(status, id);
    }
}
