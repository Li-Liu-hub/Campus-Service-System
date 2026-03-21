package com.jsyl.module.user.service;

import com.jsyl.model.user.dto.UserDTO;
import com.jsyl.model.user.dto.UserRegisterDTO;
import com.jsyl.model.user.entity.User;

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

    void updateAvatar(Integer userId, String avatarUrl);
}
