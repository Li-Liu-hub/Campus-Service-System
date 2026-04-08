package com.jsyl.module.admin.controller;

import com.jsyl.common.result.Result;
import com.jsyl.module.admin.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/statistics")
@Slf4j
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取统计数据概览
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        return Result.success(statisticsService.getOverview());
    }

    /**
     * 获取最近7天数据趋势
     */
    @GetMapping("/trend")
    public Result<Map<String, List<Map<String, Object>>>> getTrend() {
        return Result.success(statisticsService.getTrend());
    }
}
