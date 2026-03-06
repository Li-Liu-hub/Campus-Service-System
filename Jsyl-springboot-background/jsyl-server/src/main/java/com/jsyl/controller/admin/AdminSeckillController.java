package com.jsyl.controller.admin;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jsyl.entity.SeckillGoods;
import com.jsyl.mapper.SeckillGoodsMapper;
import com.jsyl.result.PageResult;
import com.jsyl.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/seckill")
@Api(tags = "管理端秒杀管理")
@Slf4j
public class AdminSeckillController {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    /**
     * 获取秒杀商品列表
     */
    @GetMapping("/goods")
    @ApiOperation("获取秒杀商品列表")
    public Result<PageResult> getGoodsList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status) {

        PageHelper.startPage(page, pageSize);
        List<SeckillGoods> list = seckillGoodsMapper.getListForAdmin(title, status);

        Page<SeckillGoods> p = (Page<SeckillGoods>) list;
        PageResult pageResult = new PageResult(p.getTotal(), p.getResult());

        return Result.success(pageResult);
    }

    /**
     * 获取秒杀商品详情
     */
    @GetMapping("/goods/{id}")
    @ApiOperation("获取秒杀商品详情")
    public Result<SeckillGoods> getGoodsDetail(@PathVariable Long id) {
        SeckillGoods goods = seckillGoodsMapper.getById(id);
        return Result.success(goods);
    }

    /**
     * 添加秒杀商品
     */
    @PostMapping("/goods")
    @ApiOperation("添加秒杀商品")
    public Result<String> addGoods(@RequestBody SeckillGoods goods) {
        if (goods.getTitle() == null || goods.getTitle().isEmpty()) {
            return Result.error("商品标题不能为空");
        }
        if (goods.getSeckillPrice() == null || goods.getOriginalPrice() == null) {
            return Result.error("价格不能为空");
        }
        if (goods.getStock() == null || goods.getStock() <= 0) {
            return Result.error("库存必须大于0");
        }
        if (goods.getStartTime() == null || goods.getEndTime() == null) {
            return Result.error("秒杀时间不能为空");
        }

        goods.setStatus(1); // 上架状态
        goods.setSoldCount(0);
        goods.setCreateTime(LocalDateTime.now());
        goods.setUpdateTime(LocalDateTime.now());

        seckillGoodsMapper.insert(goods);

        return Result.success("添加成功");
    }

    /**
     * 更新秒杀商品
     */
    @PutMapping("/goods/{id}")
    @ApiOperation("更新秒杀商品")
    public Result<String> updateGoods(@PathVariable Long id, @RequestBody SeckillGoods goods) {
        SeckillGoods existing = seckillGoodsMapper.getById(id);
        if (existing == null) {
            return Result.error("商品不存在");
        }

        goods.setId(id);
        goods.setUpdateTime(LocalDateTime.now());
        seckillGoodsMapper.update(goods);

        return Result.success("更新成功");
    }

    /**
     * 删除秒杀商品
     */
    @DeleteMapping("/goods/{id}")
    @ApiOperation("删除秒杀商品")
    public Result<String> deleteGoods(@PathVariable Long id) {
        seckillGoodsMapper.deleteById(id);
        return Result.success("删除成功");
    }

    /**
     * 上架/下架秒杀商品
     */
    @PutMapping("/goods/{id}/status")
    @ApiOperation("修改秒杀商品状态")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        seckillGoodsMapper.updateStatus(id, status);
        return Result.success(status == 1 ? "上架成功" : "下架成功");
    }

    /**
     * 获取秒杀统计概览
     */
    @GetMapping("/overview")
    @ApiOperation("获取秒杀统计概览")
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> result = new HashMap<>();

        List<SeckillGoods> allGoods = seckillGoodsMapper.getListForAdmin(null, null);
        int totalGoods = allGoods.size();
        int activeGoods = (int) allGoods.stream().filter(g -> g.getStatus() == 1).count();
        int totalStock = allGoods.stream().mapToInt(g -> g.getStock() != null ? g.getStock() : 0).sum();
        int totalSold = allGoods.stream().mapToInt(g -> g.getSoldCount() != null ? g.getSoldCount() : 0).sum();

        result.put("totalGoods", totalGoods);
        result.put("activeGoods", activeGoods);
        result.put("totalStock", totalStock);
        result.put("totalSold", totalSold);

        return Result.success(result);
    }
}
