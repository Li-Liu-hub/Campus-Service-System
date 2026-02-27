package com.jsyl.mapper;

import com.jsyl.dto.UserDTO;
import com.jsyl.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * from user_info where account = #{account}")
    User Login(UserDTO userDTO);

    @Select("SELECT * from user_info where account = #{account}")
    User getByAccount(String account);

    @Select("SELECT * from user_info where email = #{email}")
    User getByEmail(String email);

    @Select("SELECT * from user_info where id = #{id}")
    User getById(Integer id);

    @Insert("INSERT INTO user_info (account, nickname, password, phone, permission, role) "
            + "VALUES (#{account}, #{nickname}, #{password}, #{phone}, #{permission}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Update("UPDATE user_info SET nickname = #{nickname}, phone = #{phone}, role = #{role} WHERE id = #{id}")
    void update(User user);

    @Update("UPDATE user_info SET password = #{password} WHERE id = #{id}")
    void updatePassword(Integer id, String password);

    @Select("SELECT * FROM user_info")
    List<User> getAllUsers();
}
