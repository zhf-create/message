package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.CustomerRecord;

import java.util.List;

@Mapper
public interface CustomerRecordMapper {

    @Select("select * from customer_record")
    List<CustomerRecord> getAllRecords();

    @Select("select * from customer_record where id=#{id}")
    CustomerRecord getRecordById(Integer id);

    @Select("select * from customer_record where phone=#{phone}")
    CustomerRecord getRecordByPhone(String phone);

    @Insert("insert into customer_record(name, phone, gender, age, address, health_condition, preferences, notes, create_time, update_time)" +
            " values(#{name}, #{phone}, #{gender}, #{age}, #{address}, #{healthCondition}, #{preferences}, #{notes}, now(), now())")
    void insertRecord(CustomerRecord record);

    @Update("update customer_record set name=#{name}, phone=#{phone}, gender=#{gender}, age=#{age}, " +
            "address=#{address}, health_condition=#{healthCondition}, preferences=#{preferences}, notes=#{notes}, update_time=now() where id=#{id}")
    void updateRecord(CustomerRecord record);

    @Delete("delete from customer_record where id=#{id}")
    void deleteRecord(Integer id);
}