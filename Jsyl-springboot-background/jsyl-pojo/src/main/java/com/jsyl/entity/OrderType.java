package com.jsyl.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderType implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单类型ID
     */
    private Integer id;

    /**
     * 订单类型名称
     */
    private String typeName;

}