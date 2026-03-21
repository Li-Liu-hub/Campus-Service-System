package com.jsyl.module.admin.mapper;

import com.jsyl.model.common.entity.SystemSettings;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SystemSettingsMapper {

    @Select("SELECT * FROM system_settings LIMIT 1")
    SystemSettings getSettings();

    @Update("UPDATE system_settings SET " +
            "system_name = #{systemName}, " +
            "system_desc = #{systemDesc}, " +
            "maintenance_mode = #{maintenanceMode}, " +
            "maintenance_notice = #{maintenanceNotice}, " +
            "allow_register = #{allowRegister}, " +
            "allow_comment = #{allowComment}, " +
            "allow_order = #{allowOrder} " +
            "WHERE id = #{id}")
    void update(SystemSettings settings);

}
