package com.jsyl.controller;

import com.jsyl.context.BaseContext;
import com.jsyl.entity.Order;
import com.jsyl.entity.Post;
import com.jsyl.result.Result;
import com.jsyl.service.RecommendationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jsyl/home/recommend")
@Api(tags = "推荐算法接口")
@Slf4j
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/orders")
    @ApiOperation("获取推荐订单")
    public Result<List<Order>> getRecommendedOrders(@RequestParam(defaultValue = "10") Integer limit) {
        Integer userId = BaseContext.getCurrentId().intValue();
        List<Order> orders = recommendationService.getRecommendedOrders(userId, limit);
        return Result.success(orders);
    }

    @GetMapping("/posts")
    @ApiOperation("获取推荐帖子")
    public Result<List<Post>> getRecommendedPosts(@RequestParam(defaultValue = "10") Integer limit) {
        Integer userId = BaseContext.getCurrentId().intValue();
        List<Post> posts = recommendationService.getRecommendedPosts(userId, limit);
        return Result.success(posts);
    }

    @GetMapping("/hot-orders")
    @ApiOperation("获取热门订单")
    public Result<List<Order>> getHotOrders(@RequestParam(defaultValue = "10") Integer limit) {
        List<Order> orders = recommendationService.getHotOrders(limit);
        return Result.success(orders);
    }

    @PostMapping("/behavior")
    @ApiOperation("记录用户行为")
    public Result<String> recordBehavior(@RequestParam String itemType,
                                         @RequestParam Long itemId,
                                         @RequestParam String behaviorType) {
        Integer userId = BaseContext.getCurrentId().intValue();
        recommendationService.recordBehavior(userId, itemType, itemId, behaviorType);
        return Result.success("记录成功");
    }
}
