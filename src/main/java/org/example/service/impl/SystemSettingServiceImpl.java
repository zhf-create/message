package org.example.service.impl;

import org.example.mapper.SystemSettingMapper;
import org.example.pojo.SystemSetting;
import org.example.service.SystemSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemSettingServiceImpl implements SystemSettingService {

    @Autowired
    private SystemSettingMapper systemSettingMapper;

    @Override
    public List<SystemSetting> getAllSettings() {
        return systemSettingMapper.getAllSettings();
    }

    @Override
    public SystemSetting getSettingById(Integer id) {
        return systemSettingMapper.getSettingById(id);
    }

    @Override
    public SystemSetting getSettingByKey(String settingKey) {
        return systemSettingMapper.getSettingByKey(settingKey);
    }

    @Override
    public void insertSetting(SystemSetting setting) {
        systemSettingMapper.insertSetting(setting);
    }

    @Override
    public void updateSetting(SystemSetting setting) {
        systemSettingMapper.updateSetting(setting);
    }

    @Override
    public void deleteSetting(Integer id) {
        systemSettingMapper.deleteSetting(id);
    }
}