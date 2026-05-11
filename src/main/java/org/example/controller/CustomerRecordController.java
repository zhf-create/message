package org.example.controller;

import org.example.pojo.CustomerRecord;
import org.example.pojo.Result;
import org.example.service.CustomerRecordService;
import org.example.service.OperationLogService;
import org.example.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customer/record")
public class CustomerRecordController {

    @Autowired
    private CustomerRecordService customerRecordService;

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping
    public Result getAllRecords() {
        List<CustomerRecord> records = customerRecordService.getAllRecords();
        return Result.success(records);
    }

    @GetMapping("/{id}")
    public Result getRecordById(@PathVariable Integer id) {
        CustomerRecord record = customerRecordService.getRecordById(id);
        if (record != null) {
            return Result.success(record);
        }
        return Result.error("档案不存在");
    }

    @GetMapping("/phone/{phone}")
    public Result getRecordByPhone(@PathVariable String phone) {
        CustomerRecord record = customerRecordService.getRecordByPhone(phone);
        if (record != null) {
            return Result.success(record);
        }
        return Result.error("档案不存在");
    }

    @PostMapping
    public Result insertRecord(@RequestBody CustomerRecord record) {
        customerRecordService.insertRecord(record);
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            String operator = claims != null ? (String) claims.get("username") : "system";
            operationLogService.insertLog("customer", "添加客户: " + record.getName(), operator);
        } catch (Exception e) {}
        return Result.success();
    }

    @PutMapping
    public Result updateRecord(@RequestBody CustomerRecord record) {
        customerRecordService.updateRecord(record);
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            String operator = claims != null ? (String) claims.get("username") : "system";
            operationLogService.insertLog("customer", "修改客户档案: " + record.getName(), operator);
        } catch (Exception e) {}
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result deleteRecord(@PathVariable Integer id) {
        CustomerRecord record = customerRecordService.getRecordById(id);
        String name = record != null ? record.getName() : "未知客户";
        customerRecordService.deleteRecord(id);
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            String operator = claims != null ? (String) claims.get("username") : "system";
            operationLogService.insertLog("customer", "删除客户档案: " + name, operator);
        } catch (Exception e) {}
        return Result.success();
    }
}