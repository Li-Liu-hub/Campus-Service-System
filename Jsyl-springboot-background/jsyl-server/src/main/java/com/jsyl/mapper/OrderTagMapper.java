package com.jsyl.mapper;

import com.jsyl.entity.OrderTag;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Set;

@Mapper
public interface OrderTagMapper {

    @Insert("INSERT INTO order_tag (order_id, tag, create_time) VALUES (#{orderId}, #{tag}, #{createTime})")
    void insert(OrderTag orderTag);

    @Select("SELECT tag FROM order_tag WHERE order_id = #{orderId}")
    Set<String> getTagsByOrderId(@Param("orderId") Long orderId);

    @Select("SELECT DISTINCT order_id FROM order_tag WHERE tag = #{tag}")
    List<Long> getOrderIdsByTag(@Param("tag") String tag);

    @Delete("DELETE FROM order_tag WHERE order_id = #{orderId}")
    void deleteByOrderId(@Param("orderId") Long orderId);
}
