package com.jsyl.controller;

import com.jsyl.constant.MessageConstant;
import com.jsyl.context.BaseContext;
import com.jsyl.dto.PostPageQueryDTO;
import com.jsyl.dto.PostPublishDTO;
import com.jsyl.entity.Post;
import com.jsyl.entity.User;
import com.jsyl.result.PageResult;
import com.jsyl.result.Result;
import com.jsyl.service.PostService;
import com.jsyl.service.UserService;
import com.jsyl.vo.PostDetailVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jsyl/home/post")
@Slf4j
@Api(tags = "帖子相关接口")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @PostMapping("/publish")
    @ApiOperation("发布帖子")
    public Result<String> publish(@RequestBody PostPublishDTO postPublishDTO) {
        log.info("发布帖子：{}", postPublishDTO);
        Long userIdLong = BaseContext.getCurrentId();
        if (userIdLong == null) {
            return Result.error("用户未登录");
        }
        Integer userId = userIdLong.intValue();
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (user.getPermission() != null && user.getPermission() == 0) {
            return Result.error("您的账号已被禁用，无法发布帖子");
        }
        postService.publish(postPublishDTO, userId);
        return Result.success(MessageConstant.POST_PUBLISHED_SUCCESS);
    }

    @GetMapping("/detail/{id}")
    @ApiOperation("获取帖子详情")
    public Result<PostDetailVO> getDetail(@PathVariable Long id) {
        log.info("获取帖子详情：{}", id);
        PostDetailVO postDetailVO = postService.getDetail(id);
        return Result.success(postDetailVO);
    }

    @PutMapping("/update/{id}")
    @ApiOperation("更新帖子")
    public Result<String> update(@PathVariable Long id, @RequestBody PostPublishDTO postPublishDTO) {
        log.info("更新帖子：id={}, dto={}", id, postPublishDTO);
        Integer userId = 2;
        try {
            userId = BaseContext.getCurrentId().intValue();
        } catch (Exception e) {
            log.warn("使用默认用户ID 2");
        }
        postService.update(id, postPublishDTO, userId);
        return Result.success(MessageConstant.POST_UPDATED_SUCCESS);
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除帖子")
    public Result<String> delete(@PathVariable Long id) {
        log.info("删除帖子：{}", id);
        postService.deleteByAdmin(id);
        return Result.success(MessageConstant.POST_DELETED_SUCCESS);
    }

    @GetMapping("/page")
    @ApiOperation("分页查询帖子")
    public Result<PageResult> page(PostPageQueryDTO postPageQueryDTO) {
        log.info("分页查询帖子：{}", postPageQueryDTO);
        PageResult pageResult = postService.pageQuery(postPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/myPosts")
    @ApiOperation("获取我发起的帖子")
    public Result<List<Post>> getMyPosts() {
        Long userId = BaseContext.getCurrentId();
        log.info("获取我发起的帖子：userId={}", userId);
        List<Post> posts = postService.getMyPosts(userId);
        return Result.success(posts);
    }

}
