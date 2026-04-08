package com.jsyl.module.chat.controller;

import com.jsyl.common.context.BaseContext;
import com.jsyl.model.chat.dto.SendMessageDTO;
import com.jsyl.model.chat.entity.Conversation;
import com.jsyl.model.chat.entity.PrivateMessage;
import com.jsyl.common.result.Result;
import com.jsyl.module.chat.service.ConversationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jsyl/user/conversation")
@Slf4j
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @GetMapping("/list")
    public Result<List<Conversation>> getConversationList() {
        Long userIdLong = BaseContext.getCurrentId();
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        List<Conversation> conversations = conversationService.getConversations(userId);
        return Result.success(conversations);
    }

    @GetMapping("/messages/{targetUserId}")
    public Result<List<PrivateMessage>> getMessages(@PathVariable Integer targetUserId) {
        Long userIdLong = BaseContext.getCurrentId();
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        List<PrivateMessage> messages = conversationService.getMessages(userId, targetUserId);
        conversationService.markAsRead(userId, targetUserId);
        return Result.success(messages);
    }

    @PostMapping("/send")
    public Result<PrivateMessage> sendMessage(@RequestBody SendMessageDTO sendMessageDTO) {
        Long userIdLong = BaseContext.getCurrentId();
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        Integer targetUserId = sendMessageDTO.getTargetUserId();
        String content = sendMessageDTO.getContent();
        PrivateMessage message = conversationService.sendMessage(userId, targetUserId, content);
        return Result.success(message);
    }

    @PutMapping("/read/{targetUserId}")
    public Result markAsRead(@PathVariable Integer targetUserId) {
        Long userIdLong = BaseContext.getCurrentId();
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        conversationService.markAsRead(userId, targetUserId);
        return Result.success();
    }

    @GetMapping("/unread/count")
    public Result<Integer> getUnreadCount() {
        Long userIdLong = BaseContext.getCurrentId();
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        Integer count = conversationService.getUnreadCount(userId);
        return Result.success(count);
    }

}
