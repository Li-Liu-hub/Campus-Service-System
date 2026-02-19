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

    /**
     * 评论ID
     */
    private Long id;

    /**
     * 所属帖子ID
     */
    private Long postId;

    /**
     * 父评论ID(0表示顶级评论)
     */
    private Long parentId;

    /**
     * 评论者用户ID
     */
    private Integer userId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 状态(0正常 1删除)
     */
    private Integer status;

    /**
     * 评论时间
     */
    private LocalDateTime createTime;

    /**
     * 评论者昵称
     */
    private String userNickname;

}