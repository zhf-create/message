package org.example.controller;

import org.example.pojo.OperationLog;
import org.example.pojo.Result;
import org.example.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/operation/log")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping("/recent")
    public Result getRecentLogs() {
        List<OperationLog> logs = operationLogService.getRecentLogs();
        return Result.success(logs);
    }
}