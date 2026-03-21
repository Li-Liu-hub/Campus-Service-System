package com.jsyl.module.forum.mapper;

import com.jsyl.model.forum.dto.PostPageQueryDTO;
import com.jsyl.model.forum.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PostMapper {

    void insert(Post post);

    Post getById(Long id);

    void update(Post post);

    void deleteById(Long id);

    @Update("UPDATE post_info SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(Long id);

    @Update("UPDATE post_info SET reply_count = reply_count + 1 WHERE id = #{id}")
    void incrementReplyCount(Long id);

    @Update("UPDATE post_info SET reply_count = reply_count - 1 WHERE id = #{id}")
    void decrementReplyCount(Long id);

    List<Post> pageQuery(PostPageQueryDTO postPageQueryDTO);

    @Select("SELECT * FROM post_info WHERE user_id = #{userId} ORDER BY create_time DESC LIMIT #{limit}")
    List<Post> getMyPosts(@Param("userId") Integer userId, @Param("limit") Integer limit);

    @Select("SELECT p.*, u.nickname as author FROM post_info p LEFT JOIN user_info u ON p.user_id = u.id WHERE p.deleted_at IS NULL ORDER BY p.create_time DESC LIMIT #{limit}")
    List<Post> getRecentPosts(@Param("limit") Integer limit);

    @Select("SELECT COUNT(*) FROM post_info WHERE deleted_at IS NULL")
    Long countAll();

}
