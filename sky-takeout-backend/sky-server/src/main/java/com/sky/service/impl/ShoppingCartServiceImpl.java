package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartMapper shoppingCartMapper;
    private final DishMapper dishMapper;
    private final SetmealMapper setmealMapper;

    @Autowired
    public ShoppingCartServiceImpl(ShoppingCartMapper shoppingCartMapper, DishMapper dishMapper, SetmealMapper setmealMapper) {
        this.shoppingCartMapper = shoppingCartMapper;
        this.dishMapper = dishMapper;
        this.setmealMapper = setmealMapper;
    }

    /**
     * 此方法用于：添加购物车
     *
     * @param shoppingCartDTO 购物车数据
     * @return int
     */
    @Override
    public int add(ShoppingCartDTO shoppingCartDTO) {
        // 判断要加入的商品，是否已经存在
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId(); // 用户 id
        shoppingCart.setUserId(userId);

        List<ShoppingCart> cartRecordList = shoppingCartMapper.selectByShoppingCart(shoppingCart); // 只可能查出一条数据

        // 如果存在，则数量加一
        if (cartRecordList != null && !cartRecordList.isEmpty()) {
            ShoppingCart cartRecord = cartRecordList.get(0);
            cartRecord.setNumber(cartRecord.getNumber() + 1);
            return shoppingCartMapper.updateById(cartRecord);
        }

        // 如果不存在，需要插入一条购物车记录，补全冗余字段的值
        if (shoppingCart.getDishId() != null) {
            // 添加的是菜品
            Dish dish = dishMapper.selectById(shoppingCart.getDishId());
            shoppingCart.setName(dish.getName());
            shoppingCart.setImage(dish.getImage());
            shoppingCart.setAmount(dish.getPrice());
        } else {
            // 添加的是套餐
            SetmealVO setmealVO = setmealMapper.selectById(shoppingCart.getSetmealId());
            shoppingCart.setName(setmealVO.getName());
            shoppingCart.setImage(setmealVO.getImage());
            shoppingCart.setAmount(setmealVO.getPrice());
        }
        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());

        return shoppingCartMapper.insert(shoppingCart);
    }

    /**
     * 此方法用于：查看购物车
     *
     * @return List<ShoppingCart>
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        // 获取用户 id
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId)
                .build();

        return shoppingCartMapper.selectByShoppingCart(shoppingCart);
    }

    /**
     * 此方法用于：清空购物车
     *
     * @return int
     */
    @Override
    public int cleanShoppingCart() {
        // 获取用户 id
        Long userId = BaseContext.getCurrentId();
        return shoppingCartMapper.deleteByUserId(userId);
    }
}
