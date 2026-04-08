package com.jsyl.module.admin.service.impl;

import com.jsyl.module.admin.service.StatisticsService;
import com.jsyl.module.forum.mapper.PostMapper;
import com.jsyl.module.trade.mapper.OrderCenterMapper;
import com.jsyl.module.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private OrderCenterMapper orderCenterMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostMapper postMapper;

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> result = new HashMap<>();

        Long userCount = userMapper.countAll();
        result.put("userCount", userCount != null ? userCount : 0);

        Long orderCount = orderCenterMapper.countAll();
        result.put("orderCount", orderCount != null ? orderCount : 0);

        Long postCount = postMapper.countAll();
        result.put("postCount", postCount != null ? postCount : 0);

        Double totalAmount = orderCenterMapper.sumCompletedAmount();
        result.put("totalAmount", totalAmount != null ? totalAmount : 0);

        return result;
    }

    @Override
    public Map<String, List<Map<String, Object>>> getTrend() {
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

            Map<String, Object> userData = new HashMap<>();
            userData.put("date", dateStr);
            userData.put("value", (int)(Math.random() * 20) + 5);
            userTrend.add(userData);

            Map<String, Object> orderData = new HashMap<>();
            orderData.put("date", dateStr);
            orderData.put("value", (int)(Math.random() * 30) + 10);
            orderTrend.add(orderData);

            Map<String, Object> postData = new HashMap<>();
            postData.put("date", dateStr);
            postData.put("value", (int)(Math.random() * 15) + 5);
            postTrend.add(postData);

            Map<String, Object> amountData = new HashMap<>();
            amountData.put("date", dateStr);
            amountData.put("value", (int)(Math.random() * 5000) + 1000);
            amountTrend.add(amountData);
        }

        result.put("userTrend", userTrend);
        result.put("orderTrend", orderTrend);
        result.put("postTrend", postTrend);
        result.put("amountTrend", amountTrend);

        return result;
    }
}
