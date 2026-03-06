package com.jsyl.controller.admin;

import com.jsyl.entity.SystemSettings;
import com.jsyl.result.Result;
import com.jsyl.service.SystemSettingsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/system")
@Api(tags = "系统设置管理")
@Slf4j
public class SystemSettingsController {

    @Autowired
    private SystemSettingsService systemSettingsService;

    @GetMapping("/settings")
    @ApiOperation("获取系统设置")
    public Result<SystemSettings> getSettings() {
        SystemSettings settings = systemSettingsService.getSettings();
        return Result.success(settings);
    }

    @PutMapping("/settings")
    @ApiOperation("更新系统设置")
    public Result<String> updateSettings(@RequestBody SystemSettings settings) {
        systemSettingsService.updateSettings(settings);
        return Result.success("保存成功");
    }

}
