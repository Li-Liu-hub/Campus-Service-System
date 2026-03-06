package com.jsyl.service;

import com.jsyl.vo.SignInVO;

import java.time.LocalDate;
import java.util.List;

public interface SignInService {

    SignInVO signIn(Integer userId);

    SignInVO getSignInStatus(Integer userId);

    List<LocalDate> getMonthSignInDates(Integer userId, int year, int month);
}
