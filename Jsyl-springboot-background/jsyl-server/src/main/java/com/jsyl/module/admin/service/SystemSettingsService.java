package com.jsyl.module.admin.service;

import com.jsyl.model.common.entity.SystemSettings;

public interface SystemSettingsService {

    SystemSettings getSettings();

    void updateSettings(SystemSettings settings);

}
