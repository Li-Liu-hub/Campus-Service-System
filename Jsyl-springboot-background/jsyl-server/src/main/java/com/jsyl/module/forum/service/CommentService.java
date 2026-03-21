package com.jsyl.module.forum.service;

import com.jsyl.model.forum.dto.CommentPublishDTO;
import com.jsyl.model.forum.entity.Comment;
import com.jsyl.model.forum.vo.CommentVO;

import java.util.List;

public interface CommentService {

    void publish(CommentPublishDTO commentPublishDTO, Integer userId);

    Comment getById(Long id);

    void update(Long id, String content, Integer userId);

    void delete(Long id, Integer userId);

    List<CommentVO> getByPostId(Long postId);

}
