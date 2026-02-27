package com.jsyl.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsVO {

    private Long orderCount;
    private Double totalAmount;
    private Long postCount;
    private Long acceptedOrderCount;
}
