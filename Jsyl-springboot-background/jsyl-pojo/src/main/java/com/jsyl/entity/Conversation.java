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

    private Long id;

    private Integer userId;

    private Integer targetUserId;

    private String lastMessage;

    private Integer unreadCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String targetUserNickname;

}
