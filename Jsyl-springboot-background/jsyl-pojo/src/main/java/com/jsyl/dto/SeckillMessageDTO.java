package com.jsyl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillMessageDTO implements Serializable {

    private Long goodsId;
    private Integer userId;
    private Integer quantity;
    private Long timestamp;
}
