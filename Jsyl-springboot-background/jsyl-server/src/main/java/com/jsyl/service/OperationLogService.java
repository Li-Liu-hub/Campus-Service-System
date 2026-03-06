package com.jsyl.service;

import com.jsyl.entity.OperationLog;

import java.util.List;
import java.util.Map;

public interface OperationLogService {

    void addLog(OperationLog log);

    Map<String, Object> list(String operatorName, String operationType, String startTime, String endTime, int page, int pageSize);

    OperationLog getById(Long id);

    void clearLogs();

    void batchDelete(List<Long> ids);

    List<OperationLog> exportLogs(String operatorName, String operationType, String startTime, String endTime);

}
