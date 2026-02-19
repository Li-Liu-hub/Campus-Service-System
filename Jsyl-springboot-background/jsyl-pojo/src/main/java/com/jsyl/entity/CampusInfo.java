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
public class CampusInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 校区ID
     */
    private Integer id;

    /**
     * 校区名称
     */
    private String campusName;

}
