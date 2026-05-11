package org.example.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationLog {
    private Integer id;
    private String operationType;
    private String operationName;
    private String operator;
    private LocalDateTime createTime;
}