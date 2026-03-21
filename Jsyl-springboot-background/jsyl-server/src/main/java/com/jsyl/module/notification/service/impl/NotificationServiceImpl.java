package com.jsyl.module.notification.service.impl;

import com.jsyl.model.notification.entity.Notification;
import com.jsyl.module.notification.mapper.NotificationMapper;
import com.jsyl.module.notification.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public List<Notification> getNotifications(Integer userId, Integer type, Integer isRead) {
        if (type != null && isRead != null) {
            // 按类型和已读状态查询
            return notificationMapper.getByUserIdAndTypeAndIsRead(userId, type, isRead);
        } else if (isRead != null) {
            // 按已读状态查询
            return notificationMapper.getByUserIdAndIsRead(userId, isRead);
        } else if (type != null) {
            // 按类型查询
            return notificationMapper.getByUserIdAndType(userId, type);
        } else {
            // 查询全部
            return notificationMapper.getByUserId(userId);
        }
    }

    @Override
    public Integer getUnreadCount(Integer userId) {
        return notificationMapper.getUnreadCount(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        notificationMapper.markAsRead(id);
    }

    @Override
    @Transactional
    public void markAllAsRead(Integer userId) {
        notificationMapper.markAllAsRead(userId);
    }

    @Override
    @Transactional
    public void createNotification(Notification notification) {
        if (notification.getCreateTime() == null) {
            notification.setCreateTime(LocalDateTime.now());
        }
        if (notification.getIsRead() == null) {
            notification.setIsRead(0);
        }
        notificationMapper.insert(notification);
    }

    @Override
    @Transactional
    public void deleteNotification(Long id) {
        notificationMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void broadcastNotification(String title, String content, Integer type) {

        // 获取所有用户ID
        List<Integer> userIds = notificationMapper.getAllUserIds();

        if (userIds.isEmpty()) {
            log.warn("系统没有用户，无法发送全员通知");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        List<Notification> notifications = new ArrayList<>();

        for (Integer userId : userIds) {
            Notification notification = Notification.builder()
                    .userId(userId)
                    .type(type != null ? type : 0)
                    .title(title)
                    .content(content)
                    .relatedId(null)
                    .isRead(0)
                    .createTime(now)
                    .build();
            notifications.add(notification);
        }

        notificationMapper.batchInsert(notifications);
    }

}
