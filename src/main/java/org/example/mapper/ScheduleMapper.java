package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.Schedule;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ScheduleMapper {

    @Select("select * from schedule")
    List<Schedule> getAllSchedules();

    @Select("select * from schedule where id=#{id}")
    Schedule getScheduleById(Integer id);

    @Select("select * from schedule where staff_id=#{staffId}")
    List<Schedule> getSchedulesByStaffId(Integer staffId);

    @Select("select * from schedule where date=#{date}")
    List<Schedule> getSchedulesByDate(LocalDate date);

    @Insert("insert into schedule(staff_id, staff_name, date, start_time, end_time, status, notes, create_time, update_time)" +
            " values(0, #{staffName}, #{date}, #{startTime}, #{endTime}, #{status}, #{notes}, now(), now())")
    void insertSchedule(Schedule schedule);

    @Update("update schedule set staff_id=#{staffId}, staff_name=#{staffName}, date=#{date}, " +
            "start_time=#{startTime}, end_time=#{endTime}, status=#{status}, notes=#{notes}, update_time=now() where id=#{id}")
    void updateSchedule(Schedule schedule);

    @Delete("delete from schedule where id=#{id}")
    void deleteSchedule(Integer id);
}