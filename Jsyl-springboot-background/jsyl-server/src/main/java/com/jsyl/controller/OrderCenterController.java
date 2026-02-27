package com.jsyl.controller;

import com.jsyl.constant.MessageConstant;
import com.jsyl.context.BaseContext;
import com.jsyl.dto.OrderAcceptDTO;
import com.jsyl.dto.OrderPageQueryDTO;
import com.jsyl.dto.OrderPublishDTO;
import com.jsyl.entity.Order;
import com.jsyl.entity.User;
import com.jsyl.result.PageResult;
import com.jsyl.result.Result;
import com.jsyl.service.OrderCenterService;
import com.jsyl.service.UserService;
import com.jsyl.vo.OrderDetailVO;
import com.jsyl.vo.UserStatisticsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/jsyl/home/orderCenter")
@Api(tags = "订单相关接口")
public class OrderCenterController {

    @Autowired
    private OrderCenterService orderCenterService;

    @Autowired
    private UserService userService;

    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Result<PageResult> page(OrderPageQueryDTO orderPageQueryDTO) {
        log.info("分页查询：{}", orderPageQueryDTO);
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
    @ApiOperation("发布订单")
    public Result<String> publish(@RequestBody OrderPublishDTO orderPublishDTO) {
        log.info("发布订单：{}", orderPublishDTO);
        Long userIdLong = BaseContext.getCurrentId();
        if (userIdLong == null) {
            return Result.error("用户未登录");
        }
        Integer userId = userIdLong.intValue();
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (user.getPermission() != null && user.getPermission() == 0) {
            return Result.error("您的账号已被禁用，无法发布订单");
        }
        if (user.getCampusId() != null) {
            orderPublishDTO.setCampusId(user.getCampusId());
        }
        orderCenterService.publish(orderPublishDTO, userId);
        return Result.success(MessageConstant.ORDER_PUBLISHED_SUCCESS);
    }

    @PostMapping("/accept/{orderId}")
    @ApiOperation("接单")
    public Result<String> accept(@PathVariable Long orderId) {
        log.info("接单：orderId={}", orderId);
        Integer userId = 2;
        try {
            userId = BaseContext.getCurrentId().intValue();
        } catch (Exception e) {
            log.warn("使用默认用户ID 2");
        }
        orderCenterService.accept(orderId, userId);
        return Result.success(MessageConstant.ORDER_ACCEPTED_SUCCESS);
    }

    @GetMapping("/detail/{id}")
    @ApiOperation("获取订单详情")
    public Result<OrderDetailVO> getDetail(@PathVariable Long id) {
        log.info("获取订单详情：id={}", id);
        OrderDetailVO orderDetailVO = orderCenterService.getDetail(id);
        return Result.success(orderDetailVO);
    }

    @PutMapping("/cancel/{orderId}")
    @ApiOperation("取消订单（管理员）")
    public Result<String> cancel(@PathVariable Long orderId) {
        log.info("管理员取消订单：orderId={}", orderId);
        orderCenterService.cancelOrder(orderId);
        return Result.success(MessageConstant.ORDER_CANCELLED_SUCCESS);
    }

    @PutMapping("/cancelByUser/{orderId}")
    @ApiOperation("用户放弃订单")
    public Result<String> cancelByUser(@PathVariable Long orderId) {
        Long userIdLong = BaseContext.getCurrentId();
        if (userIdLong == null) {
            return Result.error("用户未登录");
        }
        Integer userId = userIdLong.intValue();
        log.info("用户放弃订单：orderId={}, userId={}", orderId, userId);
        orderCenterService.cancelByUser(orderId, userId);
        return Result.success(MessageConstant.ORDER_CANCELLED_SUCCESS);
    }

    @PutMapping("/complete/{orderId}")
    @ApiOperation("用户完成订单")
    public Result<String> complete(@PathVariable Long orderId) {
        Long userIdLong = BaseContext.getCurrentId();
        if (userIdLong == null) {
            return Result.error("用户未登录");
        }
        Integer userId = userIdLong.intValue();
        log.info("用户完成订单：orderId={}, userId={}", orderId, userId);
        orderCenterService.completeByUser(orderId, userId);
        return Result.success(MessageConstant.ORDER_COMPLETED_SUCCESS);
    }

    @GetMapping("/statistics")
    @ApiOperation("获取用户统计数据")
    public Result<UserStatisticsVO> getUserStatistics() {
        Long userId = BaseContext.getCurrentId();
        log.info("获取用户统计数据：userId={}", userId);
        UserStatisticsVO statistics = orderCenterService.getUserStatistics(userId);
        return Result.success(statistics);
    }

    @GetMapping("/myPublished")
    @ApiOperation("获取我发起的订单")
    public Result<List<Order>> getMyPublishedOrders() {
        Long userId = BaseContext.getCurrentId();
        log.info("获取我发起的订单：userId={}", userId);
        List<Order> orders = orderCenterService.getMyPublishedOrders(userId);
        return Result.success(orders);
    }

    @GetMapping("/myAccepted")
    @ApiOperation("获取我接受的订单")
    public Result<List<Order>> getMyAcceptedOrders() {
        Long userId = BaseContext.getCurrentId();
        log.info("获取我接受的订单：userId={}", userId);
        List<Order> orders = orderCenterService.getMyAcceptedOrders(userId);
        return Result.success(orders);
    }

    @GetMapping("/transactions")
    @ApiOperation("获取交易记录")
    public Result<List<Order>> getTransactionHistory() {
        Long userId = BaseContext.getCurrentId();
        log.info("获取交易记录：userId={}", userId);
        List<Order> transactions = orderCenterService.getTransactionHistory(userId);
        return Result.success(transactions);
    }
}
