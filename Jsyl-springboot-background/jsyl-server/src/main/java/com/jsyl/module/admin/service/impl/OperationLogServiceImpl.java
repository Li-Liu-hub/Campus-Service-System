package com.jsyl.module.admin.service.impl;

import com.jsyl.model.admin.entity.OperationLog;
import com.jsyl.module.admin.mapper.OperationLogMapper;
import com.jsyl.module.admin.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public void addLog(OperationLog log) {
        operationLogMapper.insert(log);
    }

    @Override
    public Map<String, Object> list(String operatorName, String operationType, String startTime, String endTime, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<OperationLog> records = operationLogMapper.list(operatorName, operationType, startTime, endTime, offset, pageSize);
        int total = operationLogMapper.count(operatorName, operationType, startTime, endTime);

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }

    @Override
    public OperationLog getById(Long id) {
        return operationLogMapper.getById(id);
    }

    @Override
    public void clearLogs() {
        operationLogMapper.deleteAll();
    }

    @Override
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        String idsStr = ids.stream()
                .map(String::valueOf)
                .reduce((a, b) -> a + "," + b)
                .orElse("");
        operationLogMapper.batchDelete(idsStr);
    }

    @Override
    public List<OperationLog> exportLogs(String operatorName, String operationType, String startTime, String endTime) {
        // 导出时不分页，最多导出10000条
        return operationLogMapper.list(operatorName, operationType, startTime, endTime, 0, 10000);
    }

}
