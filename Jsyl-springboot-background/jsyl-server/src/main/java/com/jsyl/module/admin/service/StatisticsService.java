package com.jsyl.module.admin.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {

    /**
     * 获取统计数据概览
     */
    Map<String, Object> getOverview();

    /**
     * 获取最近7天数据趋势
     */
    Map<String, List<Map<String, Object>>> getTrend();
}
