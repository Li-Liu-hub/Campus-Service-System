package com.jsyl.constant;

/**
 * 状态常量，启用或者禁用
 */
public class StatusConstant {

    //启用
    public static final Integer ENABLE = 1;

    //禁用
    public static final Integer DISABLE = 0;

    //订单状态：待接单
    public static final Integer ORDER_STATUS_PENDING = 0;

    //订单状态：已接单
    public static final Integer ORDER_STATUS_ACCEPTED = 1;

    //订单状态：已完成
    public static final Integer ORDER_STATUS_COMPLETED = 2;

    //订单状态：已取消
    public static final Integer ORDER_STATUS_CANCELLED = 3;
}
