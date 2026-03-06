package com.jsyl.service.impl;

import com.jsyl.config.RabbitMQConfig;
import com.jsyl.dto.SeckillMessageDTO;
import com.jsyl.entity.SeckillGoods;
import com.jsyl.entity.SeckillOrder;
import com.jsyl.mapper.SeckillGoodsMapper;
import com.jsyl.mapper.SeckillOrderMapper;
import com.jsyl.service.SeckillService;
import com.jsyl.vo.SeckillGoodsVO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String STOCK_KEY_PREFIX = "seckill:stock:";
    private static final String USER_LIMIT_KEY_PREFIX = "seckill:user:";

    @Override
    public List<SeckillGoodsVO> getActiveGoodsList(Integer userId) {
        List<SeckillGoods> goodsList = seckillGoodsMapper.getActiveList();

        return goodsList.stream().map(goods -> {
            SeckillGoodsVO vo = new SeckillGoodsVO();
            BeanUtils.copyProperties(goods, vo);

            Integer purchasedCount = seckillOrderMapper.getUserPurchaseCount(userId, goods.getId());
            vo.setUserPurchasedCount(purchasedCount);

            long remainingSeconds = Duration.between(LocalDateTime.now(), goods.getEndTime()).getSeconds();
            vo.setRemainingSeconds(Math.max(0, remainingSeconds));

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public SeckillGoodsVO getGoodsDetail(Long goodsId, Integer userId) {
        SeckillGoods goods = seckillGoodsMapper.getById(goodsId);
        if (goods == null) {
            throw new RuntimeException("商品不存在");
        }

        SeckillGoodsVO vo = new SeckillGoodsVO();
        BeanUtils.copyProperties(goods, vo);

        Integer purchasedCount = seckillOrderMapper.getUserPurchaseCount(userId, goodsId);
        vo.setUserPurchasedCount(purchasedCount);

        long remainingSeconds = Duration.between(LocalDateTime.now(), goods.getEndTime()).getSeconds();
        vo.setRemainingSeconds(Math.max(0, remainingSeconds));

        return vo;
    }

    @Override
    public String seckill(Long goodsId, Integer quantity, Integer userId) {
        SeckillGoods goods = seckillGoodsMapper.getById(goodsId);
        if (goods == null) {
            throw new RuntimeException("商品不存在");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(goods.getStartTime())) {
            throw new RuntimeException("秒杀还未开始");
        }
        if (now.isAfter(goods.getEndTime())) {
            throw new RuntimeException("秒杀已结束");
        }

        Integer purchasedCount = seckillOrderMapper.getUserPurchaseCount(userId, goodsId);
        if (purchasedCount + quantity > goods.getLimitPerUser()) {
            throw new RuntimeException("超过限购数量");
        }

        String stockKey = STOCK_KEY_PREFIX + goodsId;
        Long stock = redisTemplate.opsForValue().decrement(stockKey, quantity);

        if (stock == null) {
            Integer dbStock = seckillGoodsMapper.getStock(goodsId);
            redisTemplate.opsForValue().set(stockKey, dbStock, 1, TimeUnit.HOURS);
            stock = redisTemplate.opsForValue().decrement(stockKey, quantity);
        }

        if (stock < 0) {
            redisTemplate.opsForValue().increment(stockKey, quantity);
            throw new RuntimeException("库存不足");
        }

        SeckillMessageDTO message = SeckillMessageDTO.builder()
                .goodsId(goodsId)
                .userId(userId)
                .quantity(quantity)
                .timestamp(System.currentTimeMillis())
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.SECKILL_EXCHANGE,
                RabbitMQConfig.SECKILL_ROUTING_KEY,
                message
        );

        return "排队中";
    }

    @Override
    public List<SeckillOrder> getUserOrders(Integer userId) {
        return seckillOrderMapper.getByUserId(userId);
    }
}
