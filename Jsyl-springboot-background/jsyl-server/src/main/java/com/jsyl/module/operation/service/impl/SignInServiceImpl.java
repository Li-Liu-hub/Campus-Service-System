package com.jsyl.module.operation.service.impl;

import com.jsyl.model.operation.entity.SignIn;
import com.jsyl.module.operation.mapper.SignInMapper;
import com.jsyl.module.operation.service.SignInService;
import com.jsyl.model.operation.vo.SignInVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SignInServiceImpl implements SignInService {

    @Autowired
    private SignInMapper signInMapper;

    @Override
    @Transactional
    public SignInVO signIn(Integer userId) {
        LocalDate today = LocalDate.now();

        SignIn todaySignIn = signInMapper.getByUserIdAndDate(userId, today);
        if (todaySignIn != null) {
            throw new RuntimeException("今天已经签到过了");
        }

        SignIn lastSignIn = signInMapper.getLastSignIn(userId);

        int continuousDays = 1;
        int totalDays = signInMapper.getTotalDays(userId) + 1;

        if (lastSignIn != null) {
            long daysBetween = ChronoUnit.DAYS.between(lastSignIn.getSignDate(), today);
            if (daysBetween == 1) {
                continuousDays = lastSignIn.getContinuousDays() + 1;
            }
        }

        SignIn signIn = SignIn.builder()
                .userId(userId)
                .signDate(today)
                .continuousDays(continuousDays)
                .totalDays(totalDays)
                .build();

        signInMapper.insert(signIn);

        return SignInVO.builder()
                .signedToday(true)
                .continuousDays(continuousDays)
                .totalDays(totalDays)
                .lastSignDate(today)
                .build();
    }

    @Override
    public SignInVO getSignInStatus(Integer userId) {
        LocalDate today = LocalDate.now();
        SignIn todaySignIn = signInMapper.getByUserIdAndDate(userId, today);
        SignIn lastSignIn = signInMapper.getLastSignIn(userId);

        boolean signedToday = todaySignIn != null;
        int continuousDays = 0;
        int totalDays = signInMapper.getTotalDays(userId);
        LocalDate lastSignDate = null;

        if (lastSignIn != null) {
            lastSignDate = lastSignIn.getSignDate();
            if (signedToday) {
                continuousDays = lastSignIn.getContinuousDays();
            } else {
                long daysBetween = ChronoUnit.DAYS.between(lastSignIn.getSignDate(), today);
                if (daysBetween == 1) {
                    continuousDays = lastSignIn.getContinuousDays();
                }
            }
        }

        return SignInVO.builder()
                .signedToday(signedToday)
                .continuousDays(continuousDays)
                .totalDays(totalDays)
                .lastSignDate(lastSignDate)
                .build();
    }

    @Override
    public List<LocalDate> getMonthSignInDates(Integer userId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<SignIn> signInList = signInMapper.getSignInList(userId, startDate, endDate);

        return signInList.stream()
                .map(SignIn::getSignDate)
                .collect(Collectors.toList());
    }
}
