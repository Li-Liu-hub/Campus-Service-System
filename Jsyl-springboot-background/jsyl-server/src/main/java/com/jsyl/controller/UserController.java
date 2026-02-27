package com.jsyl.controller;

import com.jsyl.constant.JwtClaimsConstant;
import com.jsyl.constant.MessageConstant;
import com.jsyl.context.BaseContext;
import com.jsyl.dto.UserChangePasswordDTO;
import com.jsyl.dto.UserDTO;
import com.jsyl.dto.UserRegisterDTO;
import com.jsyl.dto.UserUpdateDTO;
import com.jsyl.entity.User;
import com.jsyl.service.UserService;
import com.jsyl.result.Result;
import com.jsyl.utils.JwtUtil;
import com.jsyl.vo.UserLoginVo;
import com.jsyl.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/jsyl/common")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result<UserLoginVo> login(@RequestBody UserDTO userDTO) {
        log.info("用户登录请求：{}", userDTO.getAccount());

        User user = userService.Login(userDTO);
        if (user == null) {
            return Result.error("账号密码不存在");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());

        String token = jwtUtil.createUserJWT(claims);

        UserLoginVo userLoginVo = UserLoginVo.builder()
                .id(user.getId())
                .account(user.getAccount())
                .token(token)
                .role(user.getRole() != null ? user.getRole() : 1)
                .build();

        log.info("用户{}登录成功，生成token：{}", user.getAccount(), token);
        return Result.success(userLoginVo);
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        log.info("用户注册请求：{}", userRegisterDTO.getAccount());
        userService.register(userRegisterDTO);
        log.info("用户{}注册成功", userRegisterDTO.getAccount());
        return Result.success(MessageConstant.REGISTER_SUCCESS);
    }

    @PostMapping("/logout")
    public Result<String> logout() {
        log.info("用户登出请求");
        return Result.success(MessageConstant.LOGOUT_SUCCESS);
    }

    @GetMapping("/userInfo")
    public Result<UserVO> getUserInfo() {
        log.info("获取用户信息请求");
        Long userIdLong = BaseContext.getCurrentId();
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        if (userId == null) {
            return Result.error("用户未登录");
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setUsername(user.getNickname());

        if (user.getCampusId() != null) {
            userVO.setCampusId(user.getCampusId());
            String campusName = userService.getCampusNameById(user.getCampusId());
            userVO.setCampusName(campusName);
        }

        return Result.success(userVO);
    }

    @PostMapping("/updateUserInfo")
    public Result<String> updateUserInfo(@RequestBody UserUpdateDTO userUpdateDTO) {
        log.info("更新用户信息请求：{}", userUpdateDTO);
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        User user = userService.getUserById(userId.intValue());
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (userUpdateDTO.getUsername() != null) {
            user.setNickname(userUpdateDTO.getUsername());
        }
        if (userUpdateDTO.getPhone() != null) {
            user.setPhone(userUpdateDTO.getPhone());
        }
        if (userUpdateDTO.getCampusId() != null) {
            user.setCampusId(userUpdateDTO.getCampusId());
        }
        userService.updateUserInfo(user);
        return Result.success(MessageConstant.USER_INFO_UPDATED_SUCCESS);
    }

    @PostMapping("/changePassword")
    public Result<String> changePassword(@RequestBody UserChangePasswordDTO userChangePasswordDTO) {
        log.info("修改密码请求");
        Integer userId = 1;
        userService.changePassword(userId, userChangePasswordDTO.getOldPassword(), userChangePasswordDTO.getNewPassword());
        return Result.success(MessageConstant.PASSWORD_CHANGED_SUCCESS);
    }
}
