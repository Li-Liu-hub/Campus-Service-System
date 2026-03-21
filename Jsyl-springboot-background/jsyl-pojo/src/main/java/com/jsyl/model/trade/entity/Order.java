package com.jsyl.model.trade.entity;

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
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String orderNo;

    private Integer userId;

    private Integer acceptorId;

    private String serviceAddress;

    private String requirement;

    private String contactPhone;

    private BigDecimal orderAmount;

    private Integer orderStatus;

    private Integer typeId;

    private LocalDateTime createTime;

    private LocalDateTime requireTime;

    private LocalDateTime completeTime;

    private Integer campusId;

    private String publisherNickname;

    private String acceptorNickname;

    private Integer publisherCancel;

    private Integer acceptorCancel;

    private Integer isSeckill;

    private BigDecimal seckillFee;

}
