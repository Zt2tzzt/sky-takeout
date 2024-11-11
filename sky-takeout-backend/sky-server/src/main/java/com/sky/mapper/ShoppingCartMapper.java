package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    /**
     * 此方法用于：根据用户 id ，套餐 id；或者用户 id，菜品 id查询购物车
     *
     * @param shoppingCart 查询条件
     * @return List<ShoppingCart>
     */
    List<ShoppingCart> selectByShoppingCart(ShoppingCart shoppingCart);

    /**
     * 此方法用于：添加购物车
     *
     * @param shoppingCart 要修改的购物车
     * @return int
     */
    @Update("UPDATE shopping_cart SET number = #{number} WHERE id = #{id}")
    int updateById(ShoppingCart shoppingCart);

    /**
     * 此方法用于：添加购物车记录
     *
     * @param shoppingCart 要添加的购物车记录
     * @return int
     */
    @Insert("INSERT INTO shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time) VALUES (#{name}, #{image}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{createTime})")
    int insert(ShoppingCart shoppingCart);

    /**
     * 此方法用于：根据用户 id 删除购物车记录
     *
     * @param userId 用户
     */
    @Delete("DELETE FROM shopping_cart WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);
}
