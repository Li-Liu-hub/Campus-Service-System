package com.jsyl.entity;

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
public class Conversation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    private Long id;

    /**
     * 当前用户ID
     */
    private Integer userId;

    /**
     * 对方用户ID
     */
    private Integer targetUserId;

    /**
     * 最后一条消息预览
     */
    private String lastMessage;

    /**
     * 未读消息数
     */
    private Integer unreadCount;

    /**
     * 会话创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后消息时间
     */
    private LocalDateTime updateTime;

    /**
     * 对方用户昵称（非数据库字段）
     */
    private String targetUserNickname;

}
