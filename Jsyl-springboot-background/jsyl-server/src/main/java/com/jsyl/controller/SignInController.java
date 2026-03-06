package com.jsyl.controller;

import com.jsyl.context.BaseContext;
import com.jsyl.result.Result;
import com.jsyl.service.SignInService;
import com.jsyl.vo.SignInVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/jsyl/home/signin")
@Api(tags = "签到相关接口")
@Slf4j
public class SignInController {

    @Autowired
    private SignInService signInService;

    @PostMapping
    @ApiOperation("每日签到")
    public Result<SignInVO> signIn() {
        Integer userId = BaseContext.getCurrentId().intValue();
        SignInVO signInVO = signInService.signIn(userId);
        return Result.success(signInVO);
    }

    @GetMapping("/status")
    @ApiOperation("获取签到状态")
    public Result<SignInVO> getSignInStatus() {
        Integer userId = BaseContext.getCurrentId().intValue();
        SignInVO signInVO = signInService.getSignInStatus(userId);
        return Result.success(signInVO);
    }

    @GetMapping("/calendar")
    @ApiOperation("获取月度签到日历")
    public Result<List<LocalDate>> getMonthSignInDates(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        Integer userId = BaseContext.getCurrentId().intValue();

        if (year == null || month == null) {
            LocalDate now = LocalDate.now();
            year = now.getYear();
            month = now.getMonthValue();
        }

        List<LocalDate> dates = signInService.getMonthSignInDates(userId, year, month);
        return Result.success(dates);
    }
}
