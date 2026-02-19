package com.jsyl.controller;

import com.jsyl.constant.MessageConstant;
import com.jsyl.context.BaseContext;
import com.jsyl.dto.OrderAcceptDTO;
import com.jsyl.dto.OrderPageQueryDTO;
import com.jsyl.dto.OrderPublishDTO;
import com.jsyl.result.PageResult;
import com.jsyl.result.Result;
import com.jsyl.service.OrderCenterService;
import com.jsyl.vo.OrderDetailVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/jsyl/home/orderCenter")
@Api(tags = "订单相关接口")
public class OrderCenterController {
    @Autowired
    private OrderCenterService orderCenterService;

    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Result<PageResult> page(OrderPageQueryDTO orderPageQueryDTO) {
        log.info("分页查询：{}", orderPageQueryDTO);
        PageResult pageResult = orderCenterService.pageQuery(orderPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/publish")
    @ApiOperation("发布订单")
    public Result<String> publish(@RequestBody OrderPublishDTO orderPublishDTO) {
        log.info("发布订单：{}", orderPublishDTO);
        Integer userId = 2;
        try {
            userId = BaseContext.getCurrentId().intValue();
        } catch (Exception e) {
            log.warn("使用默认用户ID 2");
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
}
