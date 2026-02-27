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
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单主键ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 下单用户ID
     */
    private Integer userId;

    /**
     * 接单用户ID
     */
    private Integer acceptorId;

    /**
     * 服务/收货地址
     */
    private String serviceAddress;

    /**
     * 具体需求描述
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
     * 订单状态：0-待接单 1-已接单 2-已完成 3-已取消
     */
    private Integer orderStatus;

    /**
     * 订单类型ID
     */
    private Integer typeId;

    /**
     * 下单时间
     */
    private LocalDateTime createTime;

    /**
     * 订单要求时间
     */
    private LocalDateTime requireTime;

    /**
     * 订单完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 校区ID
     */
    private Integer campusId;

    /**
     * 发布者昵称
     */
    private String publisherNickname;

    /**
     * 接单者昵称
     */
    private String acceptorNickname;

    /**
     * 发布者是否申请取消: 0-否 1-是
     */
    private Integer publisherCancel;

    /**
     * 接单者是否申请取消: 0-否 1-是
     */
    private Integer acceptorCancel;

}
