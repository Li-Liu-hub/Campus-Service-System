package com.jsyl.controller;

import com.jsyl.context.BaseContext;
import com.jsyl.entity.SeckillOrder;
import com.jsyl.result.Result;
import com.jsyl.service.SeckillService;
import com.jsyl.vo.SeckillGoodsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/jsyl/home/seckill")
@Api(tags = "秒杀相关接口")
@Slf4j
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

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
        SeckillGoodsVO vo = seckillService.getGoodsDetail(id, userId);
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
