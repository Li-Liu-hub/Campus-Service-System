package com.jsyl.model.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String account;

    private String nickname;

    private String avatarUrl;

    private String password;

    private String phone;

    private Integer campusId;

    private Integer role;

    private String address;

    private String createdAt;

    private Double accountBalance;

}
