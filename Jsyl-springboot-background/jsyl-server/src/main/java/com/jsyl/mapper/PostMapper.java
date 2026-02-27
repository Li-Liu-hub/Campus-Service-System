package com.jsyl.mapper;

import com.jsyl.dto.PostPageQueryDTO;
import com.jsyl.entity.Post;
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

}
