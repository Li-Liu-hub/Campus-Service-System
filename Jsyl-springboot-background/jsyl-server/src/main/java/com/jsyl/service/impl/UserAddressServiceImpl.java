package com.jsyl.service.impl;

import com.jsyl.entity.UserAddress;
import com.jsyl.mapper.UserAddressMapper;
import com.jsyl.service.UserAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Override
    @Transactional
    public void add(UserAddress userAddress, Integer userId) {
        userAddress.setUserId(userId);
        if (userAddress.getIsDefault() == null) {
            userAddress.setIsDefault(0);
        }
        if (userAddress.getIsDefault() == 1) {
            userAddressMapper.setNonDefault(userId);
        }
        userAddressMapper.insert(userAddress);
    }

    @Override
    public List<UserAddress> getList(Integer userId) {
        return userAddressMapper.getByUserId(userId);
    }

    @Override
    @Transactional
    public void update(UserAddress userAddress, Integer userId) {
        UserAddress existing = userAddressMapper.getById(userAddress.getId());
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new IllegalArgumentException("地址不存在或无权修改");
        }
        if (userAddress.getIsDefault() != null && userAddress.getIsDefault() == 1) {
            userAddressMapper.setNonDefault(userId);
        }
        userAddressMapper.update(userAddress);
    }

    @Override
    public void delete(Long id, Integer userId) {
        UserAddress existing = userAddressMapper.getById(id);
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new IllegalArgumentException("地址不存在或无权删除");
        }
        userAddressMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void setDefault(Long id, Integer userId) {
        UserAddress existing = userAddressMapper.getById(id);
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new IllegalArgumentException("地址不存在或无权设置");
        }
        userAddressMapper.setNonDefault(userId);
        existing.setIsDefault(1);
        userAddressMapper.update(existing);
    }
}
