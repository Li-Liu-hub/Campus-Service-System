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
import com.jsyl.vo.UserStatisticsVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
                .campusId(orderPublishDTO.getCampusId())
                .requireTime(orderPublishDTO.getRequireTime())
                .build();
        orderCenterMapper.insert(order);
    }

    @Override
    public void accept(Long orderId, Integer userId) {
        Order order = orderCenterMapper.getById(orderId);
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (order.getUserId().equals(userId)) {
            throw new OrderBusinessException("不能接取自己的订单");
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

    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderCenterMapper.getById(orderId);
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (StatusConstant.ORDER_STATUS_COMPLETED.equals(order.getOrderStatus())) {
            throw new OrderBusinessException("订单已完成，无法取消");
        }
        if (StatusConstant.ORDER_STATUS_CANCELLED.equals(order.getOrderStatus())) {
            throw new OrderBusinessException("订单已取消");
        }
        order.setOrderStatus(StatusConstant.ORDER_STATUS_CANCELLED);
        orderCenterMapper.update(order);
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Override
    public UserStatisticsVO getUserStatistics(Long userId) {
        Integer userIdInt = userId.intValue();
        Long orderCount = orderCenterMapper.countByUserId(userIdInt);
        Double totalAmount = orderCenterMapper.sumAmountByUserId(userIdInt);
        Long postCount = orderCenterMapper.countPostsByUserId(userIdInt);
        Long acceptedOrderCount = orderCenterMapper.countAcceptedOrdersByUserId(userIdInt);

        return UserStatisticsVO.builder()
                .orderCount(orderCount)
                .totalAmount(totalAmount)
                .postCount(postCount)
                .acceptedOrderCount(acceptedOrderCount)
                .build();
    }

    @Override
    public List<Order> getMyPublishedOrders(Long userId) {
        return orderCenterMapper.getMyPublishedOrders(userId.intValue(), 5);
    }

    @Override
    public List<Order> getMyAcceptedOrders(Long userId) {
        return orderCenterMapper.getMyAcceptedOrders(userId.intValue(), 5);
    }

    @Override
    public void cancelByUser(Long orderId, Integer userId) {
        Order order = orderCenterMapper.getById(orderId);
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (!order.getUserId().equals(userId) && !order.getAcceptorId().equals(userId)) {
            throw new OrderBusinessException("您无权取消此订单");
        }
        if (order.getOrderStatus() == StatusConstant.ORDER_STATUS_COMPLETED) {
            throw new OrderBusinessException("订单已完成，无法取消");
        }
        if (order.getOrderStatus() == StatusConstant.ORDER_STATUS_CANCELLED) {
            throw new OrderBusinessException("订单已取消");
        }
        if (order.getOrderStatus() != StatusConstant.ORDER_STATUS_ACCEPTED
                && order.getOrderStatus() != StatusConstant.ORDER_STATUS_PENDING) {
            throw new OrderBusinessException("当前订单状态无法取消");
        }

        if (order.getUserId().equals(userId)) {
            if (order.getPublisherCancel() != null && order.getPublisherCancel() == 1) {
                throw new OrderBusinessException("您已申请过取消");
            }
            order.setPublisherCancel(1);
            if (order.getAcceptorId() != null && order.getAcceptorCancel() != null && order.getAcceptorCancel() == 1) {
                order.setOrderStatus(StatusConstant.ORDER_STATUS_CANCELLED);
            }
        } else if (order.getAcceptorId() != null && order.getAcceptorId().equals(userId)) {
            if (order.getAcceptorCancel() != null && order.getAcceptorCancel() == 1) {
                throw new OrderBusinessException("您已申请过取消");
            }
            order.setAcceptorCancel(1);
            if (order.getPublisherCancel() != null && order.getPublisherCancel() == 1) {
                order.setOrderStatus(StatusConstant.ORDER_STATUS_CANCELLED);
            }
        }

        orderCenterMapper.update(order);
    }

    @Override
    public void completeByUser(Long orderId, Integer userId) {
        Order order = orderCenterMapper.getById(orderId);
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (!order.getUserId().equals(userId) && !order.getAcceptorId().equals(userId)) {
            throw new OrderBusinessException("您无权完成此订单");
        }
        if (order.getOrderStatus() != StatusConstant.ORDER_STATUS_ACCEPTED) {
            throw new OrderBusinessException("只有已接单的订单才能完成");
        }
        order.setOrderStatus(StatusConstant.ORDER_STATUS_COMPLETED);
        order.setCompleteTime(LocalDateTime.now());
        orderCenterMapper.update(order);
    }

    @Override
    public List<Order> getTransactionHistory(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        List<Order> publishedOrders = orderCenterMapper.getByUserId(userId);
        List<Order> acceptedOrders = orderCenterMapper.getByAcceptorId(userId.intValue());

        List<Order> allTransactions = new ArrayList<>();
        if (publishedOrders != null) {
            for (Order order : publishedOrders) {
                order.setOrderNo("支出-" + order.getOrderNo());
            }
            allTransactions.addAll(publishedOrders);
        }
        if (acceptedOrders != null) {
            for (Order order : acceptedOrders) {
                order.setOrderNo("收入-" + order.getOrderNo());
            }
            allTransactions.addAll(acceptedOrders);
        }

        allTransactions.sort((a, b) -> {
            if (a.getCreateTime() == null || b.getCreateTime() == null) {
                return 0;
            }
            return b.getCreateTime().compareTo(a.getCreateTime());
        });

        return allTransactions;
    }
}
