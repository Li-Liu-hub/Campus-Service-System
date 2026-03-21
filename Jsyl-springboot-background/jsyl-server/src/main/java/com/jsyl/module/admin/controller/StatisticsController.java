package com.jsyl.module.admin.controller;

import com.jsyl.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin/statistics")
@Api(tags = "管理端统计")
@Slf4j
public class StatisticsController {

    @Autowired
    private com.jsyl.module.trade.mapper.OrderCenterMapper orderCenterMapper;

    @Autowired
    private com.jsyl.module.user.mapper.UserMapper userMapper;

    @Autowired
    private com.jsyl.module.forum.mapper.PostMapper postMapper;

    /**
     * 获取统计数据概览
     */
    @GetMapping("/overview")
    @ApiOperation("获取统计概览")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> result = new HashMap<>();

        // 用户总数
        Long userCount = userMapper.countAll();
        result.put("userCount", userCount != null ? userCount : 0);

        // 订单总数
        Long orderCount = orderCenterMapper.countAll();
        result.put("orderCount", orderCount != null ? orderCount : 0);

        // 帖子总数
        Long postCount = postMapper.countAll();
        result.put("postCount", postCount != null ? postCount : 0);

        // 交易总金额（已完成订单）
        Double totalAmount = orderCenterMapper.sumCompletedAmount();
        result.put("totalAmount", totalAmount != null ? totalAmount : 0);

        return Result.success(result);
    }

    /**
     * 获取最近7天数据趋势
     */
    @GetMapping("/trend")
    @ApiOperation("获取最近7天数据趋势")
    public Result<Map<String, List<Map<String, Object>>>> getTrend() {
        Map<String, List<Map<String, Object>>> result = new HashMap<>();

        List<Map<String, Object>> userTrend = new ArrayList<>();
        List<Map<String, Object>> orderTrend = new ArrayList<>();
        List<Map<String, Object>> postTrend = new ArrayList<>();
        List<Map<String, Object>> amountTrend = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();

        for (int i = 6; i >= 0; i--) {
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH, -i);
            Date date = calendar.getTime();

            String dateStr = String.format("%d/%02d",
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));

            // 用户数据（新增用户模拟数据，实际应查询用户创建时间）
            Map<String, Object> userData = new HashMap<>();
            userData.put("date", dateStr);
            userData.put("value", (int)(Math.random() * 20) + 5);
            userTrend.add(userData);

            // 订单数据
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("date", dateStr);
            orderData.put("value", (int)(Math.random() * 30) + 10);
            orderTrend.add(orderData);

            // 帖子数据
            Map<String, Object> postData = new HashMap<>();
            postData.put("date", dateStr);
            postData.put("value", (int)(Math.random() * 15) + 5);
            postTrend.add(postData);

            // 交易金额数据
            Map<String, Object> amountData = new HashMap<>();
            amountData.put("date", dateStr);
            amountData.put("value", (int)(Math.random() * 5000) + 1000);
            amountTrend.add(amountData);
        }

        result.put("userTrend", userTrend);
        result.put("orderTrend", orderTrend);
        result.put("postTrend", postTrend);
        result.put("amountTrend", amountTrend);

        return Result.success(result);
    }
}
