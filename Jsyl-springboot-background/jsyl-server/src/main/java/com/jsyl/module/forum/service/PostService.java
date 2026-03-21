package com.jsyl.module.forum.service;

import com.jsyl.model.forum.dto.PostPageQueryDTO;
import com.jsyl.model.forum.dto.PostPublishDTO;
import com.jsyl.model.forum.entity.Post;
import com.jsyl.common.result.PageResult;
import com.jsyl.model.forum.vo.PostDetailVO;

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
