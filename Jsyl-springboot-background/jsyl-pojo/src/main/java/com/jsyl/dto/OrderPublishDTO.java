package com.jsyl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
}