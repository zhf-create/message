package org.example.pojo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
public class Schedule {
    private Integer id;
    private Integer staffId;
    private String staffName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private String notes;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}