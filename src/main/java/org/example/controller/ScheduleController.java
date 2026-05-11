package org.example.controller;

import org.example.pojo.Result;
import org.example.pojo.Schedule;
import org.example.service.OperationLogService;
import org.example.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import org.example.util.ThreadLocalUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping
    public Result getAllSchedules() {
        List<Schedule> schedules = scheduleService.getAllSchedules();
        return Result.success(schedules);
    }

    @GetMapping("/{id}")
    public Result getScheduleById(@PathVariable Integer id) {
        Schedule schedule = scheduleService.getScheduleById(id);
        if (schedule != null) {
            return Result.success(schedule);
        }
        return Result.error("排班不存在");
    }

    @GetMapping("/staff/{staffId}")
    public Result getSchedulesByStaffId(@PathVariable Integer staffId) {
        List<Schedule> schedules = scheduleService.getSchedulesByStaffId(staffId);
        return Result.success(schedules);
    }

    @GetMapping("/date/{date}")
    public Result getSchedulesByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Schedule> schedules = scheduleService.getSchedulesByDate(date);
        return Result.success(schedules);
    }

    @PostMapping
    public Result insertSchedule(@RequestBody Schedule schedule) {
        scheduleService.insertSchedule(schedule);
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            String operator = claims != null ? (String) claims.get("username") : "system";
            operationLogService.insertLog("schedule", "添加排班: " + schedule.getStaffName(), operator);
        } catch (Exception e) {}
        return Result.success();
    }

    @PutMapping
    public Result updateSchedule(@RequestBody Schedule schedule) {
        scheduleService.updateSchedule(schedule);
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            String operator = claims != null ? (String) claims.get("username") : "system";
            operationLogService.insertLog("schedule", "修改排班: " + schedule.getStaffName(), operator);
        } catch (Exception e) {}
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result deleteSchedule(@PathVariable Integer id) {
        Schedule schedule = scheduleService.getScheduleById(id);
        String staffName = schedule != null ? schedule.getStaffName() : "未知排班";
        scheduleService.deleteSchedule(id);
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            String operator = claims != null ? (String) claims.get("username") : "system";
            operationLogService.insertLog("schedule", "删除排班: " + staffName, operator);
        } catch (Exception e) {}
        return Result.success();
    }
}