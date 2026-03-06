package com.jsyl.service;

import com.jsyl.dto.OrderPageQueryDTO;
import com.jsyl.dto.OrderPublishDTO;
import com.jsyl.entity.Order;
import com.jsyl.result.PageResult;
import com.jsyl.vo.OrderDetailVO;
import com.jsyl.vo.UserStatisticsVO;

import java.util.List;

public interface OrderCenterService {

    /*订单中心分页查询*/
    PageResult pageQuery(OrderPageQueryDTO orderPageQueryDTO);

    /*发布订单*/
    void publish(OrderPublishDTO orderPublishDTO, Integer userId);

    /*接单*/
    void accept(Long orderId, Integer userId);

    /*获取订单详情*/
    OrderDetailVO getDetail(Long id);

    /*取消订单（管理员）*/
    void cancelOrder(Long orderId);

    /*获取用户统计数据*/
    UserStatisticsVO getUserStatistics(Long userId);

    /*获取我发起的订单*/
    List<Order> getMyPublishedOrders(Long userId);

    /*获取我接受的订单*/
    List<Order> getMyAcceptedOrders(Long userId);

    /*用户放弃订单*/
    void cancelByUser(Long orderId, Integer userId);

    /*用户完成订单*/
    void completeByUser(Long orderId, Integer userId);

    /*获取交易记录*/
    List<Order> getTransactionHistory(Long userId);
}
