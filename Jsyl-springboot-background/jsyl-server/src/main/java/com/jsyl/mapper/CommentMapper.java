package com.jsyl.mapper;

import com.jsyl.entity.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {

    @Insert("INSERT INTO comment_info (post_id, parent_id, user_id, content, create_time) " +
            "VALUES (#{postId}, #{parentId}, #{userId}, #{content}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Comment comment);

    @Select("SELECT c.id, c.post_id as postId, c.parent_id as parentId, c.user_id as userId, c.content, c.create_time as createTime, " +
            "u.nickname as userNickname " +
            "FROM comment_info c LEFT JOIN user_info u ON c.user_id = u.id WHERE c.id = #{id}")
    Comment getById(Long id);

    @Update("UPDATE comment_info SET content = #{content} WHERE id = #{id}")
    void update(Comment comment);

    @Delete("DELETE FROM comment_info WHERE id = #{id}")
    void deleteById(Long id);

    @Select("SELECT c.id, c.post_id as postId, c.parent_id as parentId, c.user_id as userId, c.content, c.create_time as createTime, " +
            "u.nickname as userNickname " +
            "FROM comment_info c LEFT JOIN user_info u ON c.user_id = u.id WHERE c.post_id = #{postId} ORDER BY c.create_time DESC")
    List<Comment> getByPostId(Long postId);

    @Select("SELECT c.id, c.post_id as postId, c.parent_id as parentId, c.user_id as userId, c.content, c.create_time as createTime, " +
            "u.nickname as userNickname " +
            "FROM comment_info c LEFT JOIN user_info u ON c.user_id = u.id WHERE c.post_id = #{postId} AND c.parent_id = 0 ORDER BY c.create_time DESC")
    List<Comment> getTopLevelByPostId(Long postId);

    @Select("SELECT c.id, c.post_id as postId, c.parent_id as parentId, c.user_id as userId, c.content, c.create_time as createTime, " +
            "u.nickname as userNickname " +
            "FROM comment_info c LEFT JOIN user_info u ON c.user_id = u.id WHERE c.parent_id = #{parentId} ORDER BY c.create_time ASC")
    List<Comment> getRepliesByParentId(Long parentId);

    @Select("SELECT c.id, c.post_id as postId, c.parent_id as parentId, c.user_id as userId, c.content, c.create_time as createTime, " +
            "u.nickname as userNickname " +
            "FROM comment_info c LEFT JOIN user_info u ON c.user_id = u.id WHERE c.post_id = #{postId} AND c.id = #{parentId}")
    Comment getByPostIdAndParentId(Long postId, Long parentId);

}
