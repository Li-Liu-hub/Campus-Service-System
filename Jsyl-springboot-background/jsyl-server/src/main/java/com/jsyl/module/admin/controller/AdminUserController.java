package com.jsyl.module.admin.controller;

import com.jsyl.common.constant.RoleEnum;
import com.jsyl.common.context.BaseContext;
import com.jsyl.model.user.entity.User;
import com.jsyl.common.result.Result;
import com.jsyl.module.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/user")
@Api(tags = "管理员用户管理")
@Slf4j
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    @ApiOperation("获取用户列表")
    public Result<List<User>> getUserList() {
        List<User> users = userService.getAllUsers();
        return Result.success(users);
    }

    @GetMapping("/{id}")
    @ApiOperation("获取用户详情")
    public Result<User> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return Result.success(user);
    }

    @PutMapping("/role")
    @ApiOperation("修改用户角色")
    public Result<String> updateUserRole(@RequestParam Integer userId, @RequestParam Integer role) {

        if (role < 0 || role > 4) {
            return Result.error("无效的角色值");
        }

        Long currentUserId = BaseContext.getCurrentId();
        User currentUser = userService.getUserById(currentUserId != null ? currentUserId.intValue() : null);

        if (currentUser == null) {
            return Result.error("用户未登录");
        }

        RoleEnum currentRole = RoleEnum.fromCode(currentUser.getRole() != null ? currentUser.getRole() : 1);
        RoleEnum targetRole = RoleEnum.fromCode(role);

        if (!currentRole.canEditUser()) {
            return Result.error("权限不足，只有管理员及以上权限才能修改用户角色");
        }

        userService.updateUserRole(userId, role);

        return Result.success("修改成功");
    }

    @PutMapping("/status")
    @ApiOperation("启用/禁用用户")
    public Result<String> updateUserStatus(@RequestParam Integer userId, @RequestParam Integer status) {

        Long currentUserId = BaseContext.getCurrentId();

        if (currentUserId == null) {
            return Result.error("无权限操作");
        }

        User currentUser = userService.getUserById(currentUserId.intValue());

        if (currentUser == null) {
            return Result.error("无权限操作");
        }

        RoleEnum currentRole = RoleEnum.fromCode(currentUser.getRole() != null ? currentUser.getRole() : 1);

        if (!currentRole.canEditUser()) {
            return Result.error("权限不足，只有管理员及以上权限才能修改用户状态");
        }

        userService.updateUserStatus(userId, status);

        return Result.success("修改成功");
    }
}
