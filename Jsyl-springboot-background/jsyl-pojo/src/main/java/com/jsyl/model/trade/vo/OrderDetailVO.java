package com.jsyl.model.trade.vo;

import com.jsyl.model.user.vo.UserVO;
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
public class OrderDetailVO {

    private Long id;

    private String orderNo;

    private String serviceAddress;

    private String requirement;

    private String contactPhone;

    private BigDecimal orderAmount;

    private Integer orderStatus;

    private String orderStatusName;

    private Integer typeId;

    private String typeName;

    private LocalDateTime createTime;

    private UserVO publisher;

    private UserVO acceptor;
}
