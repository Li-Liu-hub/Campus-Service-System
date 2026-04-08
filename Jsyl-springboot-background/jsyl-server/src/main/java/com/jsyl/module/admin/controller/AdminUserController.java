package com.jsyl.module.admin.controller;

import com.jsyl.common.annotation.RequireRole;
import com.jsyl.common.enumeration.RoleEnum;
import com.jsyl.model.user.entity.User;
import com.jsyl.common.result.Result;
import com.jsyl.module.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/user")
@Slf4j
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Result<List<User>> getUserList() {
        List<User> users = userService.getAllUsers();
        return Result.success(users);
    }

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return Result.success(user);
    }

    @RequireRole(RoleEnum.SUPER_ADMIN)
    @PutMapping("/role")
    public Result<String> updateUserRole(@RequestParam Integer userId, @RequestParam Integer role) {

        userService.updateUserRole(userId, role);

        return Result.success("修改成功");
    }

    @RequireRole(RoleEnum.SUPER_ADMIN)
    @PutMapping("/status")
    public Result<String> updateUserStatus(@RequestParam Integer userId, @RequestParam Integer status) {

        userService.updateUserStatus(userId, status);

        return Result.success("修改成功");
    }
}
