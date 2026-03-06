package com.jsyl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserChangePasswordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String oldPassword;

    private String newPassword;

}
