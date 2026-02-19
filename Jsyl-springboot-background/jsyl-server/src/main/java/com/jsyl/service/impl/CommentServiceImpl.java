package com.jsyl.service.impl;

import com.jsyl.constant.MessageConstant;
import com.jsyl.dto.CommentPublishDTO;
import com.jsyl.entity.Comment;
import com.jsyl.entity.Post;
import com.jsyl.entity.User;
import com.jsyl.exception.CommentNotFoundException;
import com.jsyl.exception.PermissionDeniedException;
import com.jsyl.exception.PostNotFoundException;
import com.jsyl.mapper.CommentMapper;
import com.jsyl.mapper.PostMapper;
import com.jsyl.mapper.UserMapper;
import com.jsyl.service.CommentService;
import com.jsyl.utils.HtmlSanitizerUtil;
import com.jsyl.vo.CommentVO;
import com.jsyl.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public void publish(CommentPublishDTO commentPublishDTO, Integer userId) {
        if (commentPublishDTO.getContent() == null || commentPublishDTO.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException(MessageConstant.COMMENT_CONTENT_EMPTY);
        }
        if (commentPublishDTO.getPostId() == null) {
            throw new IllegalArgumentException("所属帖子ID不能为空");
        }

        Post post = postMapper.getById(commentPublishDTO.getPostId());
        if (post == null) {
            throw new PostNotFoundException(MessageConstant.POST_NOT_FOUND);
        }

        Long parentId = commentPublishDTO.getParentId() != null ? commentPublishDTO.getParentId() : 0L;
        if (parentId > 0) {
            Comment parentComment = commentMapper.getByPostIdAndParentId(commentPublishDTO.getPostId(), parentId);
            if (parentComment == null) {
                throw new CommentNotFoundException(MessageConstant.PARENT_COMMENT_NOT_FOUND);
            }
        }

        Comment comment = Comment.builder()
                .postId(commentPublishDTO.getPostId())
                .parentId(parentId)
                .userId(userId)
                .content(HtmlSanitizerUtil.sanitize(commentPublishDTO.getContent()))
                .createTime(LocalDateTime.now())
                .build();

        commentMapper.insert(comment);
        postMapper.incrementReplyCount(commentPublishDTO.getPostId());
    }

    @Override
    public Comment getById(Long id) {
        return commentMapper.getById(id);
    }

    @Override
    @Transactional
    public void update(Long id, String content, Integer userId) {
        Comment comment = commentMapper.getById(id);
        if (comment == null) {
            throw new CommentNotFoundException(MessageConstant.COMMENT_NOT_FOUND);
        }
        if (!comment.getUserId().equals(userId)) {
            throw new PermissionDeniedException(MessageConstant.PERMISSION_DENIED);
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException(MessageConstant.COMMENT_CONTENT_EMPTY);
        }

        comment.setContent(HtmlSanitizerUtil.sanitize(content));
        commentMapper.update(comment);
    }

    @Override
    @Transactional
    public void delete(Long id, Integer userId) {
        Comment comment = commentMapper.getById(id);
        if (comment == null) {
            throw new CommentNotFoundException(MessageConstant.COMMENT_NOT_FOUND);
        }
        if (!comment.getUserId().equals(userId)) {
            throw new PermissionDeniedException(MessageConstant.PERMISSION_DENIED);
        }

        commentMapper.deleteById(id);
        postMapper.decrementReplyCount(comment.getPostId());
    }

    @Override
    public List<CommentVO> getByPostId(Long postId) {
        List<Comment> comments = commentMapper.getByPostId(postId);
        List<CommentVO> result = new ArrayList<>();

        for (Comment comment : comments) {
            CommentVO commentVO = new CommentVO();
            BeanUtils.copyProperties(comment, commentVO);
            result.add(commentVO);
        }

        return result;
    }

}
