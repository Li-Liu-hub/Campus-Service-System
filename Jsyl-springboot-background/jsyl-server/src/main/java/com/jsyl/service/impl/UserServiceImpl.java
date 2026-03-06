package com.jsyl.service.impl;

import com.jsyl.constant.MessageConstant;
import com.jsyl.dto.UserDTO;
import com.jsyl.dto.UserRegisterDTO;
import com.jsyl.entity.User;
import com.jsyl.exception.*;
import com.jsyl.mapper.CampusInfoMapper;
import com.jsyl.mapper.UserMapper;
import com.jsyl.service.SensitiveWordService;
import com.jsyl.service.UserService;
import com.jsyl.utils.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CampusInfoMapper campusInfoMapper;

    @Autowired
    private SensitiveWordService sensitiveWordService;

    public User Login(UserDTO userDTO) {
        User user = userMapper.Login(userDTO);

        if (user == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        String dbPassword = user.getPassword();
        String inputPassword = userDTO.getPassword();
        if (dbPassword == null) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }
        boolean isBcrypt = dbPassword.startsWith("$2a$") || dbPassword.startsWith("$2b$") || dbPassword.startsWith("$2y$");
        if (isBcrypt) {
            if (!passwordEncoder.matches(inputPassword, dbPassword)) {
                throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
            }
        } else {
            if (!inputPassword.equals(dbPassword)) {
                throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
            }
            userMapper.updatePassword(user.getId(), passwordEncoder.encode(inputPassword));
        }
        return user;
    }

    @Override
    @Transactional
    public void register(UserRegisterDTO userRegisterDTO) {
        validateRegisterParams(userRegisterDTO);

        String account = userRegisterDTO.getAccount();
        String username = userRegisterDTO.getUsername();
        if (account == null || account.isEmpty()) {
            account = username;
        }
        
        if (userMapper.getByAccount(account) != null) {
            throw new AccountAlreadyExistsException(MessageConstant.ACCOUNT_ALREADY_EXISTS);
        }

        String nickname = (username != null && !username.isEmpty()) ? username : account;

        User user = User.builder()
                .account(account)
                .nickname(nickname)
                .password(passwordEncoder.encode(userRegisterDTO.getPassword()))
                .phone(userRegisterDTO.getPhone())
                .role(1)
                .build();

        userMapper.insert(user);
    }

    private void validateRegisterParams(UserRegisterDTO userRegisterDTO) {
        String account = userRegisterDTO.getAccount();
        String username = userRegisterDTO.getUsername();

        if ((account == null || account.isEmpty()) && (username == null || username.isEmpty())) {
            throw new ParameterValidationException("请输入用户名");
        }

        String checkAccount = (account != null && !account.isEmpty()) ? account : username;
        if (!ValidationUtil.isValidUsername(checkAccount)) {
            throw new ParameterValidationException(MessageConstant.USERNAME_FORMAT_ERROR);
        }

        if (sensitiveWordService.containsSensitiveWord(checkAccount)) {
            throw new ParameterValidationException("用户名包含敏感词，请重新输入");
        }

        if (!ValidationUtil.isValidPassword(userRegisterDTO.getPassword())) {
            throw new ParameterValidationException(MessageConstant.PASSWORD_STRENGTH_ERROR);
        }

        String phone = userRegisterDTO.getPhone();
        if (phone != null && !phone.isEmpty() && !ValidationUtil.isValidPhone(phone)) {
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

    @Override
    public List<User> getAllUsers() {
        return userMapper.getAllUsers();
    }

    @Override
    @Transactional
    public void updateUserRole(Integer userId, Integer role) {
        User user = userMapper.getById(userId);
        if (user == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        user.setRole(role);
        userMapper.update(user);
    }

    @Override
    @Transactional
    public void updateUserStatus(Integer userId, Integer status) {
        User user = userMapper.getById(userId);
        if (user == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        user.setRole(status);
        userMapper.update(user);
    }

    @Override
    public String getCampusNameById(Integer campusId) {
        if (campusId == null) {
            return null;
        }
        return campusInfoMapper.getCampusNameById(campusId);
    }

    @Override
    @Transactional
    public void updateAvatar(Integer userId, String avatarUrl) {
        User user = userMapper.getById(userId);
        if (user == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        user.setAvatarUrl(avatarUrl);
        userMapper.update(user);
    }
}
