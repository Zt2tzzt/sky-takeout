package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    /**
     * 此方法用于：添加购物车
     *
     * @param shoppingCartDTO 购物车数据
     * @return int
     */
    int add(ShoppingCartDTO shoppingCartDTO);

    /**
     * 此方法用于：查看购物车
     *
     * @return List<ShoppingCart>
     */
    List<ShoppingCart> showShoppingCart();

    /**
     * 此方法用于：清空购物车
     *
     * @return int
     */
    int cleanShoppingCart();
}
