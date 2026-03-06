# 推荐引擎 - RecommendationEngine

## 类简介

`RecommendationEngine` 是校园服务平台的推荐算法引擎，提供热门内容计算、用户相似度计算、内容相似度计算等功能。

## 核心功能

1. **热门分数计算** - 基于浏览量、成交量、评论数和时间衰减计算热门度
2. **用户相似度** - 基于用户行为计算用户之间的相似度（杰卡德相似度）
3. **内容相似度** - 基于标签计算内容之间的相似度
4. **TopN 推荐** - 获取评分最高的 N 个推荐

## 核心方法详解

### 1. 热门分数计算
```java
/**
 * 计算热门分数
 * 综合考虑浏览量、成交量、评论数，并加入时间衰减因子
 *
 * 公式：hotScore = (viewCount * 1.0 + acceptCount * 5.0 + commentCount * 3.0) * timeDecay
 * 时间衰减：每过一天，热度下降5%
 *
 * @param viewCount 浏览量
 * @param acceptCount 成交量/接单数
 * @param commentCount 评论数
 * @param createTime 发布时间
 * @return 热门分数
 */
public double calculateHotScore(int viewCount, int acceptCount, int commentCount, LocalDateTime createTime) {
    // 1. 权重设置
    double viewWeight = 1.0;     // 浏览权重
    double acceptWeight = 5.0;   // 接单权重（成交最重要）
    double commentWeight = 3.0;   // 评论权重

    // 2. 计算基础分数
    double baseScore = viewCount * viewWeight
                     + acceptCount * acceptWeight
                     + commentCount * commentWeight;

    // 3. 计算时间衰减因子
    // 计算发布时间距离现在的小时数
    long hoursSinceCreation = ChronoUnit.HOURS.between(createTime, LocalDateTime.now());
    // 每过1天(24小时)，衰减5%
    double timeDecay = Math.pow(0.95, hoursSinceCreation / 24.0);

    // 4. 计算最终分数
    return baseScore * timeDecay;
}
```

**分数计算示例：**

| 浏览量 | 接单数 | 评论数 | 发布时间 | 基础分数 | 时间衰减 | 最终分数 |
|--------|--------|--------|----------|----------|----------|----------|
| 100 | 10 | 20 | 1小时前 | 180 | 0.998 | 179.6 |
| 100 | 10 | 20 | 1天前 | 180 | 0.95 | 171.0 |
| 100 | 10 | 20 | 7天前 | 180 | 0.698 | 125.6 |

### 2. 用户相似度计算
```java
/**
 * 计算用户相似度
 * 使用杰卡德相似度系数（Jaccard Similarity）
 *
 * 公式：Jaccard(A, B) = |A ∩ B| / |A ∪ B|
 * A 和 B 是两个用户交互过的物品集合
 *
 * @param userItems 用户ID -> 物品ID集合 的映射
 * @return 用户ID -> 相似度 的映射
 */
public Map<Long, Double> calculateUserSimilarity(Map<Long, Set<Long>> userItems) {
    Map<Long, Double> similarity = new HashMap<>();

    // 遍历所有用户对
    for (Map.Entry<Long, Set<Long>> entry1 : userItems.entrySet()) {
        for (Map.Entry<Long, Set<Long>> entry2 : userItems.entrySet()) {
            // 跳过自己与自己的比较
            if (entry1.getKey().equals(entry2.getKey())) {
                continue;
            }

            Set<Long> items1 = entry1.getValue();  // 用户1的物品集合
            Set<Long> items2 = entry2.getValue();  // 用户2的物品集合

            // 计算交集
            Set<Long> intersection = new HashSet<>(items1);
            intersection.retainAll(items2);

            // 计算并集
            Set<Long> union = new HashSet<>(items1);
            union.addAll(items2);

            // 杰卡德相似度 = 交集大小 / 并集大小
            double jaccardSimilarity = union.isEmpty() ? 0
                : (double) intersection.size() / union.size();

            // 记录相似度
            similarity.put(entry2.getKey(), jaccardSimilarity);
        }
    }

    return similarity;
}
```

**杰卡德相似度示例：**

```
用户A浏览的订单：[1, 2, 3, 4]
用户B浏览的订单：[2, 3, 5, 6]

交集：[2, 3]
并集：[1, 2, 3, 4, 5, 6]

相似度 = 2 / 6 = 0.333
```

### 3. 内容相似度计算
```java
/**
 * 计算内容相似度
 * 基于标签的杰卡德相似度
 *
 * @param tags1 内容1的标签集合
 * @param tags2 内容2的标签集合
 * @return 相似度分数 (0-1)
 */
public double calculateContentSimilarity(Set<String> tags1, Set<String> tags2) {
    // 空标签集返回0
    if (tags1.isEmpty() || tags2.isEmpty()) {
        return 0.0;
    }

    // 计算交集
    Set<String> intersection = new HashSet<>(tags1);
    intersection.retainAll(tags2);

    // 计算并集
    Set<String> union = new HashSet<>(tags1);
    union.addAll(tags2);

    // 杰卡德相似度
    return union.isEmpty() ? 0 : (double) intersection.size() / union.size();
}
```

