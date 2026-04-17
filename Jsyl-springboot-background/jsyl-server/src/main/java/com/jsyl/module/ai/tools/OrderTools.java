package com.jsyl.module.ai.tools;

import com.jsyl.common.context.BaseContext;
import com.jsyl.model.trade.entity.Order;
import com.jsyl.module.trade.service.OrderCenterService;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderTools {

    @Autowired
    private OrderCenterService orderCenterService;

    @Tool("查询当前用户发布的所有订单")
    public String getMyPublishedOrders() {
        Long userId = BaseContext.getCurrentId();
        List<Order> orders = orderCenterService.getMyPublishedOrders(userId);
        if (orders.isEmpty()) {
            return "你还没有发布过订单";
        }
        return orders.stream()
                .map(o -> String.format("订单ID:%d, 状态:%s, 描述:%s",
                        o.getId(), o.getOrderStatus(),o.getRequirement()))
                .collect(Collectors.joining("\n"));
    }
    @Tool("查询当前用户接取的所有订单")
    public String getMyAcceptedOrders() {
        Long userId = BaseContext.getCurrentId();
        List<Order> orders = orderCenterService.getMyAcceptedOrders(userId);
        if (orders.isEmpty()) {
            return "你还没有接取过订单";
        }
        return orders.stream()
                .map(o -> String.format("订单ID:%d, 状态:%s, 标题:%s",
                        o.getId(), o.getOrderStatus(),o.getRequirement()))
                .collect(Collectors.joining("\n"));
    }
}