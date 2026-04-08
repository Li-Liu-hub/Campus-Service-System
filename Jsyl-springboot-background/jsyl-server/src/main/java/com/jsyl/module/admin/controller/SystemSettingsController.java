package com.jsyl.module.admin.controller;

import com.jsyl.model.common.entity.SystemSettings;
import com.jsyl.common.result.Result;
import com.jsyl.module.admin.service.SystemSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/system")
@Slf4j
public class SystemSettingsController {

    @Autowired
    private SystemSettingsService systemSettingsService;

    @GetMapping("/settings")
    public Result<SystemSettings> getSettings() {
        SystemSettings settings = systemSettingsService.getSettings();
        return Result.success(settings);
    }

    @PutMapping("/settings")
    public Result<String> updateSettings(@RequestBody SystemSettings settings) {
        systemSettingsService.updateSettings(settings);
        return Result.success("保存成功");
    }

}
