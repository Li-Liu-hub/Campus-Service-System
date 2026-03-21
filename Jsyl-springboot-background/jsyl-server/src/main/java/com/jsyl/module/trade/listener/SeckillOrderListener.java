package com.jsyl.module.trade.listener;

import com.jsyl.common.config.RabbitMQConfig;
import com.jsyl.model.trade.dto.SeckillMessageDTO;
import com.jsyl.model.trade.entity.SeckillGoods;
import com.jsyl.model.trade.entity.SeckillOrder;
import com.jsyl.module.trade.mapper.SeckillGoodsMapper;
import com.jsyl.module.trade.mapper.SeckillOrderMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Slf4j
public class SeckillOrderListener {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String STOCK_KEY_PREFIX = "seckill:stock:";
    private static final String ORDER_KEY_PREFIX = "seckill:order:";

    @RabbitListener(queues = RabbitMQConfig.SECKILL_QUEUE)
    @Transactional
    public void handleSeckillOrder(SeckillMessageDTO message, Channel channel, Message mqMessage) throws IOException {
        long deliveryTag = mqMessage.getMessageProperties().getDeliveryTag();

        try {
            Long goodsId = message.getGoodsId();
            Integer userId = message.getUserId();
            Integer quantity = message.getQuantity();

            SeckillGoods goods = seckillGoodsMapper.getById(goodsId);
            if (goods == null) {
                channel.basicAck(deliveryTag, false);
                return;
            }

            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(goods.getStartTime()) || now.isAfter(goods.getEndTime())) {
                String stockKey = STOCK_KEY_PREFIX + goodsId;
                redisTemplate.opsForValue().increment(stockKey, quantity);
                channel.basicAck(deliveryTag, false);
                return;
            }

            Integer purchasedCount = seckillOrderMapper.getUserPurchaseCount(userId, goodsId);
            if (purchasedCount + quantity > goods.getLimitPerUser()) {
                String stockKey = STOCK_KEY_PREFIX + goodsId;
                redisTemplate.opsForValue().increment(stockKey, quantity);
                channel.basicAck(deliveryTag, false);
                return;
            }

            int result = seckillGoodsMapper.decreaseStock(goodsId, quantity);
            if (result == 0) {
                String stockKey = STOCK_KEY_PREFIX + goodsId;
                redisTemplate.opsForValue().increment(stockKey, quantity);
                channel.basicAck(deliveryTag, false);
                return;
            }

            String orderNo = generateOrderNo();
            SeckillOrder order = SeckillOrder.builder()
                    .orderNo(orderNo)
                    .userId(userId)
                    .goodsId(goodsId)
                    .goodsTitle(goods.getTitle())
                    .goodsImage(goods.getImageUrl())
                    .quantity(quantity)
                    .price(goods.getSeckillPrice())
                    .totalAmount(goods.getSeckillPrice().multiply(new BigDecimal(quantity)))
                    .status(0)
                    .build();

            seckillOrderMapper.insert(order);

            String orderKey = ORDER_KEY_PREFIX + userId + ":" + goodsId;
            redisTemplate.opsForValue().set(orderKey, orderNo);

            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("处理秒杀订单失败", e);
            channel.basicNack(deliveryTag, false, true);
        }
    }

    private String generateOrderNo() {
        return "SK" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
