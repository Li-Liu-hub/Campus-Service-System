package com.jsyl.mapper;

import com.jsyl.entity.Notification;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotificationMapper {

    @Select("SELECT * FROM notification WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Notification> getByUserId(Integer userId);

    @Select("SELECT * FROM notification WHERE user_id = #{userId} AND type = #{type} ORDER BY create_time DESC")
    List<Notification> getByUserIdAndType(Integer userId, Integer type);

    @Select("SELECT * FROM notification WHERE user_id = #{userId} AND is_read = #{isRead} ORDER BY create_time DESC")
    List<Notification> getByUserIdAndIsRead(Integer userId, Integer isRead);

    @Select("SELECT * FROM notification WHERE user_id = #{userId} AND type = #{type} AND is_read = #{isRead} ORDER BY create_time DESC")
    List<Notification> getByUserIdAndTypeAndIsRead(Integer userId, Integer type, Integer isRead);

    @Select("SELECT COUNT(*) FROM notification WHERE user_id = #{userId} AND is_read = 0")
    Integer getUnreadCount(Integer userId);

    @Insert("INSERT INTO notification (user_id, type, title, content, related_id, is_read, create_time) " +
            "VALUES (#{userId}, #{type}, #{title}, #{content}, #{relatedId}, #{isRead}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Notification notification);

    @Update("UPDATE notification SET is_read = 1 WHERE id = #{id}")
    void markAsRead(Long id);

    @Update("UPDATE notification SET is_read = 1 WHERE user_id = #{userId} AND is_read = 0")
    void markAllAsRead(Integer userId);

    @Delete("DELETE FROM notification WHERE id = #{id}")
    void deleteById(Long id);

    @Insert("<script>" +
            "INSERT INTO notification (user_id, type, title, content, related_id, is_read, create_time) " +
            "VALUES " +
            "<foreach collection='notifications' item='item' separator=','>" +
            "(#{item.userId}, #{item.type}, #{item.title}, #{item.content}, #{item.relatedId}, #{item.isRead}, #{item.createTime})" +
            "</foreach>" +
            "</script>")
    void batchInsert(@Param("notifications") List<Notification> notifications);

    @Select("SELECT id FROM user_info")
    List<Integer> getAllUserIds();

}
