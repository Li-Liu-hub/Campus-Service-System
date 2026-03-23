package com.jsyl.module.forum.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jsyl.common.constant.MessageConstant;
import com.jsyl.common.exception.UserNotFoundException;
import com.jsyl.model.forum.dto.PostPageQueryDTO;
import com.jsyl.model.forum.dto.PostPublishDTO;
import com.jsyl.model.forum.entity.Post;
import com.jsyl.model.user.entity.User;
import com.jsyl.common.exception.PermissionDeniedException;
import com.jsyl.common.exception.PostNotFoundException;
import com.jsyl.model.user.vo.UserPostVO;
import com.jsyl.module.forum.mapper.PostMapper;
import com.jsyl.module.user.mapper.UserMapper;
import com.jsyl.common.result.PageResult;
import com.jsyl.module.forum.service.PostService;
import com.jsyl.module.admin.service.SensitiveWordService;
import com.jsyl.common.utils.HtmlSanitizerUtil;
import com.jsyl.model.forum.vo.PostDetailVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SensitiveWordService sensitiveWordService;

    private static final String POST_DETAIL_CACHE_KEY = "post:detail:";
    // 缓存基础过期时间：30分钟
    private static final long CACHE_BASE_EXPIRE = 30;
    // 随机波动时间：0-10分钟
    private static final long CACHE_RANDOM_EXPIRE = 10;
    @Autowired
    private RedisTemplate<String, Object> redisObjectTemplate;

    @Override
    @Transactional
    public void publish(PostPublishDTO postPublishDTO, Integer userId) {
        if (postPublishDTO.getTitle() == null || postPublishDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException(MessageConstant.POST_TITLE_EMPTY);
        }
        if (postPublishDTO.getContent() == null || postPublishDTO.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException(MessageConstant.POST_CONTENT_EMPTY);
        }

        if (sensitiveWordService.containsSensitiveWord(postPublishDTO.getTitle())) {
            List<String> words = sensitiveWordService.getSensitiveWords(postPublishDTO.getTitle());
            throw new IllegalArgumentException("标题包含敏感词：" + String.join(", ", words));
        }

        if (sensitiveWordService.containsSensitiveWord(postPublishDTO.getContent())) {
            List<String> words = sensitiveWordService.getSensitiveWords(postPublishDTO.getContent());
            throw new IllegalArgumentException("内容包含敏感词：" + String.join(", ", words));
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
        String cacheKey = POST_DETAIL_CACHE_KEY + id;
        /*先去缓存中查询postDetailVO是否存在*/
        PostDetailVO postDetailVO = (PostDetailVO) redisObjectTemplate.opsForValue().get(cacheKey);
        if(postDetailVO ==null){
            Post post = postMapper.getById(id);
            if (post == null) {
                throw new PostNotFoundException(MessageConstant.POST_NOT_FOUND);
            }
            postDetailVO = new PostDetailVO();
            BeanUtils.copyProperties(post, postDetailVO);
            Integer userId = post.getUserId();
            User user = userMapper.getById(userId);
            if(user == null){
                throw new UserNotFoundException(MessageConstant.USER_NOT_FOUND);
            }
            UserPostVO userPostVO = new UserPostVO();
            BeanUtils.copyProperties(user, userPostVO);
            postDetailVO.setAuthor(userPostVO);
            long randomExpire = CACHE_BASE_EXPIRE + (long) (Math.random() * CACHE_RANDOM_EXPIRE);
            redisObjectTemplate.opsForValue().set(cacheKey, postDetailVO, randomExpire, TimeUnit.MINUTES);
        }
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

        String cacheKey = POST_DETAIL_CACHE_KEY + id;

        // 1. 删数据库
        postMapper.deleteById(id);

        // 2. 立即删缓存
        redisObjectTemplate.delete(cacheKey);

        // 3. 延迟再删一次缓存
        new Thread(() -> {
            try {
                Thread.sleep(500);
                redisObjectTemplate.delete(cacheKey);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
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
    public List<Post> getMyPosts(Integer userId) {
        return postMapper.getMyPosts(userId, 5);
    }

}
