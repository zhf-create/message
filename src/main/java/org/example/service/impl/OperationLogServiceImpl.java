package org.example.service.impl;

import org.example.mapper.OperationLogMapper;
import org.example.pojo.OperationLog;
import org.example.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public List<OperationLog> getRecentLogs() {
        return operationLogMapper.getRecentLogs();
    }

    @Override
    public void insertLog(String operationType, String operationName, String operator) {
        OperationLog log = new OperationLog();
        log.setOperationType(operationType);
        log.setOperationName(operationName);
        log.setOperator(operator);
        operationLogMapper.insertLog(log);
    }
}