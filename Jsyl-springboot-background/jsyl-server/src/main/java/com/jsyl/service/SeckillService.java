package com.jsyl.service;

import com.jsyl.entity.SeckillOrder;
import com.jsyl.vo.SeckillGoodsVO;

import java.util.List;

public interface SeckillService {

    List<SeckillGoodsVO> getActiveGoodsList(Integer userId);

    SeckillGoodsVO getGoodsDetail(Long goodsId, Integer userId);

    String seckill(Long goodsId, Integer quantity, Integer userId);

    List<SeckillOrder> getUserOrders(Integer userId);
}
