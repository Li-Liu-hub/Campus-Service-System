package com.jsyl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostPageQueryDTO {

    /*页码*/
    private int page;

    /*每页记录数*/
    private int pageSize;

    /*帖子标题关键词搜索*/
    private String keyword;

    /*发布者用户ID*/
    private Integer userId;
}