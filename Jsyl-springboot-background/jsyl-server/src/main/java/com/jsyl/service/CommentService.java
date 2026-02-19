package com.jsyl.service;

import com.jsyl.dto.CommentPublishDTO;
import com.jsyl.entity.Comment;
import com.jsyl.vo.CommentVO;

import java.util.List;

public interface CommentService {

    void publish(CommentPublishDTO commentPublishDTO, Integer userId);

    Comment getById(Long id);

    void update(Long id, String content, Integer userId);

    void delete(Long id, Integer userId);

    List<CommentVO> getByPostId(Long postId);

}
