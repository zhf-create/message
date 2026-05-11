package org.example.service.impl;

import org.example.mapper.CustomerRecordMapper;
import org.example.pojo.CustomerRecord;
import org.example.service.CustomerRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerRecordServiceImpl implements CustomerRecordService {

    @Autowired
    private CustomerRecordMapper customerRecordMapper;

    @Override
    public List<CustomerRecord> getAllRecords() {
        return customerRecordMapper.getAllRecords();
    }

    @Override
    public CustomerRecord getRecordById(Integer id) {
        return customerRecordMapper.getRecordById(id);
    }

    @Override
    public CustomerRecord getRecordByPhone(String phone) {
        return customerRecordMapper.getRecordByPhone(phone);
    }

    @Override
    public void insertRecord(CustomerRecord record) {
        customerRecordMapper.insertRecord(record);
    }

    @Override
    public void updateRecord(CustomerRecord record) {
        customerRecordMapper.updateRecord(record);
    }

    @Override
    public void deleteRecord(Integer id) {
        customerRecordMapper.deleteRecord(id);
    }
}