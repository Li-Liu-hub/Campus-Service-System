package com.jsyl.mapper;

import com.jsyl.entity.Conversation;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ConversationMapper {

    @Insert("INSERT INTO conversation (user_id, target_user_id, last_message, unread_count, create_time, update_time) " +
            "VALUES (#{userId}, #{targetUserId}, #{lastMessage}, #{unreadCount}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Conversation conversation);

    @Select("SELECT c.id, c.user_id as userId, c.target_user_id as targetUserId, c.last_message as lastMessage, " +
            "c.unread_count as unreadCount, c.create_time as createTime, c.update_time as updateTime, " +
            "u.nickname as targetUserNickname " +
            "FROM conversation c LEFT JOIN user_info u ON c.target_user_id = u.id WHERE c.user_id = #{userId} ORDER BY c.update_time DESC")
    List<Conversation> getByUserId(Integer userId);

    @Select("SELECT c.id, c.user_id as userId, c.target_user_id as targetUserId, c.last_message as lastMessage, " +
            "c.unread_count as unreadCount, c.create_time as createTime, c.update_time as updateTime " +
            "FROM conversation c WHERE c.user_id = #{userId} AND c.target_user_id = #{targetUserId}")
    Conversation getByUserIdAndTargetUserId(@Param("userId") Integer userId, @Param("targetUserId") Integer targetUserId);

    @Update("UPDATE conversation SET last_message = #{lastMessage}, unread_count = #{unreadCount}, update_time = #{updateTime} WHERE id = #{id}")
    void update(Conversation conversation);

    @Update("UPDATE conversation SET unread_count = 0 WHERE user_id = #{userId} AND target_user_id = #{targetUserId}")
    void resetUnreadCount(@Param("userId") Integer userId, @Param("targetUserId") Integer targetUserId);

    @Delete("DELETE FROM conversation WHERE id = #{id}")
    void deleteById(Long id);

}
