package com.jsyl.controller.admin;

import com.jsyl.constant.MessageConstant;
import com.jsyl.constant.RoleEnum;
import com.jsyl.context.BaseContext;
import com.jsyl.dto.BroadcastNotificationDTO;
import com.jsyl.entity.User;
import com.jsyl.result.Result;
import com.jsyl.service.NotificationService;
import com.jsyl.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/notification")
@Api(tags = "管理员通知管理")
@Slf4j
public class AdminNotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @PostMapping("/broadcast")
    @ApiOperation("发布全员通知")
    public Result<String> broadcastNotification(@RequestBody BroadcastNotificationDTO dto) {

        // 验证当前用户是否为超级管理员
        Long currentUserId = BaseContext.getCurrentId();
        if (currentUserId == null) {
            return Result.error("用户未登录");
        }

        User currentUser = userService.getUserById(currentUserId.intValue());
        if (currentUser == null) {
            return Result.error("用户不存在");
        }

        RoleEnum currentRole = RoleEnum.fromCode(currentUser.getRole() != null ? currentUser.getRole() : 1);
        if (!currentRole.isSuperAdmin()) {
            return Result.error("权限不足，只有超级管理员才能发布全员通知");
        }

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
