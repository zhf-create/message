package org.example.service;

import org.example.pojo.CustomerRecord;

import java.util.List;

public interface CustomerRecordService {

    List<CustomerRecord> getAllRecords();

    CustomerRecord getRecordById(Integer id);

    CustomerRecord getRecordByPhone(String phone);

    void insertRecord(CustomerRecord record);

    void updateRecord(CustomerRecord record);

    void deleteRecord(Integer id);
}