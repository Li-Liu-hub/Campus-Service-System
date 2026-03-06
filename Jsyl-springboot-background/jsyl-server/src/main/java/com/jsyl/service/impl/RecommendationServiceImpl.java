package com.jsyl.service.impl;

import com.jsyl.entity.*;
import com.jsyl.mapper.*;
import com.jsyl.service.RecommendationService;
import com.jsyl.utils.RecommendationEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private OrderCenterMapper orderCenterMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private RecommendationEngine recommendationEngine;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String HOT_ORDERS_KEY = "recommend:hot:orders";
    private static final String USER_BEHAVIOR_KEY = "recommend:user:behavior:";
    private static final String ORDER_HOT_SCORE_KEY = "recommend:order:hot:";

    @Override
    public List<Order> getRecommendedOrders(Integer userId, int limit) {
        String cacheKey = "recommend:orders:" + userId;
        List<Order> cachedOrders = (List<Order>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedOrders != null) {
            return cachedOrders.stream().limit(limit).collect(Collectors.toList());
        }

        List<Order> allOrders = orderCenterMapper.getAllActiveOrders();
        if (allOrders.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Double> scores = new HashMap<>();
        for (Order order : allOrders) {
            double score = calculateOrderScore(order, userId);
            scores.put(order.getId(), score);
        }

        List<Long> recommendedIds = recommendationEngine.getTopNRecommendations(scores, limit * 2);
        List<Order> recommendedOrders = allOrders.stream()
                .filter(order -> recommendedIds.contains(order.getId()))
                .sorted(Comparator.comparingInt(order -> recommendedIds.indexOf(order.getId())))
                .limit(limit)
                .collect(Collectors.toList());

        redisTemplate.opsForValue().set(cacheKey, recommendedOrders, 10, TimeUnit.MINUTES);
        return recommendedOrders;
    }

    @Override
    public List<Post> getRecommendedPosts(Integer userId, int limit) {
        String cacheKey = "recommend:posts:" + userId;
        List<Post> cachedPosts = (List<Post>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedPosts != null) {
            return cachedPosts.stream().limit(limit).collect(Collectors.toList());
        }

        List<Post> allPosts = postMapper.getRecentPosts(100);
        if (allPosts.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Double> scores = new HashMap<>();
        for (Post post : allPosts) {
            double score = calculatePostScore(post, userId);
            scores.put(post.getId(), score);
        }

        List<Long> recommendedIds = recommendationEngine.getTopNRecommendations(scores, limit * 2);
        List<Post> recommendedPosts = allPosts.stream()
                .filter(post -> recommendedIds.contains(post.getId()))
                .sorted(Comparator.comparingInt(post -> recommendedIds.indexOf(post.getId())))
                .limit(limit)
                .collect(Collectors.toList());

        redisTemplate.opsForValue().set(cacheKey, recommendedPosts, 10, TimeUnit.MINUTES);
        return recommendedPosts;
    }

    @Override
    public List<Order> getHotOrders(int limit) {
        List<Order> cachedOrders = (List<Order>) redisTemplate.opsForValue().get(HOT_ORDERS_KEY);
        if (cachedOrders != null) {
            return cachedOrders.stream().limit(limit).collect(Collectors.toList());
        }

        List<Order> allOrders = orderCenterMapper.getAllActiveOrders();
        Map<Long, Double> hotScores = new HashMap<>();

        for (Order order : allOrders) {
            String scoreKey = ORDER_HOT_SCORE_KEY + order.getId();
            Double cachedScore = (Double) redisTemplate.opsForValue().get(scoreKey);

            if (cachedScore != null) {
                hotScores.put(order.getId(), cachedScore);
            } else {
                double score = recommendationEngine.calculateHotScore(
                        0, 0, 0, order.getCreateTime()
                );
                hotScores.put(order.getId(), score);
                redisTemplate.opsForValue().set(scoreKey, score, 1, TimeUnit.HOURS);
            }
        }

        List<Long> hotIds = recommendationEngine.getTopNRecommendations(hotScores, limit);
        List<Order> hotOrders = allOrders.stream()
                .filter(order -> hotIds.contains(order.getId()))
                .sorted(Comparator.comparingInt(order -> hotIds.indexOf(order.getId())))
                .collect(Collectors.toList());

        redisTemplate.opsForValue().set(HOT_ORDERS_KEY, hotOrders, 5, TimeUnit.MINUTES);
        return hotOrders;
    }

    @Override
    public void recordBehavior(Integer userId, String itemType, Long itemId, String behaviorType) {
        String key = USER_BEHAVIOR_KEY + userId + ":" + itemType;
        redisTemplate.opsForSet().add(key, itemId);
        redisTemplate.expire(key, 7, TimeUnit.DAYS);

        if ("order".equals(itemType)) {
            updateHotScore(itemId);
        }
    }

    @Override
    public void updateHotScore(Long orderId) {
        String scoreKey = ORDER_HOT_SCORE_KEY + orderId;
        redisTemplate.delete(scoreKey);
        redisTemplate.delete(HOT_ORDERS_KEY);
    }

    private double calculateOrderScore(Order order, Integer userId) {
        double score = 0.0;

        long hoursSinceCreation = java.time.temporal.ChronoUnit.HOURS.between(
                order.getCreateTime(), LocalDateTime.now()
        );
        double freshnessScore = Math.max(0, 100 - hoursSinceCreation);
        score += freshnessScore * 0.3;

        String behaviorKey = USER_BEHAVIOR_KEY + userId + ":order";
        Set<Object> viewedOrders = redisTemplate.opsForSet().members(behaviorKey);
        if (viewedOrders != null && !viewedOrders.contains(order.getId())) {
            score += 20;
        }

        if (order.getCampusId() != null) {
            score += 30;
        }

        Random random = new Random();
        score += random.nextDouble() * 10;

        return score;
    }

    private double calculatePostScore(Post post, Integer userId) {
        double score = 0.0;

        long hoursSinceCreation = java.time.temporal.ChronoUnit.HOURS.between(
                post.getCreateTime(), LocalDateTime.now()
        );
        double freshnessScore = Math.max(0, 100 - hoursSinceCreation);
        score += freshnessScore * 0.3;

        score += post.getViewCount() * 0.1;
        score += post.getReplyCount() * 2;

        String behaviorKey = USER_BEHAVIOR_KEY + userId + ":post";
        Set<Object> viewedPosts = redisTemplate.opsForSet().members(behaviorKey);
        if (viewedPosts != null && !viewedPosts.contains(post.getId())) {
            score += 20;
        }

        Random random = new Random();
        score += random.nextDouble() * 10;

        return score;
    }
}
