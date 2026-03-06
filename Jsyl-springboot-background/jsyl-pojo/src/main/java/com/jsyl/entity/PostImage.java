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
public class PostImage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long postId;

    private String imageUrl;

    private Integer sortOrder;

    private LocalDateTime createTime;

}
