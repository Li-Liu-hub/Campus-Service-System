package com.jsyl.dto;

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

    /**
     * 订单类型ID
     */
    private Integer typeId;

    /**
     * 服务/收货地址
     */
    private String serviceAddress;

    /**
     * 需求描述
     */
    private String requirement;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 校区ID
     */
    private Integer campusId;

    /**
     * 订单要求完成时间
     */
    private LocalDateTime requireTime;
}
