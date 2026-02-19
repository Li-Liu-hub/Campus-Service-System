package com.jsyl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息类型：1-聊天消息 2-系统通知 3-心跳
     */
    private Integer type;

    /**
     * 发送者ID
     */
    private Integer senderId;

    /**
     * 发送者昵称
     */
    private String senderNickname;

    /**
     * 接收者ID
     */
    private Integer receiverId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息ID（数据库记录ID）
     */
    private Long messageId;

    /**
     * 会话ID
     */
    private Long conversationId;

    /**
     * 发送时间
     */
    private LocalDateTime createTime;

}
