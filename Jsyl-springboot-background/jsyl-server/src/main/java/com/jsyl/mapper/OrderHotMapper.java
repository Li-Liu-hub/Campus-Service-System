package com.jsyl.mapper;

import com.jsyl.entity.OrderHot;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderHotMapper {

    @Insert("INSERT INTO order_hot (order_id, view_count, accept_count, comment_count, hot_score, update_time) " +
            "VALUES (#{orderId}, #{viewCount}, #{acceptCount}, #{commentCount}, #{hotScore}, #{updateTime}) " +
            "ON DUPLICATE KEY UPDATE " +
            "view_count = #{viewCount}, accept_count = #{acceptCount}, comment_count = #{commentCount}, " +
            "hot_score = #{hotScore}, update_time = #{updateTime}")
    void insertOrUpdate(OrderHot orderHot);

    @Select("SELECT * FROM order_hot WHERE order_id = #{orderId}")
    OrderHot getByOrderId(@Param("orderId") Long orderId);

    @Update("UPDATE order_hot SET view_count = view_count + 1, update_time = NOW() WHERE order_id = #{orderId}")
    void incrementViewCount(@Param("orderId") Long orderId);

    @Update("UPDATE order_hot SET accept_count = accept_count + 1, update_time = NOW() WHERE order_id = #{orderId}")
    void incrementAcceptCount(@Param("orderId") Long orderId);

    @Update("UPDATE order_hot SET comment_count = comment_count + 1, update_time = NOW() WHERE order_id = #{orderId}")
    void incrementCommentCount(@Param("orderId") Long orderId);
}
