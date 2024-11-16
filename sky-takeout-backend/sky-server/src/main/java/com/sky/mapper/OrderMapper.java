package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 此方法用于：插入订单记录
     *
     * @param orders 订单实体类
     * @return int
     */
    int insert(Orders orders);

    /**
     * 此方法用于：根据订单号查询订单
     *
     * @param orderNumber 订单号
     */
    @Select("SELECT * FROM orders WHERE number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 此方法用于：修改订单信息
     *
     * @param orders 订单
     */
    int updateByIds(Orders orders);

    /**
     * 此方法用于：查询订单状态为 status 且下单时间小于 orderTime 的订单
     *
     * @param status    订单状态
     * @param orderTime 下单时间
     * @return Orders
     */
    @Select("SELECT * FROM orders WHERE status = #{status} AND order_time < #{orderTime}")
    List<Orders> selectByStatusAndOrderTimeLT(int status, LocalDateTime orderTime);

    /**
     * 此方法用于：根据id查询订单
     *
     * @param id id
     * @return Orders
     */
    @Select("SELECT * FROM orders WHERE id = #{id}")
    Orders selectById(Long id);

    /**
     * 此方法用于：统计订单金额
     * @param claim 查询条件
     * @return Double
     */
    Double sumByStatusAndOrderTime(HashMap<String, Object> claim);
}
