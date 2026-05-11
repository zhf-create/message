package org.example.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerRecord {
    private Integer id;
    private String name;
    private String phone;
    private String gender;
    private Integer age;
    private String address;
    private String healthCondition;
    private String preferences;
    private String notes;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}