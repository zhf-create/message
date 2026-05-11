package org.example.service.impl;

import org.example.mapper.ScheduleMapper;
import org.example.pojo.Schedule;
import org.example.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Override
    public List<Schedule> getAllSchedules() {
        return scheduleMapper.getAllSchedules();
    }

    @Override
    public Schedule getScheduleById(Integer id) {
        return scheduleMapper.getScheduleById(id);
    }

    @Override
    public List<Schedule> getSchedulesByStaffId(Integer staffId) {
        return scheduleMapper.getSchedulesByStaffId(staffId);
    }

    @Override
    public List<Schedule> getSchedulesByDate(LocalDate date) {
        return scheduleMapper.getSchedulesByDate(date);
    }

    @Override
    public void insertSchedule(Schedule schedule) {
        scheduleMapper.insertSchedule(schedule);
    }

    @Override
    public void updateSchedule(Schedule schedule) {
        scheduleMapper.updateSchedule(schedule);
    }

    @Override
    public void deleteSchedule(Integer id) {
        scheduleMapper.deleteSchedule(id);
    }
}