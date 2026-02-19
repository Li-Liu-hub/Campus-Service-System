package com.jsyl.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentVO {

    private Long id;

    private Long postId;

    private Long parentId;

    private String content;

    private LocalDateTime createTime;

    /**
     * 评论者用户ID
     */
    private Integer userId;

    /**
     * 评论者昵称
     */
    private String userNickname;

    /**
     * 评论者信息
     */
    private UserVO commentator;
}