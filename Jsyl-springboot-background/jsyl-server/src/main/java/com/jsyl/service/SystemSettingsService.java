package com.jsyl.service;

import com.jsyl.entity.SystemSettings;

public interface SystemSettingsService {

    SystemSettings getSettings();

    void updateSettings(SystemSettings settings);

}
