package com.jsyl.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

    private Integer id;

    private String account;

    private String username;

    private String nickname;

    private String phone;

    private Integer permission;

    private String address;

}
