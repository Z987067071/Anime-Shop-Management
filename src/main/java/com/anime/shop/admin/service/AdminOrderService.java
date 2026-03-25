package com.anime.shop.admin.service;

import com.anime.shop.admin.controller.dto.order.OrderQueryDTO;
import com.anime.shop.admin.controller.dto.order.OrderStatusDTO;
import com.anime.shop.entity.OrderEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Map;

public interface AdminOrderService {
    // 分页查询订单
    IPage<OrderEntity> getOrderPage(OrderQueryDTO query);

    // 根据ID查询订单详情（含订单项）
    Map<String, Object> getOrderDetail(Long id);

    // 修改订单状态
    boolean updateOrderStatus(OrderStatusDTO dto);

    // 删除订单（逻辑删除）
    boolean deleteOrder(Long id);
}
