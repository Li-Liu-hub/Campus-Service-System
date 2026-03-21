package com.jsyl.model.operation.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignIn implements Serializable {

    private Long id;
    private Integer userId;
    private LocalDate signDate;
    private Integer continuousDays;
    private Integer totalDays;
    private LocalDateTime createTime;
}
