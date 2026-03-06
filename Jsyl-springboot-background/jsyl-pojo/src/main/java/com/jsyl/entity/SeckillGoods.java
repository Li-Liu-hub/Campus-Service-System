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
public class SeckillGoods implements Serializable {

    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private BigDecimal originalPrice;
    private BigDecimal seckillPrice;
    private Integer stock;
    private Integer soldCount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private Integer limitPerUser;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
