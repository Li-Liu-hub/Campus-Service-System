package com.jsyl.controller;

import com.jsyl.constant.MessageConstant;
import com.jsyl.context.BaseContext;
import com.jsyl.entity.Notification;
import com.jsyl.result.Result;
import com.jsyl.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/jsyl/home/notification")
@Slf4j
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/list")
    public Result<Map<String, Object>> getNotificationList(
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer isRead) {

        Long userIdLong = BaseContext.getCurrentId();
        if (userIdLong == null) {
            return Result.error("用户未登录");
        }
        Integer userId = userIdLong.intValue();

        List<Notification> notifications = notificationService.getNotifications(userId, type, isRead);
        Integer unreadCount = notificationService.getUnreadCount(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("records", notifications);
        result.put("total", notifications.size());
        result.put("unreadCount", unreadCount);

        return Result.success(result);
    }

    @GetMapping("/unreadCount")
    public Result<Integer> getUnreadCount() {
        Long userIdLong = BaseContext.getCurrentId();
        if (userIdLong == null) {
            return Result.error("用户未登录");
        }
        Integer userId = userIdLong.intValue();

        Integer count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }

    @PutMapping("/read/{id}")
    public Result<String> markAsRead(@PathVariable Long id) {

        notificationService.markAsRead(id);
        return Result.success(MessageConstant.SUCCESS);
    }

    @PostMapping("/readAll")
    public Result<String> markAllAsRead() {
        Long userIdLong = BaseContext.getCurrentId();
        if (userIdLong == null) {
            return Result.error("用户未登录");
        }
        Integer userId = userIdLong.intValue();

        notificationService.markAllAsRead(userId);
        return Result.success(MessageConstant.SUCCESS);
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> deleteNotification(@PathVariable Long id) {

        notificationService.deleteNotification(id);
        return Result.success(MessageConstant.SUCCESS);
    }

}
