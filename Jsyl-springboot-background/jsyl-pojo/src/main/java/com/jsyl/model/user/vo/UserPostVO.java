package com.jsyl.model.user.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPostVO {

    private Integer id;

    private String nickname;

    private String avatarUrl;


}
