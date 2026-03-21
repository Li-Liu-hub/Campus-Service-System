package com.jsyl.module.operation.mapper;

import com.jsyl.model.operation.entity.SignIn;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface SignInMapper {

    @Select("SELECT * FROM sign_in WHERE user_id = #{userId} AND sign_date = #{signDate}")
    SignIn getByUserIdAndDate(@Param("userId") Integer userId, @Param("signDate") LocalDate signDate);

    @Select("SELECT * FROM sign_in WHERE user_id = #{userId} ORDER BY sign_date DESC LIMIT 1")
    SignIn getLastSignIn(@Param("userId") Integer userId);

    @Insert("INSERT INTO sign_in (user_id, sign_date, continuous_days, total_days) " +
            "VALUES (#{userId}, #{signDate}, #{continuousDays}, #{totalDays})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(SignIn signIn);

    @Select("SELECT COUNT(*) FROM sign_in WHERE user_id = #{userId}")
    Integer getTotalDays(@Param("userId") Integer userId);

    @Select("SELECT * FROM sign_in WHERE user_id = #{userId} " +
            "AND sign_date >= #{startDate} AND sign_date <= #{endDate} " +
            "ORDER BY sign_date DESC")
    List<SignIn> getSignInList(@Param("userId") Integer userId,
                               @Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);
}
