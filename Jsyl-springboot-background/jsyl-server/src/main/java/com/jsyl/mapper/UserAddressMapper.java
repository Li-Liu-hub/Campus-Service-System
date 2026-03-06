package com.jsyl.mapper;

import com.jsyl.entity.UserAddress;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserAddressMapper {

    @Insert("INSERT INTO user_address (user_id, contact_name, contact_phone, address_detail, is_default) " +
            "VALUES (#{userId}, #{contactName}, #{contactPhone}, #{addressDetail}, #{isDefault})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(UserAddress userAddress);

    @Select("SELECT id, user_id as userId, contact_name as contactName, contact_phone as contactPhone, " +
            "address_detail as addressDetail, is_default as isDefault, create_time as createTime " +
            "FROM user_address WHERE user_id = #{userId} ORDER BY is_default DESC, create_time DESC")
    List<UserAddress> getByUserId(Integer userId);

    @Select("SELECT id, user_id as userId, contact_name as contactName, contact_phone as contactPhone, " +
            "address_detail as addressDetail, is_default as isDefault, create_time as createTime " +
            "FROM user_address WHERE id = #{id}")
    UserAddress getById(Long id);

    @Update("UPDATE user_address SET contact_name = #{contactName}, contact_phone = #{contactPhone}, " +
            "address_detail = #{addressDetail}, is_default = #{isDefault} WHERE id = #{id}")
    void update(UserAddress userAddress);

    @Delete("DELETE FROM user_address WHERE id = #{id}")
    void deleteById(Long id);

    @Update("UPDATE user_address SET is_default = 0 WHERE user_id = #{userId}")
    void setNonDefault(Integer userId);
}
