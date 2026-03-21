package com.jsyl.module.trade.service;

import com.jsyl.model.trade.entity.Order;
import com.jsyl.model.forum.entity.Post;

import java.util.List;

public interface RecommendationService {

    List<Order> getRecommendedOrders(Integer userId, int limit);

    List<Post> getRecommendedPosts(Integer userId, int limit);

    List<Order> getHotOrders(int limit);

    void recordBehavior(Integer userId, String itemType, Long itemId, String behaviorType);

    void updateHotScore(Long orderId);
}
