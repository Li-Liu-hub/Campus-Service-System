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
public class PostType implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 帖子类型ID
     */
    private Integer id;

    /**
     * 帖子类型名称
     */
    private String typeName;

}
