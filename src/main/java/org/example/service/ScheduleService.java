package org.example.service;

import org.example.pojo.Schedule;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {

    List<Schedule> getAllSchedules();

    Schedule getScheduleById(Integer id);

    List<Schedule> getSchedulesByStaffId(Integer staffId);

    List<Schedule> getSchedulesByDate(LocalDate date);

    void insertSchedule(Schedule schedule);

    void updateSchedule(Schedule schedule);

    void deleteSchedule(Integer id);
}