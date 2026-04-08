package com.jsyl.module.forum.controller;

import com.jsyl.common.annotation.RateLimit;
import com.jsyl.common.constant.MessageConstant;
import com.jsyl.common.utils.UserContextUtil;
import com.jsyl.model.forum.dto.PostPageQueryDTO;
import com.jsyl.model.forum.dto.PostPublishDTO;
import com.jsyl.model.forum.entity.Post;
import com.jsyl.model.user.entity.User;
import com.jsyl.common.result.PageResult;
import com.jsyl.common.result.Result;
import com.jsyl.module.forum.service.PostService;
import com.jsyl.module.user.service.UserService;
import com.jsyl.model.forum.vo.PostDetailVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jsyl/home/post")
@Slf4j
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisObjectTemplate;



    @PostMapping("/publish")
    @RateLimit(key = "rate_limit:post:", time = 60, count = 2, message = "发帖过于频繁，请60秒后再试")
    public Result<String> publish(@Valid @RequestBody PostPublishDTO postPublishDTO) {
        Integer userId = UserContextUtil.getCurrentUserId();
        User user = userService.getUserById(userId);
        if (user.getRole() != null && user.getRole() == 0) {
            return Result.error("您的账号已被禁用，无法发布帖子");
        }
        postService.publish(postPublishDTO, userId);
        return Result.success(MessageConstant.POST_PUBLISHED_SUCCESS);
    }

    @GetMapping("/detail/{id}")
    public Result<PostDetailVO> getDetail(@PathVariable Long id) {

        PostDetailVO postDetailVO  =  postService.getDetail(id);

        return Result.success(postDetailVO);
    }

    @PutMapping("/update/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody PostPublishDTO postPublishDTO) {
        Integer userId = UserContextUtil.getCurrentUserId();
        postService.update(id, postPublishDTO, userId);
        return Result.success(MessageConstant.POST_UPDATED_SUCCESS);
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        postService.deleteByAdmin(id);
        return Result.success(MessageConstant.POST_DELETED_SUCCESS);
    }

    @GetMapping("/page")
    public Result<PageResult> page(PostPageQueryDTO postPageQueryDTO) {
        PageResult pageResult = postService.pageQuery(postPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/myPosts")
    public Result<List<Post>> getMyPosts() {
        Integer userId = UserContextUtil.getCurrentUserId();
        List<Post> posts = postService.getMyPosts(userId);
        return Result.success(posts);
    }

}
