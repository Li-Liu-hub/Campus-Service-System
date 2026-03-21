package com.jsyl.model.admin.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Integer operatorId;

    private String operatorName;

    private String operationType;

    private String operation;

    private String requestMethod;

    private String requestUrl;

    private String requestParams;

    private String responseResult;

    private String ipAddress;

    private String userAgent;

    private LocalDateTime createTime;

    private Long executionTime;

    private String status;

    private String errorMsg;

}
