package com.jsyl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPageQueryDTO {

    /*页码*/
    private int page;

    /*每页记录数*/
    private int pageSize;

    /*订单号*/
    private String orderNo;

    /*订单类型*/
    private Integer typeId;

    /*订单状态*/
    private Integer orderStatus;

    /*下单用户ID*/
    private Integer userId;

    /*接单用户ID*/
    private Integer acceptorId;

    /*用户姓名（昵称）*/
    private String userName;

    /*校区ID*/
    private Integer campusId;
}