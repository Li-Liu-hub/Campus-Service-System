package com.jsyl.common.websocket;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Map<Integer, WebSocketSession> SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Integer userId = getUserIdFromSession(session);
        if (userId != null) {
            SESSIONS.put(userId, session);
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
        // Note: Actual database operations should be handled by a service
        // This handler just manages the WebSocket session and message routing

        Map<String, Object> responseMessage = new ConcurrentHashMap<>();
        responseMessage.put("type", 1);
        responseMessage.put("senderId", senderId);
        responseMessage.put("receiverId", receiverId);
        responseMessage.put("content", content);

        sendMessageToUser(receiverId, responseMessage);
        sendMessageToUser(senderId, responseMessage);
    }

    public void sendMessageToUser(Integer userId, Map<String, Object> message) {
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
