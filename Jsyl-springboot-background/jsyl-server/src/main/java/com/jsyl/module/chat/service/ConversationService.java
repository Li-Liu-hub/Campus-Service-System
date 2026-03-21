package com.jsyl.module.chat.service;

import com.jsyl.model.chat.entity.Conversation;
import com.jsyl.model.chat.entity.PrivateMessage;

import java.util.List;

public interface ConversationService {

    List<Conversation> getConversations(Integer userId);

    Conversation getOrCreateConversation(Integer userId, Integer targetUserId);

    List<PrivateMessage> getMessages(Integer userId, Integer targetUserId);

    PrivateMessage sendMessage(Integer senderId, Integer targetUserId, String content);

    void markAsRead(Integer userId, Integer targetUserId);

    Integer getUnreadCount(Integer userId);

}
