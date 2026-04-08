package com.jsyl.module.admin.controller;

import com.jsyl.common.result.Result;
import com.jsyl.model.admin.entity.OperationLog;
import com.jsyl.module.admin.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/logs")
@Slf4j
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) String operatorName,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> result = operationLogService.list(operatorName, operationType, startTime, endTime, page, pageSize);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<OperationLog> getById(@PathVariable Long id) {
        OperationLog log = operationLogService.getById(id);
        return Result.success(log);
    }

    @DeleteMapping("/clear")
    public Result<String> clearLogs() {
        operationLogService.clearLogs();
        return Result.success("清空成功");
    }

    @DeleteMapping("/batch")
    public Result<String> batchDelete(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的日志");
        }
        operationLogService.batchDelete(ids);
        return Result.success("删除成功");
    }

    @GetMapping("/export")
    public Result<List<OperationLog>> exportLogs(
            @RequestParam(required = false) String operatorName,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        List<OperationLog> logs = operationLogService.exportLogs(operatorName, operationType, startTime, endTime);
        return Result.success(logs);
    }

}
