package com.jsyl.service;

import com.jsyl.dto.PostPageQueryDTO;
import com.jsyl.dto.PostPublishDTO;
import com.jsyl.entity.Post;
import com.jsyl.result.PageResult;
import com.jsyl.vo.PostDetailVO;

import java.util.List;

public interface PostService {

    void publish(PostPublishDTO postPublishDTO, Integer userId);

    PostDetailVO getDetail(Long id);

    void update(Long id, PostPublishDTO postPublishDTO, Integer userId);

    void delete(Long id, Integer userId);

    void deleteByAdmin(Long id);

    PageResult pageQuery(PostPageQueryDTO postPageQueryDTO);

    List<Post> getMyPosts(Long userId);

}
