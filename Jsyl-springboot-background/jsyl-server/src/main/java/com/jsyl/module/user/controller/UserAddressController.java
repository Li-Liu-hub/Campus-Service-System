package com.jsyl.module.user.controller;

import com.jsyl.model.user.entity.UserAddress;
import com.jsyl.common.result.Result;
import com.jsyl.module.user.service.UserAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jsyl/home/userAddress")
@Slf4j
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    @PostMapping("/add")
    public Result<String> add(@RequestBody UserAddress userAddress) {
        Integer userId = 2; // 暂时使用固定用户ID
        userAddressService.add(userAddress, userId);
        return Result.success("添加成功");
    }

    @GetMapping("/list")
    public Result<List<UserAddress>> getList() {
        Integer userId = 2; // 暂时使用固定用户ID
        List<UserAddress> list = userAddressService.getList(userId);
        return Result.success(list);
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody UserAddress userAddress) {
        Integer userId = 2; // 暂时使用固定用户ID
        userAddressService.update(userAddress, userId);
        return Result.success("更新成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        Integer userId = 2; // 暂时使用固定用户ID
        userAddressService.delete(id, userId);
        return Result.success("删除成功");
    }

    @PostMapping("/setDefault/{id}")
    public Result<String> setDefault(@PathVariable Long id) {
        Integer userId = 2; // 暂时使用固定用户ID
        userAddressService.setDefault(id, userId);
        return Result.success("设置成功");
    }
}
