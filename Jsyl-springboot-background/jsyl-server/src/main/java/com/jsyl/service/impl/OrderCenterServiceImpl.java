package com.jsyl.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jsyl.constant.MessageConstant;
import com.jsyl.constant.StatusConstant;
import com.jsyl.dto.OrderPageQueryDTO;
import com.jsyl.dto.OrderPublishDTO;
import com.jsyl.entity.Order;
import com.jsyl.exception.OrderBusinessException;
import com.jsyl.mapper.OrderCenterMapper;
import com.jsyl.result.PageResult;
import com.jsyl.service.OrderCenterService;
import com.jsyl.vo.OrderDetailVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
public class OrderCenterServiceImpl implements OrderCenterService {

    @Autowired
    private OrderCenterMapper orderCenterMapper;

    public PageResult pageQuery(OrderPageQueryDTO orderPageQueryDTO) {
        PageHelper.startPage(orderPageQueryDTO.getPage(), orderPageQueryDTO.getPageSize());
        List<Order> orderList = orderCenterMapper.pageQuery(orderPageQueryDTO);
        Page<Order> page = (Page<Order>) orderList;
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void publish(OrderPublishDTO orderPublishDTO, Integer userId) {
        Order order = Order.builder()
                .orderNo(generateOrderNo())
                .typeId(orderPublishDTO.getTypeId())
                .serviceAddress(orderPublishDTO.getServiceAddress())
                .requirement(orderPublishDTO.getRequirement())
                .contactPhone(orderPublishDTO.getContactPhone())
                .orderAmount(orderPublishDTO.getOrderAmount())
                .orderStatus(StatusConstant.ORDER_STATUS_PENDING)
                .userId(userId)
                .createTime(LocalDateTime.now())
                .build();
        orderCenterMapper.insert(order);
    }

    @Override
    public void accept(Long orderId, Integer userId) {
        Order order = orderCenterMapper.getById(orderId);
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (!StatusConstant.ORDER_STATUS_PENDING.equals(order.getOrderStatus())) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        order.setAcceptorId(userId);
        order.setOrderStatus(StatusConstant.ORDER_STATUS_ACCEPTED);
        orderCenterMapper.update(order);
    }

    @Override
    public OrderDetailVO getDetail(Long id) {
        Order order = orderCenterMapper.getById(id);
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        BeanUtils.copyProperties(order, orderDetailVO);
        return orderDetailVO;
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
