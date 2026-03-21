package com.jsyl.module.user.service;

import com.jsyl.model.user.entity.UserAddress;

import java.util.List;

public interface UserAddressService {

    void add(UserAddress userAddress, Integer userId);

    List<UserAddress> getList(Integer userId);

    void update(UserAddress userAddress, Integer userId);

    void delete(Long id, Integer userId);

    void setDefault(Long id, Integer userId);
}
