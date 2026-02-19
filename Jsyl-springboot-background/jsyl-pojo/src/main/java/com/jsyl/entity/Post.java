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
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Integer userId;

    private Integer typeId;

    private String title;

    private String content;

    private Integer viewCount;

    private Integer replyCount;

    private Integer likeCount;

    private Integer commentCount;

    private String author;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime deletedAt;

}
