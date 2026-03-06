package com.jsyl.controller.admin;

import com.jsyl.entity.OperationLog;
import com.jsyl.result.Result;
import com.jsyl.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/logs")
@Api(tags = "操作日志管理")
@Slf4j
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping("/list")
    @ApiOperation("获取操作日志列表")
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
    @ApiOperation("获取日志详情")
    public Result<OperationLog> getById(@PathVariable Long id) {
        OperationLog log = operationLogService.getById(id);
        return Result.success(log);
    }

    @DeleteMapping("/clear")
    @ApiOperation("清空操作日志")
    public Result<String> clearLogs() {
        operationLogService.clearLogs();
        return Result.success("清空成功");
    }

    @DeleteMapping("/batch")
    @ApiOperation("批量删除操作日志")
    public Result<String> batchDelete(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的日志");
        }
        operationLogService.batchDelete(ids);
        return Result.success("删除成功");
    }

    @GetMapping("/export")
    @ApiOperation("导出操作日志")
    public Result<List<OperationLog>> exportLogs(
            @RequestParam(required = false) String operatorName,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        List<OperationLog> logs = operationLogService.exportLogs(operatorName, operationType, startTime, endTime);
        return Result.success(logs);
    }

}