### 4. TopN 推荐
```java
/**
 * 获取评分最高的 N 个推荐
 *
 * @param scores 物品ID -> 评分 的映射
 * @param n 推荐数量
 * @return 评分最高的N个物品ID列表
 */
public List<Long> getTopNRecommendations(Map<Long, Double> scores, int n) {
    return scores.entrySet().stream()
            // 按评分降序排序
            .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
            // 取前N个
            .limit(n)
            // 提取ID
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
}
```

## 算法流程图

### 热门内容推荐流程
```
┌─────────────────────────────────────────────────────────┐
│              热门内容推荐流程                               │
└─────────────────────────────────────────────────────────┘

   数据输入
   - 浏览量、接单数、评论数
   - 发布时间
      │
      ▼
┌─────────────────┐
│  计算基础分数    │
│  view*1.0 +    │
│  accept*5.0 +  │
│  comment*3.0   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  时间衰减计算    │
│  0.95^(天数)   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  计算最终分数    │
│  基础分×衰减   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  按分数排序     │
│  取TopN        │
└────────┬────────┘
         │
         ▼
   返回推荐列表
```

## 使用示例

### 1. 计算订单热门度
```java
/**
 * 获取热门订单列表
 */
public List<Order> getHotOrders(int limit) {
    // 1. 查询所有订单
    List<Order> orders = orderMapper.getAll();

    // 2. 计算每个订单的热门分数
    Map<Long, Double> scores = new HashMap<>();
    for (Order order : orders) {
        double score = recommendationEngine.calculateHotScore(
            order.getViewCount() != null ? order.getViewCount() : 0,
            order.getAcceptCount() != null ? order.getAcceptCount() : 0,
            order.getCommentCount() != null ? order.getCommentCount() : 0,
            order.getCreateTime()
        );
        scores.put(order.getId(), score);
    }

    // 3. 获取TopN
    List<Long> topIds = recommendationEngine.getTopNRecommendations(scores, limit);

    // 4. 返回结果
    return orders.stream()
        .filter(o -> topIds.contains(o.getId()))
        .sorted(Comparator.comparingDouble(o -> -scores.get(o.getId())))
        .collect(Collectors.toList());
}
```

### 2. 个性化推荐
```java
/**
 * 基于用户行为的个性化推荐
 */
public List<Post> getPersonalizedRecommendations(Long userId, int limit) {
    // 1. 获取用户历史浏览的帖子类型
    List<UserBehavior> behaviors = userBehaviorMapper.getByUserId(userId);

    // 2. 统计用户偏好类型
    Map<Integer, Long> typePreference = behaviors.stream()
        .filter(b -> "view".equals(b.getBehaviorType()))
        .collect(Collectors.groupingBy(
            UserBehavior::getTargetType,
            Collectors.counting()
        ));

    // 3. 获取所有帖子
    List<Post> allPosts = postMapper.getActivePosts();

    // 4. 计算每个帖子的推荐分数
    Map<Long, Double> scores = new HashMap<>();
    for (Post post : allPosts) {
        double baseScore = recommendationEngine.calculateHotScore(
            post.getViewCount() != null ? post.getViewCount(). : 0,
            0,
            post.getCommentCount() != null ? post.getCommentCount(). : 0,
            post.getCreateTime()
        );

        // 根据用户偏好加权
        Long prefCount = typePreference.getOrDefault(post.getTypeId(), 0L);
        double preferenceWeight = 1.0 + (prefCount * 0.1);

        scores.put(post.getId(), baseScore * preferenceWeight);
    }

    // 5. 返回TopN
    List<Long> topIds = recommendationEngine.getTopNRecommendations(scores, limit);

    return allPosts.stream()
        .filter(p -> topIds.contains(p.getId()))
        .collect(Collectors.toList());
}
```

## 权重参数调优

可以根据业务需求调整权重：

```java
// 方案1：强调成交
double viewWeight = 1.0;
double acceptWeight = 10.0;  // 成交最重要
double commentWeight = 3.0;

// 方案2：强调互动
double viewWeight = 1.0;
double acceptWeight = 5.0;
double commentWeight = 8.0;  // 评论最重要

// 方案3：时间衰减更快
double timeDecay = Math.pow(0.90, hoursSinceCreation / 24.0);  // 每天衰减10%
```

## 相关文件

| 文件路径 | 说明 |
|----------|------|
| `jsyl-server/src/main/java/com/jsyl/utils/RecommendationEngine.java` | 推荐引擎 |
| `jsyl-server/src/main/java/com/jsyl/service/impl/PostServiceImpl.java` | 帖子服务（使用推荐） |
| `jsyl-server/src/main/java/com/jsyl/service/impl/OrderCenterServiceImpl.java` | 订单服务 |
