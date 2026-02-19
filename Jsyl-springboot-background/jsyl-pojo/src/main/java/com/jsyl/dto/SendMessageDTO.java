package com.jsyl.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SendMessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 接收者用户ID
     */
    private Integer targetUserId;

    /**
     * 消息内容
     */
    private String content;

}
