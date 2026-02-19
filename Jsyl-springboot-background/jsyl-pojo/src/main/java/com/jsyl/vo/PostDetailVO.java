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
public class PostDetailVO {

    private Long id;

    private String title;

    private String content;

    private Integer viewCount;

    private Integer replyCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 发布者信息
     */
    private UserVO author;
}