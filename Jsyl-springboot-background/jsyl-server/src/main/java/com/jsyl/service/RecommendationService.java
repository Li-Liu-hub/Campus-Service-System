package com.jsyl.service;

import com.jsyl.entity.Order;
import com.jsyl.entity.Post;

import java.util.List;

public interface RecommendationService {

    List<Order> getRecommendedOrders(Integer userId, int limit);

    List<Post> getRecommendedPosts(Integer userId, int limit);

    List<Order> getHotOrders(int limit);

    void recordBehavior(Integer userId, String itemType, Long itemId, String behaviorType);

    void updateHotScore(Long orderId);
}
