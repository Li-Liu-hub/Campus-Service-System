package com.jsyl.module.trade.controller;

import com.jsyl.common.constant.MessageConstant;
import com.jsyl.common.context.BaseContext;
import com.jsyl.common.utils.UserContextUtil;
import com.jsyl.model.trade.dto.OrderPageQueryDTO;
import com.jsyl.model.trade.dto.OrderPublishDTO;
import com.jsyl.model.trade.entity.Order;
import com.jsyl.model.user.entity.User;
import com.jsyl.common.result.PageResult;
import com.jsyl.common.result.Result;
import com.jsyl.module.trade.service.OrderCenterService;
import com.jsyl.module.user.service.UserService;
import com.jsyl.model.trade.vo.OrderDetailVO;
import com.jsyl.model.user.vo.UserStatisticsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/jsyl/home/orderCenter")
public class OrderCenterController {

    @Autowired
    private OrderCenterService orderCenterService;

    @Autowired
    private UserService userService;

    @GetMapping("/page")
    public Result<PageResult> page(OrderPageQueryDTO orderPageQueryDTO) {
        Long userId = BaseContext.getCurrentId();
        if (userId != null) {
            User user = userService.getUserById(userId.intValue());
            if (user != null) {
                Integer role = user.getRole();
                if (role != null && role < 3) {
                    if (user.getCampusId() != null) {
                        orderPageQueryDTO.setCampusId(user.getCampusId());
                    }
                }
            }
        }
        PageResult pageResult = orderCenterService.pageQuery(orderPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/publish")
    public Result<String> publish(@RequestBody OrderPublishDTO orderPublishDTO) {
        Integer userId = UserContextUtil.getCurrentUserId();
        orderCenterService.publish(orderPublishDTO, userId);
        return Result.success(MessageConstant.ORDER_PUBLISHED_SUCCESS);
    }

    @PostMapping("/accept/{orderId}")
    public Result<String> accept(@PathVariable Long orderId) {
        Integer userId = UserContextUtil.getCurrentUserId();
        orderCenterService.accept(orderId, userId);
        return Result.success(MessageConstant.ORDER_ACCEPTED_SUCCESS);
    }

    @GetMapping("/detail/{id}")
    public Result<OrderDetailVO> getDetail(@PathVariable Long id) {
        OrderDetailVO orderDetailVO = orderCenterService.getDetail(id);
        return Result.success(orderDetailVO);
    }

    @PutMapping("/cancel/{orderId}")
    public Result<String> cancel(@PathVariable Long orderId) {
        orderCenterService.cancelOrder(orderId);
        return Result.success(MessageConstant.ORDER_CANCELLED_SUCCESS);
    }

    @PutMapping("/cancelByUser/{orderId}")
    public Result<String> cancelByUser(@PathVariable Long orderId) {
        Integer userId = UserContextUtil.getCurrentUserId();
        orderCenterService.cancelByUser(orderId, userId);
        return Result.success(MessageConstant.ORDER_CANCELLED_SUCCESS);
    }

    @PutMapping("/complete/{orderId}")
    public Result<String> complete(@PathVariable Long orderId) {
        Integer userId = UserContextUtil.getCurrentUserId();
        orderCenterService.completeByUser(orderId, userId);
        return Result.success(MessageConstant.ORDER_COMPLETED_SUCCESS);
    }

    @GetMapping("/statistics")
    public Result<UserStatisticsVO> getUserStatistics() {
        Long userId = UserContextUtil.getCurrentUserIdLong();
        UserStatisticsVO statistics = orderCenterService.getUserStatistics(userId);
        return Result.success(statistics);
    }

    @GetMapping("/myPublished")
    public Result<List<Order>> getMyPublishedOrders() {
        Long userId = UserContextUtil.getCurrentUserIdLong();
        List<Order> orders = orderCenterService.getMyPublishedOrders(userId);
        return Result.success(orders);
    }

    @GetMapping("/myAccepted")
    public Result<List<Order>> getMyAcceptedOrders() {
        Long userId = UserContextUtil.getCurrentUserIdLong();
        List<Order> orders = orderCenterService.getMyAcceptedOrders(userId);
        return Result.success(orders);
    }

    @GetMapping("/transactions")
    public Result<List<Order>> getTransactionHistory() {
        Long userId = UserContextUtil.getCurrentUserIdLong();
        List<Order> transactions = orderCenterService.getTransactionHistory(userId);
        return Result.success(transactions);
    }
}
