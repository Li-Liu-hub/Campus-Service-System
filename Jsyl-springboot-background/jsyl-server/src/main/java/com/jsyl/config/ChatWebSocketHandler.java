package com.jsyl.config;

import com.alibaba.fastjson.JSON;
import com.jsyl.dto.WebSocketMessageDTO;
import com.jsyl.entity.Conversation;
import com.jsyl.entity.PrivateMessage;
import com.jsyl.entity.User;
import com.jsyl.mapper.ConversationMapper;
import com.jsyl.mapper.PrivateMessageMapper;
import com.jsyl.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Map<Integer, WebSocketSession> SESSIONS = new ConcurrentHashMap<>();

    @Autowired
    private ConversationMapper conversationMapper;

    @Autowired
    private PrivateMessageMapper privateMessageMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Integer userId = getUserIdFromSession(session);
        if (userId != null) {
            SESSIONS.put(userId, session);
            log.info("WebSocket连接建立，用户ID：{}，当前在线人数：{}", userId, SESSIONS.size());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            String payload = message.getPayload();
            Map<String, Object> messageMap = JSON.parseObject(payload);

            Integer senderId = getUserIdFromSession(session);
            if (senderId == null) {
                log.warn("发送消息失败：用户未登录");
                return;
            }

            Integer type = messageMap.get("type") != null ? (Integer) messageMap.get("type") : 0;
            if (type == 3) {
                return;
            }

            if (type == 1) {
                Integer receiverId = (Integer) messageMap.get("receiverId");
                String content = (String) messageMap.get("content");
                handleChatMessage(senderId, receiverId, content);
            }
        } catch (Exception e) {
            log.error("处理消息失败", e);
        }
    }

    private void handleChatMessage(Integer senderId, Integer receiverId, String content) {
        User sender = userMapper.getById(senderId);
        User receiver = userMapper.getById(receiverId);

        if (receiver == null) {
            log.warn("发送消息失败：接收者不存在");
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        Conversation senderConv = conversationMapper.getByUserIdAndTargetUserId(senderId, receiverId);
        if (senderConv == null) {
            senderConv = Conversation.builder()
                    .userId(senderId)
                    .targetUserId(receiverId)
                    .lastMessage(content)
                    .unreadCount(0)
                    .createTime(now)
                    .updateTime(now)
                    .build();
            conversationMapper.insert(senderConv);
        } else {
            senderConv.setLastMessage(content);
            senderConv.setUpdateTime(now);
            conversationMapper.update(senderConv);
        }

        Conversation receiverConv = conversationMapper.getByUserIdAndTargetUserId(receiverId, senderId);
        if (receiverConv == null) {
            receiverConv = Conversation.builder()
                    .userId(receiverId)
                    .targetUserId(senderId)
                    .lastMessage(content)
                    .unreadCount(1)
                    .createTime(now)
                    .updateTime(now)
                    .build();
            conversationMapper.insert(receiverConv);
        } else {
            receiverConv.setLastMessage(content);
            receiverConv.setUnreadCount(receiverConv.getUnreadCount() + 1);
            receiverConv.setUpdateTime(now);
            conversationMapper.update(receiverConv);
        }

        PrivateMessage privateMessage = PrivateMessage.builder()
                .conversationId(senderConv.getId())
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .msgType(1)
                .isRead(0)
                .createTime(now)
                .build();
        privateMessageMapper.insert(privateMessage);

        String senderNickname = sender != null ? sender.getNickname() : "";

        Map<String, Object> responseMessage = new ConcurrentHashMap<>();
        responseMessage.put("type", 1);
        responseMessage.put("senderId", senderId);
        responseMessage.put("senderNickname", senderNickname);
        responseMessage.put("receiverId", receiverId);
        responseMessage.put("content", content);
        responseMessage.put("messageId", privateMessage.getId());
        responseMessage.put("conversationId", senderConv.getId());
        responseMessage.put("createTime", now.toString());

        sendMessageToUser(receiverId, responseMessage);
        sendMessageToUser(senderId, responseMessage);

        log.info("消息发送成功：{} -> {}, 内容：{}", senderId, receiverId, content);
    }

    private void sendMessageToUser(Integer userId, Map<String, Object> message) {
        WebSocketSession session = SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(JSON.toJSONString(message)));
            } catch (IOException e) {
                log.error("发送消息失败，用户ID：{}", userId, e);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Integer userId = getUserIdFromSession(session);
        if (userId != null) {
            SESSIONS.remove(userId);
            log.info("WebSocket连接关闭，用户ID：{}，当前在线人数：{}", userId, SESSIONS.size());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket传输错误", exception);
    }

    private Integer getUserIdFromSession(WebSocketSession session) {
        Map<String, Object> attributes = session.getAttributes();
        Object userIdObj = attributes.get("userId");
        if (userIdObj instanceof Long) {
            return ((Long) userIdObj).intValue();
        } else if (userIdObj instanceof Integer) {
            return (Integer) userIdObj;
        }
        return null;
    }

}
