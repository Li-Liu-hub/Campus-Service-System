package com.jsyl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentPublishDTO {

    /**
     * 所属帖子ID
     */
    private Long postId;

    /**
     * 父评论ID（顶级评论填0）
     */
    private Long parentId;

    /**
     * 评论内容
     */
    private String content;
}