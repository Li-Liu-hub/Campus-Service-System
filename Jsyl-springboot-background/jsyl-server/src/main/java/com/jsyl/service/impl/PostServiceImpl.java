package com.jsyl.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jsyl.constant.MessageConstant;
import com.jsyl.context.BaseContext;
import com.jsyl.dto.PostPageQueryDTO;
import com.jsyl.dto.PostPublishDTO;
import com.jsyl.entity.Post;
import com.jsyl.entity.User;
import com.jsyl.exception.PermissionDeniedException;
import com.jsyl.exception.PostNotFoundException;
import com.jsyl.mapper.PostMapper;
import com.jsyl.mapper.UserMapper;
import com.jsyl.result.PageResult;
import com.jsyl.service.PostService;
import com.jsyl.utils.HtmlSanitizerUtil;
import com.jsyl.vo.PostDetailVO;
import com.jsyl.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public void publish(PostPublishDTO postPublishDTO, Integer userId) {
        if (postPublishDTO.getTitle() == null || postPublishDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException(MessageConstant.POST_TITLE_EMPTY);
        }
        if (postPublishDTO.getContent() == null || postPublishDTO.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException(MessageConstant.POST_CONTENT_EMPTY);
        }

        Post post = Post.builder()
                .userId(userId)
                .title(HtmlSanitizerUtil.sanitize(postPublishDTO.getTitle()))
                .content(HtmlSanitizerUtil.sanitize(postPublishDTO.getContent()))
                .viewCount(0)
                .replyCount(0)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        postMapper.insert(post);
    }

    @Override
    @Transactional
    public PostDetailVO getDetail(Long id) {
        Post post = postMapper.getById(id);
        if (post == null) {
            throw new PostNotFoundException(MessageConstant.POST_NOT_FOUND);
        }

        postMapper.incrementViewCount(id);

        User author = userMapper.getById(post.getUserId());
        UserVO authorVO = UserVO.builder()
                .id(author.getId())
                .account(author.getAccount())
                .nickname(author.getNickname())
                .phone(author.getPhone())
                .permission(author.getPermission())
                .address(author.getAddress())
                .build();

        PostDetailVO postDetailVO = new PostDetailVO();
        BeanUtils.copyProperties(post, postDetailVO);
        postDetailVO.setAuthor(authorVO);

        return postDetailVO;
    }

    @Override
    @Transactional
    public void update(Long id, PostPublishDTO postPublishDTO, Integer userId) {
        Post post = postMapper.getById(id);
        if (post == null) {
            throw new PostNotFoundException(MessageConstant.POST_NOT_FOUND);
        }
        if (!post.getUserId().equals(userId)) {
            throw new PermissionDeniedException(MessageConstant.PERMISSION_DENIED);
        }
        if (postPublishDTO.getTitle() == null || postPublishDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException(MessageConstant.POST_TITLE_EMPTY);
        }
        if (postPublishDTO.getContent() == null || postPublishDTO.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException(MessageConstant.POST_CONTENT_EMPTY);
        }

        post.setTitle(HtmlSanitizerUtil.sanitize(postPublishDTO.getTitle()));
        post.setContent(HtmlSanitizerUtil.sanitize(postPublishDTO.getContent()));
        post.setUpdateTime(LocalDateTime.now());

        postMapper.update(post);
    }

    @Override
    @Transactional
    public void delete(Long id, Integer userId) {
        Post post = postMapper.getById(id);
        if (post == null) {
            throw new PostNotFoundException(MessageConstant.POST_NOT_FOUND);
        }
        if (!post.getUserId().equals(userId)) {
            throw new PermissionDeniedException(MessageConstant.PERMISSION_DENIED);
        }

        postMapper.deleteById(id);
    }

    @Override
    public void deleteByAdmin(Long id) {
        Post post = postMapper.getById(id);
        if (post == null) {
            throw new PostNotFoundException(MessageConstant.POST_NOT_FOUND);
        }
        postMapper.deleteById(id);
    }

    @Override
    public PageResult pageQuery(PostPageQueryDTO postPageQueryDTO) {
        // 1. 开启分页
        PageHelper.startPage(postPageQueryDTO.getPage(), postPageQueryDTO.getPageSize());

        // 2. 执行查询，先用 List 接收
        List<Post> list = postMapper.pageQuery(postPageQueryDTO);

        // 3. 强制类型转换 (因为 PageHelper 返回的 List 实际上是 Page 的子类)
        Page<Post> page = (Page<Post>) list;

        // 4. 封装返回结果
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<Post> getMyPosts(Long userId) {
        return postMapper.getMyPosts(userId.intValue(), 5);
    }

}
