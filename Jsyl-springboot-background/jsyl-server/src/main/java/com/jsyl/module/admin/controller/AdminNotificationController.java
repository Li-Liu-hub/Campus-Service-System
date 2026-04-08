package com.jsyl.module.admin.controller;

import com.jsyl.common.annotation.RequireRole;
import com.jsyl.common.constant.MessageConstant;
import com.jsyl.common.enumeration.RoleEnum;
import com.jsyl.model.notification.dto.BroadcastNotificationDTO;
import com.jsyl.common.result.Result;
import com.jsyl.module.notification.service.NotificationService;
import com.jsyl.module.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/notification")
@Slf4j
public class AdminNotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @RequireRole(RoleEnum.SUPER_ADMIN)
    @PostMapping("/broadcast")
    public Result<String> broadcastNotification(@RequestBody BroadcastNotificationDTO dto) {

        // 验证必填字段
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            return Result.error("通知标题不能为空");
        }
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            return Result.error("通知内容不能为空");
        }

        // 发布全员通知
        notificationService.broadcastNotification(
                dto.getTitle().trim(),
                dto.getContent().trim(),
                dto.getType()
        );

        return Result.success(MessageConstant.SUCCESS);
    }

}
