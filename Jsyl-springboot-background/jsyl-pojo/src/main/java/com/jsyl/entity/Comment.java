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
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long postId;

    private Long parentId;

    private Integer userId;

    private String content;

    private Integer status;

    private LocalDateTime createTime;

    private String userNickname;

}