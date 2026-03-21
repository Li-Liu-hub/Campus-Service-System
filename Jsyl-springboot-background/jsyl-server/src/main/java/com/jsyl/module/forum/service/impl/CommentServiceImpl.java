package com.jsyl.module.forum.service.impl;

import com.jsyl.common.constant.MessageConstant;
import com.jsyl.model.forum.dto.CommentPublishDTO;
import com.jsyl.model.forum.entity.Comment;
import com.jsyl.model.notification.entity.Notification;
import com.jsyl.model.forum.entity.Post;
import com.jsyl.model.user.entity.User;
import com.jsyl.common.exception.CommentNotFoundException;
import com.jsyl.common.exception.PermissionDeniedException;
import com.jsyl.common.exception.PostNotFoundException;
import com.jsyl.module.forum.mapper.CommentMapper;
import com.jsyl.module.forum.mapper.PostMapper;
import com.jsyl.module.user.mapper.UserMapper;
import com.jsyl.module.forum.service.CommentService;
import com.jsyl.module.notification.service.NotificationService;
import com.jsyl.common.utils.HtmlSanitizerUtil;
import com.jsyl.model.forum.vo.CommentVO;
import com.jsyl.model.user.vo.UserVO;
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

    @Autowired
    private NotificationService notificationService;

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
        Comment parentComment = null;
        if (parentId > 0) {
            parentComment = commentMapper.getByPostIdAndParentId(commentPublishDTO.getPostId(), parentId);
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

        // 发送通知
        // 如果是回复评论，通知被回复的评论作者
        if (parentComment != null && !parentComment.getUserId().equals(userId)) {
            Notification notification = Notification.builder()
                    .userId(parentComment.getUserId())
                    .type(2) // 评论通知
                    .title("收到新回复")
                    .content("您的评论收到了新的回复")
                    .relatedId(commentPublishDTO.getPostId())
                    .isRead(0)
                    .createTime(LocalDateTime.now())
                    .build();
            notificationService.createNotification(notification);
        }
        // 如果是评论帖子，通知帖子作者
        else if (parentId == 0 && !post.getUserId().equals(userId)) {
            Notification notification = Notification.builder()
                    .userId(post.getUserId())
                    .type(2) // 评论通知
                    .title("收到新评论")
                    .content("您的帖子《" + post.getTitle() + "》收到了新的评论")
                    .relatedId(commentPublishDTO.getPostId())
                    .isRead(0)
                    .createTime(LocalDateTime.now())
                    .build();
            notificationService.createNotification(notification);
        }
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
