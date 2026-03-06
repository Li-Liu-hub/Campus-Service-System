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

    private Integer type;

    private Integer senderId;

    private String senderNickname;

    private Integer receiverId;

    private String content;

    private Long messageId;

    private Long conversationId;

    private LocalDateTime createTime;

}
