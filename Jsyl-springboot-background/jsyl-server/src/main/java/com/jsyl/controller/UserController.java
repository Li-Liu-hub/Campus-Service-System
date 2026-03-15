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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/jsyl/common")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisObjectTemplate;

    // 用户信息缓存 key
    private static final String USER_INFO_CACHE_KEY = "user:info:";
    // 缓存过期时间：30分钟
    private static final long USER_INFO_CACHE_EXPIRE = 30;
    // 延迟双删的延迟时间：500毫秒
    private static final long DELAY_DELETE_TIME = 500;

    @PostMapping("/login")
    public Result<UserLoginVo> login(@RequestBody UserDTO userDTO) {
        User user = userService.Login(userDTO);
        if (user == null) {
            return Result.error("账号密码不存在");
        }

        if (user.getRole() != null && user.getRole() == 0) {
            return Result.error("账号已被禁用，请联系管理员");
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

        return Result.success(userLoginVo);
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        userService.register(userRegisterDTO);
        return Result.success(MessageConstant.REGISTER_SUCCESS);
    }

    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success(MessageConstant.LOGOUT_SUCCESS);
    }

    @GetMapping("/userInfo")
    public Result<UserVO> getUserInfo() {
        Long userIdLong = BaseContext.getCurrentId();
        Integer userId = userIdLong != null ? userIdLong.intValue() : null;
        if (userId == null) {
            return Result.error("用户未登录");
        }

        // 尝试从缓存获取
        String cacheKey = USER_INFO_CACHE_KEY + userId;
        UserVO cachedUserVO = (UserVO) redisObjectTemplate.opsForValue().get(cacheKey);
        if (cachedUserVO != null) {
            log.info("从缓存获取用户信息: userId={}", userId);
            return Result.success(cachedUserVO);
        }

        // 缓存不存在，查询数据库
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

        // 存入缓存
        redisObjectTemplate.opsForValue().set(cacheKey, userVO, USER_INFO_CACHE_EXPIRE, TimeUnit.MINUTES);
        log.info("用户信息存入缓存: userId={}", userId);

        return Result.success(userVO);
    }

    @PostMapping("/updateUserInfo")
    public Result<String> updateUserInfo(@RequestBody UserUpdateDTO userUpdateDTO) {
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        Integer userIdInt = userId.intValue();
        User user = userService.getUserById(userIdInt);
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

        // 延迟双删策略
        // 1. 先删除缓存
        String cacheKey = USER_INFO_CACHE_KEY + userIdInt;
        redisObjectTemplate.delete(cacheKey);
        log.info("延迟双删：第一次删除缓存 userId={}", userIdInt);

        // 2. 更新数据库
        userService.updateUserInfo(user);

        // 3. 延迟后再次删除缓存
        delayedDelete(cacheKey);

        return Result.success(MessageConstant.USER_INFO_UPDATED_SUCCESS);
    }

    /**
     * 延迟删除缓存
     */
    @Async
    public void delayedDelete(String cacheKey) {
        try {
            Thread.sleep(DELAY_DELETE_TIME);
            redisObjectTemplate.delete(cacheKey);
            log.info("延迟双删：第二次删除缓存 key={}", cacheKey);
        } catch (InterruptedException e) {
            log.error("延迟删除缓存失败", e);
            Thread.currentThread().interrupt();
        }
    }

    @PostMapping("/changePassword")
    public Result<String> changePassword(@RequestBody UserChangePasswordDTO userChangePasswordDTO) {
        Long userIdLong = BaseContext.getCurrentId();
        if (userIdLong == null) {
            return Result.error("用户未登录");
        }
        Integer userId = userIdLong.intValue();
        userService.changePassword(userId, userChangePasswordDTO.getOldPassword(), userChangePasswordDTO.getNewPassword());
        return Result.success(MessageConstant.PASSWORD_CHANGED_SUCCESS);
    }
}
