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
public class UserBehavior implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer userId;
    private String itemType;
    private Long itemId;
    private String behaviorType;
    private Integer score;
    private LocalDateTime createTime;
}
