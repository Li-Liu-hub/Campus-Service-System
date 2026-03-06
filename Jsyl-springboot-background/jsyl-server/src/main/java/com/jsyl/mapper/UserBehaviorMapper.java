package com.jsyl.mapper;

import com.jsyl.entity.UserBehavior;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserBehaviorMapper {

    @Insert("INSERT INTO user_behavior (user_id, item_type, item_id, behavior_type, score, create_time) " +
            "VALUES (#{userId}, #{itemType}, #{itemId}, #{behaviorType}, #{score}, #{createTime})")
    void insert(UserBehavior userBehavior);

    @Select("SELECT * FROM user_behavior WHERE user_id = #{userId} AND item_type = #{itemType} " +
            "ORDER BY create_time DESC LIMIT #{limit}")
    List<UserBehavior> getUserBehaviors(@Param("userId") Integer userId,
                                        @Param("itemType") String itemType,
                                        @Param("limit") Integer limit);

    @Select("SELECT DISTINCT item_id FROM user_behavior WHERE user_id = #{userId} AND item_type = #{itemType}")
    List<Long> getUserInteractedItems(@Param("userId") Integer userId, @Param("itemType") String itemType);

    @Select("SELECT user_id, item_id FROM user_behavior WHERE item_type = #{itemType} " +
            "AND create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY)")
    List<UserBehavior> getRecentBehaviors(@Param("itemType") String itemType);
}
