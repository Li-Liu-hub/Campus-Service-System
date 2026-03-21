package com.jsyl.module.chat.controller;

import com.jsyl.common.context.BaseContext;
import com.jsyl.model.chat.dto.SendMessageDTO;
import com.jsyl.model.chat.entity.Conversation;
import com.jsyl.model.chat.entity.PrivateMessage;
import com.jsyl.common.result.Result;
import com.jsyl.module.chat.service.ConversationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jsyl/user/conversation")
@Api(tags = "私聊相关接口")
@Slf4j
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @GetMapping("/list")
    @ApiOperation("获取会话列表")
    public Result<List<Conversation>> getConversationList() {
        Long userIdLong = BaseContext.getCurrentId();
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        List<Conversation> conversations = conversationService.getConversations(userId);
        return Result.success(conversations);
    }

    @GetMapping("/messages/{targetUserId}")
    @ApiOperation("获取与指定用户的消息列表")
    public Result<List<PrivateMessage>> getMessages(@PathVariable Integer targetUserId) {
        Long userIdLong = BaseContext.getCurrentId();
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        List<PrivateMessage> messages = conversationService.getMessages(userId, targetUserId);
        conversationService.markAsRead(userId, targetUserId);
        return Result.success(messages);
    }

    @PostMapping("/send")
    @ApiOperation("发送消息")
    public Result<PrivateMessage> sendMessage(@RequestBody SendMessageDTO sendMessageDTO) {
        Long userIdLong = BaseContext.getCurrentId();
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        Integer targetUserId = sendMessageDTO.getTargetUserId();
        String content = sendMessageDTO.getContent();
        PrivateMessage message = conversationService.sendMessage(userId, targetUserId, content);
        return Result.success(message);
    }

    @PutMapping("/read/{targetUserId}")
    @ApiOperation("标记消息为已读")
    public Result markAsRead(@PathVariable Integer targetUserId) {
        Long userIdLong = BaseContext.getCurrentId();
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        conversationService.markAsRead(userId, targetUserId);
        return Result.success();
    }

    @GetMapping("/unread/count")
    @ApiOperation("获取未读消息总数")
    public Result<Integer> getUnreadCount() {
        Long userIdLong = BaseContext.getCurrentId();
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        Integer count = conversationService.getUnreadCount(userId);
        return Result.success(count);
    }

}
