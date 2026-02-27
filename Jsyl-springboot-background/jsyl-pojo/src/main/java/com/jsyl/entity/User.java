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
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一标识
     */
    private Integer id;

    /**
     * 用户账号
     */
    private String account;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 校区ID
     */
    private Integer campusId;

    /**
     * 用户权限：0-普通用户 1-管理员
     */
    private Integer permission;

    /**
     * 用户角色：0-被限制, 1-普通用户, 2-VIP, 3-管理员, 4-超级管理员
     */
    private Integer role;

    /**
     * 用户收货地址
     */
    private String address;

    /**
     * 创建时间
     */
    private String createdAt;

    /**
     * 账户余额
     */
    private Double accountBalance;

}
