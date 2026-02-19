package com.jsyl.controller;

import com.jsyl.context.BaseContext;
import com.jsyl.dto.SendMessageDTO;
import com.jsyl.entity.Conversation;
import com.jsyl.entity.PrivateMessage;
import com.jsyl.result.Result;
import com.jsyl.service.ConversationService;
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
        log.info("获取会话列表，用户ID：{}", userId);
        List<Conversation> conversations = conversationService.getConversations(userId);
        return Result.success(conversations);
    }

    @GetMapping("/messages/{targetUserId}")
    @ApiOperation("获取与指定用户的消息列表")
    public Result<List<PrivateMessage>> getMessages(@PathVariable Integer targetUserId) {
        Long userIdLong = BaseContext.getCurrentId();
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        log.info("获取消息列表，当前用户ID：{}，目标用户ID：{}", userId, targetUserId);
        List<PrivateMessage> messages = conversationService.getMessages(userId, targetUserId);
        log.info("查询到消息数量：{}", messages != null ? messages.size() : 0);
        if (messages != null && !messages.isEmpty()) {
            for (PrivateMessage msg : messages) {
                log.info("消息 - ID: {}, 发送者: {}, 接收者: {}, 内容: {}",
                        msg.getId(), msg.getSenderId(), msg.getReceiverId(), msg.getContent());
            }
        }
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
        log.info("发送消息，发送者ID：{}，接收者ID：{}，内容：{}", userId, targetUserId, content);
        PrivateMessage message = conversationService.sendMessage(userId, targetUserId, content);
        return Result.success(message);
    }

    @PutMapping("/read/{targetUserId}")
    @ApiOperation("标记消息为已读")
    public Result markAsRead(@PathVariable Integer targetUserId) {
        Long userIdLong = BaseContext.getCurrentId();
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        log.info("标记消息为已读，当前用户ID：{}，目标用户ID：{}", userId, targetUserId);
        conversationService.markAsRead(userId, targetUserId);
        return Result.success();
    }

    @GetMapping("/unread/count")
    @ApiOperation("获取未读消息总数")
    public Result<Integer> getUnreadCount() {
        Long userIdLong = BaseContext.getCurrentId();
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        log.info("获取未读消息总数，用户ID：{}", userId);
        Integer count = conversationService.getUnreadCount(userId);
        return Result.success(count);
    }

}
