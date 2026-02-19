package com.jsyl.mapper;

import com.github.pagehelper.Page;

import com.jsyl.dto.OrderPageQueryDTO;
import com.jsyl.entity.Order;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderCenterMapper {

    /*分页查询*/
    List<Order> pageQuery(OrderPageQueryDTO orderPageQueryDTO);

    /*插入订单*/
    void insert(Order order);

    /*根据ID获取订单*/
    Order getById(Long id);

    /*更新订单*/
    void update(Order order);
}
