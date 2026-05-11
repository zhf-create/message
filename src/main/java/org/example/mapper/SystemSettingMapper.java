package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.SystemSetting;

import java.util.List;

@Mapper
public interface SystemSettingMapper {

    @Select("select * from system_setting")
    List<SystemSetting> getAllSettings();

    @Select("select * from system_setting where id=#{id}")
    SystemSetting getSettingById(Integer id);

    @Select("select * from system_setting where setting_key=#{settingKey}")
    SystemSetting getSettingByKey(String settingKey);

    @Insert("insert into system_setting(setting_key, setting_value, description, create_time, update_time)" +
            " values(#{settingKey}, #{settingValue}, #{description}, now(), now())")
    void insertSetting(SystemSetting setting);

    @Update("update system_setting set setting_key=#{settingKey}, setting_value=#{settingValue}, " +
            "description=#{description}, update_time=now() where id=#{id}")
    void updateSetting(SystemSetting setting);

    @Delete("delete from system_setting where id=#{id}")
    void deleteSetting(Integer id);
}