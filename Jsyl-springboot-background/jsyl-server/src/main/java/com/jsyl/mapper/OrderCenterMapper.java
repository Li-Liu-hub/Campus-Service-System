package com.jsyl.mapper;

import com.github.pagehelper.Page;

import com.jsyl.dto.OrderPageQueryDTO;
import com.jsyl.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderCenterMapper {

    /*分页查询*/
    List<Order> pageQuery(OrderPageQueryDTO orderPageQueryDTO);

    /*插入订单*/
    void insert(Order order);

    /*根据ID获取订单*/
    Order getById(Long id);

    /*更新订单*/
    void update(Order order);

    /*统计用户订单数*/
    @Select("SELECT COUNT(*) FROM order_info WHERE user_id = #{userId}")
    Long countByUserId(Integer userId);

    /*统计用户订单总金额*/
    @Select("SELECT COALESCE(SUM(order_amount), 0) FROM order_info WHERE user_id = #{userId}")
    Double sumAmountByUserId(Integer userId);

    /*统计用户帖子数*/
    @Select("SELECT COUNT(*) FROM post_info WHERE user_id = #{userId}")
    Long countPostsByUserId(Integer userId);

    /*统计用户接单数*/
    @Select("SELECT COUNT(*) FROM order_info WHERE acceptor_id = #{userId}")
    Long countAcceptedOrdersByUserId(Integer userId);

    /*查询我发起的订单*/
    @Select("SELECT o.*, u.nickname as publisherNickname, a.nickname as acceptorNickname " +
            "FROM order_info o " +
            "LEFT JOIN user_info u ON o.user_id = u.id " +
            "LEFT JOIN user_info a ON o.acceptor_id = a.id " +
            "WHERE o.user_id = #{userId} ORDER BY o.create_time DESC LIMIT #{limit}")
    List<Order> getMyPublishedOrders(@Param("userId") Integer userId, @Param("limit") Integer limit);

    /*查询我接受的订单*/
    @Select("SELECT o.*, u.nickname as publisherNickname, a.nickname as acceptorNickname " +
            "FROM order_info o " +
            "LEFT JOIN user_info u ON o.user_id = u.id " +
            "LEFT JOIN user_info a ON o.acceptor_id = a.id " +
            "WHERE o.acceptor_id = #{userId} ORDER BY o.create_time DESC LIMIT #{limit}")
    List<Order> getMyAcceptedOrders(@Param("userId") Integer userId, @Param("limit") Integer limit);

    /*查询用户发布的订单（用于交易记录）*/
    @Select("SELECT o.*, u.nickname as publisherNickname, a.nickname as acceptorNickname " +
            "FROM order_info o " +
            "LEFT JOIN user_info u ON o.user_id = u.id " +
            "LEFT JOIN user_info a ON o.acceptor_id = a.id " +
            "WHERE o.user_id = #{userId} AND o.order_status = 2 ORDER BY o.create_time DESC")
    List<Order> getByUserId(@Param("userId") Long userId);

    /*查询用户接受的订单（用于交易记录）*/
    @Select("SELECT o.*, u.nickname as publisherNickname, a.nickname as acceptorNickname " +
            "FROM order_info o " +
            "LEFT JOIN user_info u ON o.user_id = u.id " +
            "LEFT JOIN user_info a ON o.acceptor_id = a.id " +
            "WHERE o.acceptor_id = #{userId} AND o.order_status = 2 ORDER BY o.create_time DESC")
    List<Order> getByAcceptorId(@Param("userId") Integer userId);
}
