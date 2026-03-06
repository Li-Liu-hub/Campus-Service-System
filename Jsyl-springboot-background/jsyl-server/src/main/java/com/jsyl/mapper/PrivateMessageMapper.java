package com.jsyl.mapper;

import com.jsyl.entity.PrivateMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PrivateMessageMapper {

    @Insert("INSERT INTO private_message (conversation_id, sender_id, receiver_id, content, msg_type, is_read, create_time) " +
            "VALUES (#{conversationId}, #{senderId}, #{receiverId}, #{content}, #{msgType}, #{isRead}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(PrivateMessage message);

    @Select("SELECT id, conversation_id as conversationId, sender_id as senderId, receiver_id as receiverId, " +
            "content, msg_type as msgType, is_read as isRead, create_time as createTime " +
            "FROM private_message WHERE conversation_id = #{conversationId} ORDER BY create_time ASC")
    List<PrivateMessage> getByConversationId(Long conversationId);

    @Select("SELECT id, conversation_id as conversationId, sender_id as senderId, receiver_id as receiverId, " +
            "content, msg_type as msgType, is_read as isRead, create_time as createTime " +
            "FROM private_message WHERE (sender_id = #{userId1} AND receiver_id = #{userId2}) OR (sender_id = #{userId2} AND receiver_id = #{userId1}) " +
            "ORDER BY create_time ASC")
    List<PrivateMessage> getByUserIds(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);

    @Update("UPDATE private_message SET is_read = 1 WHERE receiver_id = #{receiverId} AND sender_id = #{senderId} AND is_read = 0")
    void markAsRead(@Param("receiverId") Integer receiverId, @Param("senderId") Integer senderId);

    @Select("SELECT COUNT(*) FROM private_message WHERE receiver_id = #{userId} AND is_read = 0")
    Integer countUnreadByUserId(Integer userId);

}
