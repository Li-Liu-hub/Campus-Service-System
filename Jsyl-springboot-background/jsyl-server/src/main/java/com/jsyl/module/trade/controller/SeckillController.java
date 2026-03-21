package com.jsyl.module.trade.controller;

import com.jsyl.common.context.BaseContext;
import com.jsyl.model.trade.entity.SeckillOrder;
import com.jsyl.common.result.Result;
import com.jsyl.module.trade.service.SeckillService;
import com.jsyl.model.trade.vo.SeckillGoodsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/jsyl/home/seckill")
@Api(tags = "秒杀相关接口")
@Slf4j
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private RedisTemplate<String, Object> redisObjectTemplate;

    private static final String SECKILL_GOODS_DETAIL_CACHE_KEY = "seckill:goods:detail:";
    // 缓存基础过期时间：30分钟
    private static final long CACHE_BASE_EXPIRE = 30;
    // 随机波动时间：0-10分钟
    private static final long CACHE_RANDOM_EXPIRE = 10;

    @GetMapping("/goods")
    @ApiOperation("获取秒杀商品列表")
    public Result<List<SeckillGoodsVO>> getGoodsList() {
        Integer userId = BaseContext.getCurrentId().intValue();
        List<SeckillGoodsVO> list = seckillService.getActiveGoodsList(userId);
        return Result.success(list);
    }

    @GetMapping("/goods/{id}")
    @ApiOperation("获取秒杀商品详情")
    public Result<SeckillGoodsVO> getGoodsDetail(@PathVariable Long id) {
        Integer userId = BaseContext.getCurrentId().intValue();

        // 尝试从缓存获取
        String cacheKey = SECKILL_GOODS_DETAIL_CACHE_KEY + id;
        SeckillGoodsVO cachedVO = (SeckillGoodsVO) redisObjectTemplate.opsForValue().get(cacheKey);
        if (cachedVO != null) {
            log.info("从缓存获取秒杀商品详情: id={}", id);
            return Result.success(cachedVO);
        }

        // 缓存不存在，查询数据库
        SeckillGoodsVO vo = seckillService.getGoodsDetail(id, userId);

        // 存入缓存，添加随机过期时间（防止缓存雪崩）
        if (vo != null) {
            long randomExpire = CACHE_BASE_EXPIRE + (long) (Math.random() * CACHE_RANDOM_EXPIRE);
            redisObjectTemplate.opsForValue().set(cacheKey, vo, randomExpire, TimeUnit.MINUTES);
            log.info("秒杀商品详情存入缓存: id={}, 过期时间={}分钟", id, randomExpire);
        }

        return Result.success(vo);
    }
    @PostMapping("/buy")
    @ApiOperation("秒杀抢购")
    public Result<Map<String, String>> seckill(@RequestParam Long goodsId,
                                                @RequestParam(defaultValue = "1") Integer quantity) {
        Integer userId = BaseContext.getCurrentId().intValue();
        String orderNo = seckillService.seckill(goodsId, quantity, userId);

        Map<String, String> result = new HashMap<>();
        result.put("orderNo", orderNo);
        result.put("message", "抢购成功！");

        return Result.success(result);
    }

    @GetMapping("/orders")
    @ApiOperation("获取我的秒杀订单")
    public Result<List<SeckillOrder>> getMyOrders() {
        Integer userId = BaseContext.getCurrentId().intValue();
        List<SeckillOrder> orders = seckillService.getUserOrders(userId);
        return Result.success(orders);
    }
}
