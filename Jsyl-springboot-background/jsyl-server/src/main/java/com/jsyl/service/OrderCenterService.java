package com.jsyl.service;

import com.jsyl.dto.OrderPageQueryDTO;
import com.jsyl.dto.OrderPublishDTO;
import com.jsyl.result.PageResult;
import com.jsyl.vo.OrderDetailVO;

public interface OrderCenterService {

    /*订单中心分页查询*/
    PageResult pageQuery(OrderPageQueryDTO orderPageQueryDTO);

    /*发布订单*/
    void publish(OrderPublishDTO orderPublishDTO, Integer userId);

    /*接单*/
    void accept(Long orderId, Integer userId);

    /*获取订单详情*/
    OrderDetailVO getDetail(Long id);
}
