package com.jsyl.model.user.entity;

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
public class UserAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Integer userId;

    private String contactName;

    private String contactPhone;

    private String addressDetail;

    private Integer isDefault;

    private LocalDateTime createTime;

}
