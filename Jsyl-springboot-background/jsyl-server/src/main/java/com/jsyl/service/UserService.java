package com.jsyl.service;

import com.jsyl.dto.UserDTO;
import com.jsyl.dto.UserRegisterDTO;
import com.jsyl.entity.User;

import java.util.List;

public interface UserService {

    User Login(UserDTO userDTO);

    void register(UserRegisterDTO userRegisterDTO);

    User getUserById(Integer id);

    void updateUserInfo(User user);

    void changePassword(Integer id, String oldPassword, String newPassword);

    List<User> getAllUsers();

    void updateUserRole(Integer userId, Integer role);

    void updateUserStatus(Integer userId, Integer status);

    String getCampusNameById(Integer campusId);
}
