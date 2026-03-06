package com.jsyl.entity;

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
public class SystemSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String systemName;

    private String systemDesc;

    private Integer maintenanceMode;

    private String maintenanceNotice;

    private Integer allowRegister;

    private Integer allowComment;

    private Integer allowOrder;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
