package com.jsyl.controller;

import com.jsyl.entity.UserAddress;
import com.jsyl.result.Result;
import com.jsyl.service.UserAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jsyl/home/userAddress")
@Slf4j
@Api(tags = "用户地址相关接口")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    @PostMapping("/add")
    @ApiOperation("添加地址")
    public Result<String> add(@RequestBody UserAddress userAddress) {
        log.info("添加地址：{}", userAddress);
        Integer userId = 2; // 暂时使用固定用户ID
        userAddressService.add(userAddress, userId);
        return Result.success("添加成功");
    }

    @GetMapping("/list")
    @ApiOperation("获取地址列表")
    public Result<List<UserAddress>> getList() {
        log.info("获取地址列表");
        Integer userId = 2; // 暂时使用固定用户ID
        List<UserAddress> list = userAddressService.getList(userId);
        return Result.success(list);
    }

    @PutMapping("/update")
    @ApiOperation("更新地址")
    public Result<String> update(@RequestBody UserAddress userAddress) {
        log.info("更新地址：{}", userAddress);
        Integer userId = 2; // 暂时使用固定用户ID
        userAddressService.update(userAddress, userId);
        return Result.success("更新成功");
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除地址")
    public Result<String> delete(@PathVariable Long id) {
        log.info("删除地址：{}", id);
        Integer userId = 2; // 暂时使用固定用户ID
        userAddressService.delete(id, userId);
        return Result.success("删除成功");
    }

    @PostMapping("/setDefault/{id}")
    @ApiOperation("设置默认地址")
    public Result<String> setDefault(@PathVariable Long id) {
        log.info("设置默认地址：{}", id);
        Integer userId = 2; // 暂时使用固定用户ID
        userAddressService.setDefault(id, userId);
        return Result.success("设置成功");
    }
}
