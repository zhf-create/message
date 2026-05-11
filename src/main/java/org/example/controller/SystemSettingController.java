package org.example.controller;

import org.example.pojo.Result;
import org.example.pojo.SystemSetting;
import org.example.service.OperationLogService;
import org.example.service.SystemSettingService;
import org.example.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/setting")
public class SystemSettingController {

    @Autowired
    private SystemSettingService systemSettingService;

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping
    public Result getAllSettings() {
        List<SystemSetting> settings = systemSettingService.getAllSettings();
        return Result.success(settings);
    }

    @GetMapping("/{id}")
    public Result getSettingById(@PathVariable Integer id) {
        SystemSetting setting = systemSettingService.getSettingById(id);
        if (setting != null) {
            return Result.success(setting);
        }
        return Result.error("设置不存在");
    }

    @GetMapping("/key/{settingKey}")
    public Result getSettingByKey(@PathVariable String settingKey) {
        SystemSetting setting = systemSettingService.getSettingByKey(settingKey);
        if (setting != null) {
            return Result.success(setting);
        }
        return Result.error("设置不存在");
    }

    @PostMapping
    public Result insertSetting(@RequestBody SystemSetting setting) {
        systemSettingService.insertSetting(setting);
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            String operator = claims != null ? (String) claims.get("username") : "system";
            operationLogService.insertLog("setting", "添加设置: " + setting.getSettingKey(), operator);
        } catch (Exception e) {}
        return Result.success();
    }

    @PutMapping
    public Result updateSetting(@RequestBody SystemSetting setting) {
        systemSettingService.updateSetting(setting);
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            String operator = claims != null ? (String) claims.get("username") : "system";
            operationLogService.insertLog("setting", "修改系统设置: " + setting.getSettingKey(), operator);
        } catch (Exception e) {}
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result deleteSetting(@PathVariable Integer id) {
        SystemSetting setting = systemSettingService.getSettingById(id);
        String key = setting != null ? setting.getSettingKey() : "未知设置";
        systemSettingService.deleteSetting(id);
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            String operator = claims != null ? (String) claims.get("username") : "system";
            operationLogService.insertLog("setting", "删除系统设置: " + key, operator);
        } catch (Exception e) {}
        return Result.success();
    }
}