package org.example.service;

import org.example.pojo.OperationLog;

import java.util.List;

public interface OperationLogService {
    List<OperationLog> getRecentLogs();
    void insertLog(String operationType, String operationName, String operator);
}