package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

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

    /**
     * 此方法用于：分页查询菜品
     *
     * @param dishPageQueryDTO 分页查询参数
     * @return 分页结果
     */
    @Override
    public PageResult<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());

        Page<DishVO> dishPage = dishMapper.selectWithPage(dishPageQueryDTO);
        return new PageResult<>(dishPage.getTotal(), dishPage.getResult());
    }

    /**
     * 此方法用于：根据 id 批量删除菜品
     *
     * @param ids 菜品 Id
     * @return 删除结果
     */
    @Override
    @Transactional
    public HashMap<String, Integer> removeBatch(List<Long> ids) {
        // 菜品状态为起售中，不能删除菜品
        /*for (Long id : ids) {
            Dish dish = dishMapper.selectById(id);
            if (Objects.equals(dish.getStatus(), StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }*/
        int i = dishMapper.countStatusByIds(ids);
        if (i > 0) throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);

        // 菜品被套餐关联，不能删除菜品
        List<Long> setmealIds = setmealDishMapper.selectSetmealIdsByDishId(ids);
        if (setmealIds != null && !setmealIds.isEmpty())
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);

        // 删除菜品数据
        int num1 = dishMapper.deleteBatchById(ids);
        // 删除菜品口味数据
        int num2 = dishFlavorMapper.deleteBatchByDishId(ids);

        // 删除菜品关联的口味数据
        return new HashMap<>(Map.of("菜品", num1, "菜品口味", num2));
    }

    /**
     * 此方法用于：根据菜品 id 查询菜品和对应的口味
     *
     * @param id 菜品 Id
     * @return DishVO
     */
    @Override
    public DishVO dishWithFlavors(Long id) {
        // 根据菜品 Id 查询菜品数据
        Dish dish = dishMapper.selectById(id);
        // 根据菜品 Id 查询口味数据
        List<DishFlavor> flavors = dishFlavorMapper.selectByDishId(id);
        // 将查询到的数据封装到 VO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    /**
     * 此方法用于：修改菜品
     *
     * @param dishDTO 菜品数据
     * @return 修改记录数
     */
    @Override
    public int modifyWithFlavor(DishDTO dishDTO) {
        // 修改菜品表基本信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        int i = dishMapper.update(dish);

        // 删除菜品对应的所有口味
        int j = dishFlavorMapper.deleteBatchByDishId(new ArrayList<>(List.of(dish.getId())));

        // 插入前端传过来的菜品口味
        int k = 0;
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            Long dishId = dish.getId(); // 获取菜品的 id
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
            k = dishFlavorMapper.insertBatch(flavors);
        }

        return i + j + k;
    }
}
