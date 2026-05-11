package org.example.service;

import org.example.pojo.SystemSetting;

import java.util.List;

public interface SystemSettingService {

    List<SystemSetting> getAllSettings();

    SystemSetting getSettingById(Integer id);

    SystemSetting getSettingByKey(String settingKey);

    void insertSetting(SystemSetting setting);

    void updateSetting(SystemSetting setting);

    void deleteSetting(Integer id);
}