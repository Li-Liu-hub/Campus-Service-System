package com.jsyl.service.impl;

import com.jsyl.constant.MessageConstant;
import com.jsyl.dto.UserDTO;
import com.jsyl.dto.UserRegisterDTO;
import com.jsyl.entity.User;
import com.jsyl.exception.AccountAlreadyExistsException;
import com.jsyl.exception.AccountNotFoundException;
import com.jsyl.exception.ParameterValidationException;
import com.jsyl.exception.PasswordErrorException;
import com.jsyl.mapper.UserMapper;
import com.jsyl.service.UserService;
import com.jsyl.utils.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User Login(UserDTO userDTO) {
        User user = userMapper.Login(userDTO);

        if (user == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        String dbPassword = user.getPassword();
        String inputPassword = userDTO.getPassword();
        if (dbPassword == null || !passwordEncoder.matches(inputPassword, dbPassword)) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
        return user;
    }

    @Override
    @Transactional
    public void register(UserRegisterDTO userRegisterDTO) {
        validateRegisterParams(userRegisterDTO);

        if (userMapper.getByAccount(userRegisterDTO.getAccount()) != null) {
            throw new AccountAlreadyExistsException(MessageConstant.ACCOUNT_ALREADY_EXISTS);
        }

        User user = User.builder()
                .account(userRegisterDTO.getAccount())
                .nickname(userRegisterDTO.getAccount())
                .password(passwordEncoder.encode(userRegisterDTO.getPassword()))
                .phone(userRegisterDTO.getPhone())
                .permission(0)
                .build();

        userMapper.insert(user);
    }

    private void validateRegisterParams(UserRegisterDTO userRegisterDTO) {
        if (!ValidationUtil.isValidUsername(userRegisterDTO.getAccount())) {
            throw new ParameterValidationException(MessageConstant.USERNAME_FORMAT_ERROR);
        }

        if (!ValidationUtil.isValidPassword(userRegisterDTO.getPassword())) {
            throw new ParameterValidationException(MessageConstant.PASSWORD_STRENGTH_ERROR);
        }

        if (!ValidationUtil.isValidPhone(userRegisterDTO.getPhone())) {
            throw new ParameterValidationException(MessageConstant.PHONE_FORMAT_ERROR);
        }
    }

    @Override
    public User getUserById(Integer id) {
        return userMapper.getById(id);
    }

    @Override
    @Transactional
    public void updateUserInfo(User user) {
        userMapper.update(user);
    }

    @Override
    @Transactional
    public void changePassword(Integer id, String oldPassword, String newPassword) {
        User user = userMapper.getById(id);
        if (user == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
        if (!ValidationUtil.isValidPassword(newPassword)) {
            throw new ParameterValidationException(MessageConstant.PASSWORD_STRENGTH_ERROR);
        }
        userMapper.updatePassword(id, passwordEncoder.encode(newPassword));
    }
}
