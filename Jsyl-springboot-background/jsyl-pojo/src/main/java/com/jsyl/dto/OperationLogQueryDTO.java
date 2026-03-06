package com.jsyl.dto;

import lombok.Data;
import java.util.List;

@Data
public class OperationLogQueryDTO {

    private String operatorName;

    private String operationType;

    private String startTime;

    private String endTime;

    private String status;

    private Integer page = 1;

    private Integer pageSize = 10;
}
