package com.jsyl.model.trade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPublishDTO {

    private Integer typeId;

    private String serviceAddress;

    private String requirement;

    private String contactPhone;

    private BigDecimal orderAmount;

    private Integer campusId;

    private LocalDateTime requireTime;

    private Integer isSeckill;
}
