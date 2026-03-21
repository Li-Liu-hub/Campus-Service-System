package com.jsyl.module.admin.service.impl;

import com.jsyl.model.common.entity.SystemSettings;
import com.jsyl.module.admin.mapper.SystemSettingsMapper;
import com.jsyl.module.admin.service.SystemSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemSettingsServiceImpl implements SystemSettingsService {

    @Autowired
    private SystemSettingsMapper systemSettingsMapper;

    @Override
    public SystemSettings getSettings() {
        SystemSettings settings = systemSettingsMapper.getSettings();
        if (settings == null) {
            // 如果没有配置，创建默认设置
            settings = SystemSettings.builder()
                    .id(1)
                    .systemName("校园服务平台")
                    .systemDesc("为广大师生提供便捷的校园服务")
                    .maintenanceMode(0)
                    .maintenanceNotice("")
                    .allowRegister(1)
                    .allowComment(1)
                    .allowOrder(1)
                    .build();
        }
        return settings;
    }

    @Override
    public void updateSettings(SystemSettings settings) {
        systemSettingsMapper.update(settings);
    }

}
