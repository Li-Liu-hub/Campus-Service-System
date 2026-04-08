package com.jsyl.module.operation.controller;

import com.jsyl.common.context.BaseContext;
import com.jsyl.common.result.Result;
import com.jsyl.module.operation.service.SignInService;
import com.jsyl.model.operation.vo.SignInVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/jsyl/home/signin")
@Slf4j
public class SignInController {

    @Autowired
    private SignInService signInService;

    @PostMapping
    public Result<SignInVO> signIn() {
        Integer userId = BaseContext.getCurrentId().intValue();
        SignInVO signInVO = signInService.signIn(userId);
        return Result.success(signInVO);
    }

    @GetMapping("/status")
    public Result<SignInVO> getSignInStatus() {
        Integer userId = BaseContext.getCurrentId().intValue();
        SignInVO signInVO = signInService.getSignInStatus(userId);
        return Result.success(signInVO);
    }

    @GetMapping("/calendar")
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
