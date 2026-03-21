package com.jsyl.module.notification.service;

import com.jsyl.model.notification.entity.Notification;

import java.util.List;

public interface NotificationService {

    List<Notification> getNotifications(Integer userId, Integer type, Integer isRead);

    Integer getUnreadCount(Integer userId);

    void markAsRead(Long id);

    void markAllAsRead(Integer userId);

    void createNotification(Notification notification);

    void deleteNotification(Long id);

    void broadcastNotification(String title, String content, Integer type);

}
