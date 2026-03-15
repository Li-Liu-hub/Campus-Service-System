package com.jsyl.controller;

import com.jsyl.annotation.RateLimit;
import com.jsyl.constant.MessageConstant;
import com.jsyl.context.BaseContext;
import com.jsyl.dto.CommentPublishDTO;
import com.jsyl.result.Result;
import com.jsyl.service.CommentService;
import com.jsyl.vo.CommentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jsyl/home/comment")
@Slf4j
@Api(tags = "评论相关接口")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/publish")
    @ApiOperation("发布评论")
    @RateLimit(key = "rate_limit:comment:", time = 60, count = 10, message = "评论过于频繁，请60秒后再试")
    public Result<String> publish(@RequestBody CommentPublishDTO commentPublishDTO) {
        Integer userId = 2;
        try {
            userId = BaseContext.getCurrentId().intValue();
        } catch (Exception e) {
        }
        commentService.publish(commentPublishDTO, userId);
        return Result.success(MessageConstant.COMMENT_PUBLISHED_SUCCESS);
    }

    @GetMapping("/post/{postId}")
    @ApiOperation("获取帖子评论列表")
    public Result<List<CommentVO>> getByPostId(@PathVariable Long postId) {
        List<CommentVO> comments = commentService.getByPostId(postId);
        return Result.success(comments);
    }

    @PutMapping("/update/{id}")
    @ApiOperation("更新评论")
    public Result<String> update(@PathVariable Long id, @RequestBody String content) {
        Integer userId = BaseContext.getCurrentId().intValue();
        commentService.update(id, content, userId);
        return Result.success(MessageConstant.COMMENT_UPDATED_SUCCESS);
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除评论")
    public Result<String> delete(@PathVariable Long id) {
        Integer userId = BaseContext.getCurrentId().intValue();
        commentService.delete(id, userId);
        return Result.success(MessageConstant.COMMENT_DELETED_SUCCESS);
    }

}
