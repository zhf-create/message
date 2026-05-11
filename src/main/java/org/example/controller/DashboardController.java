package org.example.controller;

import org.example.pojo.Result;
import org.example.service.CustomerRecordService;
import org.example.service.ScheduleService;
import org.example.service.SystemSettingService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private CustomerRecordService customerRecordService;

    @Autowired
    private SystemSettingService systemSettingService;

    @GetMapping("/stats")
    public Result getStats() {
        int userCount = userService.getAllUsers().size();
        int scheduleCount = scheduleService.getAllSchedules().size();
        int customerCount = customerRecordService.getAllRecords().size();
        int settingCount = systemSettingService.getAllSettings().size();

        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userCount);
        stats.put("scheduleCount", scheduleCount);
        stats.put("customerCount", customerCount);
        stats.put("settingCount", settingCount);

        return Result.success(stats);
    }

    @GetMapping("/weekly-schedule")
    public Result getWeeklySchedule() {
        List<Map<String, Object>> weeklyData = new ArrayList<>();
        String[] days = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        
        LocalDate today = LocalDate.now();
        LocalDate monday = today.minusDays(today.getDayOfWeek().getValue() - 1);

        Random random = new Random();
        for (int i = 0; i < 7; i++) {
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("day", days[i]);
            dayData.put("value", random.nextInt(20) + 5);
            weeklyData.add(dayData);
        }

        return Result.success(weeklyData);
    }

    @GetMapping("/service-distribution")
    public Result getServiceDistribution() {
        List<Map<String, Object>> serviceData = new ArrayList<>();
        
        Map<String, Object> item1 = new HashMap<>();
        item1.put("name", "用户管理");
        item1.put("value", userService.getAllUsers().size() * 10);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("name", "排班管理");
        item2.put("value", scheduleService.getAllSchedules().size() * 15);

        Map<String, Object> item3 = new HashMap<>();
        item3.put("name", "客户档案");
        item3.put("value", customerRecordService.getAllRecords().size() * 8);

        serviceData.add(item1);
        serviceData.add(item2);
        serviceData.add(item3);

        return Result.success(serviceData);
    }
}