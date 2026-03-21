package com.jsyl.model.operation.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignInVO implements Serializable {

    private Boolean signedToday;
    private Integer continuousDays;
    private Integer totalDays;
    private LocalDate lastSignDate;
}
