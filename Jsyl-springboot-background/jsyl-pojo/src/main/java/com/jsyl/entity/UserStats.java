package com.jsyl.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStats implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 信誉分
     */
    private Integer creditScore;

    /**
     * 完成订单数
     */
    private Integer orderCount;

    /**
     * 平均评分
     */
    private BigDecimal avgRating;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
