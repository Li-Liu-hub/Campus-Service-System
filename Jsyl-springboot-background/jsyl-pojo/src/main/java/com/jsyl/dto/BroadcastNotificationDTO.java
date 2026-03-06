package com.jsyl.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BroadcastNotificationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private String content;

    private Integer type;

}
