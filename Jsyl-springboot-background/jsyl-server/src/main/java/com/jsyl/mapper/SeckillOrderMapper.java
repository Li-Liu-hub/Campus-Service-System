package com.jsyl.mapper;

import com.jsyl.entity.SeckillOrder;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SeckillOrderMapper {

    @Insert("INSERT INTO seckill_order (order_no, user_id, goods_id, goods_title, goods_image, quantity, price, total_amount, status) " +
            "VALUES (#{orderNo}, #{userId}, #{goodsId}, #{goodsTitle}, #{goodsImage}, #{quantity}, #{price}, #{totalAmount}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(SeckillOrder order);

    @Select("SELECT * FROM seckill_order WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<SeckillOrder> getByUserId(@Param("userId") Integer userId);

    @Select("SELECT COUNT(*) FROM seckill_order WHERE user_id = #{userId} AND goods_id = #{goodsId} AND status != 2")
    Integer getUserPurchaseCount(@Param("userId") Integer userId, @Param("goodsId") Long goodsId);

    @Select("SELECT * FROM seckill_order WHERE order_no = #{orderNo}")
    SeckillOrder getByOrderNo(@Param("orderNo") String orderNo);

    @Update("UPDATE seckill_order SET status = #{status}, pay_time = NOW() WHERE order_no = #{orderNo}")
    void updateStatus(@Param("orderNo") String orderNo, @Param("status") Integer status);
}
