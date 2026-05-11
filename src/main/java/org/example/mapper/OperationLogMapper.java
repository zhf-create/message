package org.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.pojo.OperationLog;

import java.util.List;

@Mapper
public interface OperationLogMapper {

    @Select("select * from operation_log order by create_time desc limit 10")
    List<OperationLog> getRecentLogs();

    @Insert("insert into operation_log(operation_type, operation_name, operator, create_time)" +
            " values(#{operationType}, #{operationName}, #{operator}, now())")
    void insertLog(OperationLog log);
}