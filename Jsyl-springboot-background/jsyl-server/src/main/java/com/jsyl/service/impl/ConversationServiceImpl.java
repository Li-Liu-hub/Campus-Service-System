package com.jsyl.service.impl;

import com.jsyl.context.BaseContext;
import com.jsyl.entity.Conversation;
import com.jsyl.entity.PrivateMessage;
import com.jsyl.mapper.ConversationMapper;
import com.jsyl.mapper.PrivateMessageMapper;
import com.jsyl.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConversationServiceImpl implements ConversationService {

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private PrivateMessageMapper privateMessageMapper;

    @Override
    public List<Conversation> getConversations(Integer userId) {
        return conversationMapper.getByUserId(userId);
    }

    @Override
    public Conversation getOrCreateConversation(Integer userId, Integer targetUserId) {
        Conversation conversation = conversationMapper.getByUserIdAndTargetUserId(userId, targetUserId);
        if (conversation == null) {
            conversation = Conversation.builder()
                    .userId(userId)
                    .targetUserId(targetUserId)
                    .lastMessage("")
                    .unreadCount(0)
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            conversationMapper.insert(conversation);
        }
        return conversation;
    }

    @Override
    public List<PrivateMessage> getMessages(Integer userId, Integer targetUserId) {
        return privateMessageMapper.getByUserIds(userId, targetUserId);
    }

    @Override
    @Transactional
    public PrivateMessage sendMessage(Integer senderId, Integer targetUserId, String content) {
        LocalDateTime now = LocalDateTime.now();

        Conversation senderConv = getOrCreateConversation(senderId, targetUserId);
        Conversation receiverConv = getOrCreateConversation(targetUserId, senderId);

        PrivateMessage message = PrivateMessage.builder()
                .conversationId(senderConv.getId())
                .senderId(senderId)
                .receiverId(targetUserId)
                .content(content)
                .msgType(1)
                .isRead(0)
                .createTime(now)
                .build();
        privateMessageMapper.insert(message);

        senderConv.setLastMessage(content);
        senderConv.setUpdateTime(now);
        conversationMapper.update(senderConv);

        receiverConv.setLastMessage(content);
        receiverConv.setUnreadCount(receiverConv.getUnreadCount() + 1);
        receiverConv.setUpdateTime(now);
        conversationMapper.update(receiverConv);

        return message;
    }

    @Override
    @Transactional
    public void markAsRead(Integer userId, Integer targetUserId) {
        privateMessageMapper.markAsRead(userId, targetUserId);
        conversationMapper.resetUnreadCount(userId, targetUserId);
    }

    @Override
    public Integer getUnreadCount(Integer userId) {
        return privateMessageMapper.countUnreadByUserId(userId);
    }

}
