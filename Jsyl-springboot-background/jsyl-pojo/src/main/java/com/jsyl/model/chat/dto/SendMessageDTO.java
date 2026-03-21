package com.jsyl.model.chat.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SendMessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer targetUserId;

    private String content;

}
