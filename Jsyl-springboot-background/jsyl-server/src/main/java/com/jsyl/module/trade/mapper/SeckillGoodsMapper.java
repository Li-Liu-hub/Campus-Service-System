package com.jsyl.module.trade.mapper;

import com.jsyl.model.trade.entity.SeckillGoods;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SeckillGoodsMapper {

    @Select("SELECT * FROM seckill_goods WHERE status = 1 AND start_time <= NOW() AND end_time >= NOW() ORDER BY create_time DESC")
    List<SeckillGoods> getActiveList();

    @Select("SELECT * FROM seckill_goods WHERE id = #{id}")
    SeckillGoods getById(@Param("id") Long id);

    @Update("UPDATE seckill_goods SET stock = stock - #{quantity}, sold_count = sold_count + #{quantity} WHERE id = #{id} AND stock >= #{quantity}")
    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Select("SELECT stock FROM seckill_goods WHERE id = #{id}")
    Integer getStock(@Param("id") Long id);

    @Insert("INSERT INTO seckill_goods (title, description, image_url, original_price, seckill_price, stock, start_time, end_time, status, limit_per_user, create_time, update_time) " +
            "VALUES (#{title}, #{description}, #{imageUrl}, #{originalPrice}, #{seckillPrice}, #{stock}, #{startTime}, #{endTime}, #{status}, #{limitPerUser}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(SeckillGoods goods);

    @Update("UPDATE seckill_goods SET status = #{status} WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    // 管理端方法
    @Select("<script>" +
            "SELECT * FROM seckill_goods WHERE 1=1 " +
            "<if test='title != null and title != \"\"'> AND title LIKE CONCAT('%', #{title}, '%') </if>" +
            "<if test='status != null'> AND status = #{status} </if>" +
            "ORDER BY create_time DESC" +
            "</script>")
    List<SeckillGoods> getListForAdmin(@Param("title") String title, @Param("status") Integer status);

    @Update("UPDATE seckill_goods SET title=#{title}, description=#{description}, image_url=#{imageUrl}, " +
            "original_price=#{originalPrice}, seckill_price=#{seckillPrice}, stock=#{stock}, " +
            "start_time=#{startTime}, end_time=#{endTime}, limit_per_user=#{limitPerUser}, " +
            "update_time=#{updateTime} WHERE id=#{id}")
    void update(SeckillGoods goods);

    @Delete("DELETE FROM seckill_goods WHERE id = #{id}")
    void deleteById(@Param("id") Long id);
}
