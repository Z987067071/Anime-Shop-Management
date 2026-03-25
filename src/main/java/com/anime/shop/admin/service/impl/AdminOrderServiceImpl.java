package com.anime.shop.admin.service.impl;

import com.anime.shop.admin.controller.dto.order.OrderQueryDTO;
import com.anime.shop.admin.controller.dto.order.OrderStatusDTO;
import com.anime.shop.admin.mapper.comic.ComicConTicketMapper;
import com.anime.shop.admin.mapper.order.OrderEntityMapper;
import com.anime.shop.admin.mapper.order.OrderItemEntityMapper;
import com.anime.shop.admin.mapper.product.ProductMapper;
import com.anime.shop.admin.service.AdminOrderService;
import com.anime.shop.entity.*;
import com.anime.shop.mapper.OrderTicketRelationMapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminOrderServiceImpl extends ServiceImpl<OrderEntityMapper, OrderEntity> implements AdminOrderService {

    @Resource
    private OrderItemEntityMapper orderItemEntityMapper;
    @Resource
    private OrderTicketRelationMapper orderTicketRelationMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private ComicConTicketMapper comicConTicketMapper;

    /**
     * 分页查询订单列表（多条件筛选）
     */
    @Override
    public IPage<OrderEntity> getOrderPage(OrderQueryDTO query) {
        Page<OrderEntity> page = new Page<>(query.getPageNum(), query.getPageSize());
        // 1. 原有分页查询（查订单主表）
        IPage<OrderEntity> orderPage = baseMapper.selectOrderPage(page, query);

        // 2. 遍历订单，补充productType（核心修改）
        if (!orderPage.getRecords().isEmpty()) {
            for (OrderEntity order : orderPage.getRecords()) {
                // 查询当前订单的订单项
                List<OrderItemEntity> orderItems = orderItemEntityMapper.selectByOrderId(order.getId());
                if (!orderItems.isEmpty()) {
                    // 取第一个商品的productType（漫展票订单通常只有一个票务商品）
                    Long productId = orderItems.get(0).getProductId();
                    ProductEntity product = productMapper.selectById(productId);
                    if (product != null) {
                        order.setProductType(product.getProductType());
                    }
                }
            }
        }

        return orderPage;
    }

    /**
     * 查询订单详情（含订单项）
     */
    @Override
    public Map<String, Object> getOrderDetail(Long id) {
        Map<String, Object> result = new HashMap<>();
        // 查询订单主信息
        OrderEntity orderEntity = baseMapper.selectById(id);
        if (orderEntity == null || orderEntity.getIsDelete() == 1) {
            return null;
        }
        // 查询订单项
        List<OrderItemEntity> orderItemEntities = orderItemEntityMapper.selectByOrderId(id);
        // 新增：关联商品表，获取productType
        Integer productType = null;
        if (!orderItemEntities.isEmpty()) {
            Long productId = orderItemEntities.get(0).getProductId();
            ProductEntity product = productMapper.selectById(productId);
            if (product != null) {
                productType = product.getProductType();
            }
        }
        // 查询票务信息（仅票务订单）
        if (orderEntity.getOrderType() == 1) {
            List<OrderTicketRelationEntity> ticketRelations = orderTicketRelationMapper.selectByOrderId(id);
            result.put("ticketRelations", ticketRelations);
        }
        // 组装返回数据
        result.put("order", orderEntity);
        result.put("orderItems", orderItemEntities);
        result.put("productType", productType);
        return result;
    }

    /**
     * 修改订单状态（含不同状态的特殊处理）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrderStatus(OrderStatusDTO dto) {
        OrderEntity order = baseMapper.selectById(dto.getId());
        if (order == null) return false;

        LambdaUpdateWrapper<OrderEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(OrderEntity::getId, dto.getId())
                .set(OrderEntity::getOrderStatus, dto.getOrderStatus())
                .set(OrderEntity::getUpdateTime, new Date());

        Integer newStatus = dto.getOrderStatus();
        Integer oldPayStatus = order.getPayStatus();

        if (newStatus == 4) {
            // 支付状态自动修正
            if (oldPayStatus == 1) {
                updateWrapper.set(OrderEntity::getPayStatus, 3);
            } else if (oldPayStatus == 0) {
                updateWrapper.set(OrderEntity::getPayStatus, 4);
            }
            updateWrapper.set(OrderEntity::getCancelTime, new Date());

            List<OrderItemEntity> items = orderItemEntityMapper.selectByOrderId(order.getId());
            for (OrderItemEntity item : items) {
                // 1. 回滚销量
                ProductEntity product = productMapper.selectById(item.getProductId());
                if (product != null) {
                    int oldSales = product.getSales() == null ? 0 : product.getSales();
                    int realSales = Math.max(0, oldSales - item.getQuantity());
                    product.setSales(realSales);

                    // 2. 使用最新字段 remainStock 回滚库存
                    product.setRemainStock(product.getRemainStock() + item.getQuantity());
                    productMapper.updateById(product);
                }

                // 3. 同步回滚 票种库存（comic_con_ticket.stock）
                if (item.getSkuId() != null) {
                    LambdaUpdateWrapper<ComicConTicket> ticketWrapper = new LambdaUpdateWrapper<>();
                    ticketWrapper.eq(ComicConTicket::getSkuId, item.getSkuId())
                            .setSql("stock = stock + " + item.getQuantity());
                    comicConTicketMapper.update(null, ticketWrapper);
                }
            }
        }

        // 普通商品
        if (order.getOrderType() == 0) {
            if (newStatus == 1) {
                updateWrapper.set(OrderEntity::getPayStatus, 1)
                        .set(OrderEntity::getPayTime, new Date());
            } else if (newStatus == 2) {
                updateWrapper.set(OrderEntity::getDeliveryTime, new Date())
                        .set(OrderEntity::getDeliveryCompany, dto.getDeliveryCompany())
                        .set(OrderEntity::getDeliverySn, dto.getDeliverySn());
            } else if (newStatus == 3) {
                updateWrapper.set(OrderEntity::getReceiveTime, new Date());
            }
        }

        // 票务订单
        if (order.getOrderType() == 1) {
            if (newStatus == 1) {
                updateWrapper.set(OrderEntity::getPayStatus, 1)
                        .set(OrderEntity::getPayTime, new Date());
            } else if (newStatus == 3) {
                updateWrapper.set(OrderEntity::getReceiveTime, new Date());
                updateTicketVerifyStatus(dto);
            }
        }

        return baseMapper.update(null, updateWrapper) > 0;
    }

    private void updateTicketVerifyStatus(OrderStatusDTO dto) {
        // 这里需要注入orderTicketRelationMapper
        LambdaUpdateWrapper<OrderTicketRelationEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(OrderTicketRelationEntity::getOrderId, dto.getId())
                .set(OrderTicketRelationEntity::getVerifyStatus, dto.getVerifyStatus())
                .set(OrderTicketRelationEntity::getVerifyTime, new Date())
                .set(OrderTicketRelationEntity::getVerifyStaff, dto.getVerifyStaff());
        // orderTicketRelationMapper.update(null, wrapper);
    }

    /**
     * 逻辑删除订单（联动删除订单项）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteOrder(Long id) {
        // 1. 逻辑删除订单主表
        LambdaUpdateWrapper<OrderEntity> orderWrapper = new LambdaUpdateWrapper<>();
        orderWrapper.eq(OrderEntity::getId, id)
                .set(OrderEntity::getIsDelete, 1)
                .set(OrderEntity::getUpdateTime, new Date());
        baseMapper.update(null, orderWrapper);

        // 2. 联动逻辑删除订单项
        LambdaUpdateWrapper<OrderItemEntity> itemWrapper = new LambdaUpdateWrapper<>();
        itemWrapper.eq(OrderItemEntity::getOrderId, id)
                .set(OrderItemEntity::getIsDelete, 1)
                .set(OrderItemEntity::getUpdateTime, new Date());
        orderItemEntityMapper.update(null, itemWrapper);

        return true;
    }
}
