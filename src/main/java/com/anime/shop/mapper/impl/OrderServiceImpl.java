package com.anime.shop.mapper.impl;

import com.anime.shop.admin.mapper.comic.ComicConTicketMapper;
import com.anime.shop.admin.mapper.product.ProductMapper;
import com.anime.shop.admin.mapper.product.ProductSkuMapper;
import com.anime.shop.controller.dto.buyer.BuyerInfoDTO;
import com.anime.shop.controller.dto.order.*;
import com.anime.shop.entity.CartEntity;
import com.anime.shop.entity.ComicConTicket;
import com.anime.shop.entity.OrderTicketRelationEntity;
import com.anime.shop.entity.ProductEntity;
import com.anime.shop.enums.OrderStatusEnum;
import com.anime.shop.enums.PayTypeEnum;
import com.anime.shop.mapper.CartMapper;
import com.anime.shop.mapper.OrderTicketRelationMapper;
import com.anime.shop.mapper.POrderItemMapper;
import com.anime.shop.mapper.POrderMapper;
import com.anime.shop.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<POrderMapper, POrder> implements OrderService {

    @Resource
    private POrderItemMapper orderItemMapper;
    @Resource
    private CartMapper cartMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private ProductSkuMapper productSkuMapper;
    @Resource
    private ComicConTicketMapper comicConTicketMapper;
    @Resource
    private OrderTicketRelationMapper orderTicketRelationMapper;

    /**
     * 生成唯一订单编号（yyyyMMddHHmmss + 6位随机数）
     */
    private String generateOrderNo() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%tY%<tm%<td%<tH%<tM%<tS", new Date()));
        sb.append((int) ((Math.random() * 9 + 1) * 100000));
        return sb.toString();
    }

    /**
     * 提交订单（事务控制，保证订单和订单项同时入库）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitOrder(OrderSubmitDTO submitDTO) {
        for (OrderItemDTO item : submitDTO.getOrderItems()) {
            ProductEntity product = productMapper.selectById(item.getProductId());
            if (product == null) {
                throw new RuntimeException("商品不存在：ID=" + item.getProductId());
            }
            if (product.getStatus() != 1) {
                throw new RuntimeException("商品已下架：" + product.getProductName());
            }
        }

        String buyerIds = "";
        List<BuyerInfoDTO> buyerList = submitDTO.getBuyerList();
        if (submitDTO.getIsTicket() == 1 && buyerList != null && !buyerList.isEmpty()) {
            buyerIds = buyerList.stream()
                    .map(BuyerInfoDTO::getId)
                    .collect(Collectors.joining(","));
        }

        // 2. 订单主表
        POrder order = new POrder();
        BeanUtils.copyProperties(submitDTO, order);
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        order.setOrderStatus(OrderStatusEnum.UNPAID.getCode());
        order.setPayStatus(PayTypeEnum.UNPAID.getCode());
        order.setPayType(0);
        order.setIsDelete(0);
        order.setBuyerIds(buyerIds);
        order.setOrderType(submitDTO.getIsTicket()); // 同步订单类型：0=普通商品，1=漫展票务
        baseMapper.insert(order);
        Long orderId = order.getId();

        // 3. 订单项
        List<POrderItem> orderItems = new ArrayList<>();
        int buyerIndex = 0;
        for (OrderItemDTO item : submitDTO.getOrderItems()) {
            int quantity = item.getQuantity();
            for (int i = 0; i < quantity; i++) {
                POrderItem orderItem = new POrderItem();
                BeanUtils.copyProperties(item, orderItem);
                orderItem.setOrderId(orderId);
                orderItem.setOrderNo(orderNo);
                orderItem.setIsDelete(0);
                orderItem.setSkuId(item.getSkuId());
                orderItem.setTicketType(item.getTicketType() != null ? item.getTicketType() : item.getSkuName());
                orderItem.setQuantity(1);

                if (submitDTO.getIsTicket() == 1 && !CollectionUtils.isEmpty(buyerList)) {
                    BuyerInfoDTO currentBuyer = buyerList.get(buyerIndex % buyerList.size());
                    orderItem.setBuyerId(Long.valueOf(currentBuyer.getId()));
                    orderItem.setBuyerName(currentBuyer.getName());
                    orderItem.setBuyerIdCard(currentBuyer.getIdCard());
                    buyerIndex++;
                }
                orderItems.add(orderItem);
            }
        }
        for (POrderItem item : orderItems) {
            orderItemMapper.insert(item);
        }

        // 票务订单：写入核销关联表
        if (submitDTO.getIsTicket() == 1 && !CollectionUtils.isEmpty(buyerList)) {
            for (int i = 0; i < orderItems.size(); i++) {
                POrderItem orderItem = orderItems.get(i);
                BuyerInfoDTO buyer = buyerList.get(i % buyerList.size());
                
                OrderTicketRelationEntity ticketRelation = new OrderTicketRelationEntity();
                ticketRelation.setOrderId(orderId);
                ticketRelation.setOrderItemId(orderItem.getId());
                ticketRelation.setUserId(submitDTO.getUserId());
                ticketRelation.setComicConTicketId(orderItem.getSkuId());
                ticketRelation.setRealName(buyer.getName());
                ticketRelation.setIdCard(buyer.getIdCard());
                // 核销码：订单号后6位 + 连字符 + 序号，格式如：736820-01
                ticketRelation.setVerifyCode(orderNo.substring(orderNo.length() - 6) + "-" + String.format("%02d", i + 1));
                ticketRelation.setVerifyStatus(0); // 未核销
                orderTicketRelationMapper.insert(ticketRelation);
            }
        }

        for (OrderItemDTO item : submitDTO.getOrderItems()) {
            LambdaUpdateWrapper<ProductEntity> stockWrapper = new LambdaUpdateWrapper<>();
            stockWrapper.eq(ProductEntity::getId, item.getProductId())
                    .ge(ProductEntity::getRemainStock, item.getQuantity())
                    .setSql("remain_stock = remain_stock - " + item.getQuantity());
            int rows = productMapper.update(null, stockWrapper);
            if (rows == 0) {
                ProductEntity product = productMapper.selectById(item.getProductId());
                String name = product != null ? product.getProductName() : "ID=" + item.getProductId();
                throw new RuntimeException("商品库存不足：" + name);
            }

            if (item.getSkuId() != null) {
                LambdaUpdateWrapper<ComicConTicket> wrapper = new LambdaUpdateWrapper<>();
                wrapper.eq(ComicConTicket::getSkuId, item.getSkuId())
                        .ge(ComicConTicket::getStock, item.getQuantity())
                        .setSql("stock = stock - " + item.getQuantity());
                comicConTicketMapper.update(null, wrapper);
            }
        }

        // 4. 清空购物车
        List<Long> goodsIds = submitDTO.getOrderItems().stream()
                .map(OrderItemDTO::getProductId)
                .collect(Collectors.toList());
        LambdaQueryWrapper<CartEntity> cartWrapper = new LambdaQueryWrapper<>();
        cartWrapper.eq(CartEntity::getUserId, submitDTO.getUserId())
                .in(CartEntity::getGoodsId, goodsIds);
        cartMapper.delete(cartWrapper);

        return orderId;
    }

    /**
     * 获取订单详情（包含订单项）
     */
    @Override
    public OrderDetailVO getOrderDetail(Long orderId) {
        // 1. 查询订单主表
        POrder order = baseMapper.selectById(orderId);
        if (order == null || order.getIsDelete() == 1) {
            return null;
        }

        // 2. 查询订单项
        LambdaQueryWrapper<POrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(POrderItem::getOrderId, orderId)
                .eq(POrderItem::getIsDelete, 0);
        List<POrderItem> orderItems = orderItemMapper.selectList(wrapper);

        Integer productType = null;
        if (!orderItems.isEmpty()) {
            // 取第一个商品的productType（漫展票订单通常只有一个票务商品）
            Long productId = orderItems.get(0).getProductId();
            ProductEntity product = productMapper.selectById(productId);
            if (product != null) {
                productType = product.getProductType();
            }
        }

        // 3. 封装返回VO
        OrderDetailVO detailVO = new OrderDetailVO();
        BeanUtils.copyProperties(order, detailVO);
        detailVO.setOrderItems(orderItems);
        detailVO.setProductType(productType);
        return detailVO;
    }

    /**
     * 模拟支付（仅修改状态，无真实支付逻辑）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payOrder(Long orderId, Integer payType) {
        POrder order = baseMapper.selectById(orderId);
        if (order == null) {
            return false;
        }
        // 检查订单状态：仅未支付且未超时的订单可支付
        if (order.getOrderStatus().equals(OrderStatusEnum.UNPAID.getCode()) && !isOrderTimeout(orderId)) {
            order.setPayType(payType);
            order.setPayStatus(PayTypeEnum.PAID.getCode());
            order.setOrderStatus(OrderStatusEnum.UNDELIVERED.getCode());
            order.setPayTime(LocalDateTime.now()); // 支付时间
            order.setUpdateTime(LocalDateTime.now());
            baseMapper.updateById(order);

            List<POrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<POrderItem>()
                            .eq(POrderItem::getOrderId, orderId)
                            .eq(POrderItem::getIsDelete, 0)
            );

            for (POrderItem item : items) {
                LambdaUpdateWrapper<ProductEntity> salesWrapper = new LambdaUpdateWrapper<>();
                salesWrapper.eq(ProductEntity::getId, item.getProductId())
                        .setSql("sales = sales + " + item.getQuantity());
                productMapper.update(null, salesWrapper);
            }
            return true;
        }
        return false;
    }

    /**
     * 取消订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long orderId) {
        POrder order = baseMapper.selectById(orderId);
        if (order == null) return false;

        if (order.getOrderStatus().equals(OrderStatusEnum.UNPAID.getCode())) {
            LambdaQueryWrapper<POrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(POrderItem::getOrderId, orderId).eq(POrderItem::getIsDelete, 0);
            List<POrderItem> orderItems = orderItemMapper.selectList(itemWrapper);

            for (POrderItem item : orderItems) {
                LambdaUpdateWrapper<ProductEntity> restoreWrapper = new LambdaUpdateWrapper<>();
                restoreWrapper.eq(ProductEntity::getId, item.getProductId())
                        .setSql("remain_stock = remain_stock + " + item.getQuantity()
                                + ", sales = GREATEST(0, sales - " + item.getQuantity() + ")");
                productMapper.update(null, restoreWrapper);

                if (item.getSkuId() != null) {
                    LambdaUpdateWrapper<ComicConTicket> wrapper = new LambdaUpdateWrapper<>();
                    wrapper.eq(ComicConTicket::getSkuId, item.getSkuId())
                            .setSql("stock = stock + " + item.getQuantity());
                    comicConTicketMapper.update(null, wrapper);
                }
            }

            order.setOrderStatus(OrderStatusEnum.CANCELLED.getCode());
            order.setPayStatus(PayTypeEnum.CANCELLED.getCode());
            order.setCancelTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            baseMapper.updateById(order);
            return true;
        }
        return false;
    }
    /**
     * 获取用户所有订单（包含订单项）
     */
    @Override
    public List<OrderDetailVO> getUserOrders(Long userId, Integer status) {
        LambdaQueryWrapper<POrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(POrder::getUserId, userId)
                .eq(POrder::getIsDelete, 0)
                .orderByDesc(POrder::getCreateTime);

        if (status != null) {
            List<Integer> validStatus = List.of(0, 1, 2, 3, 4, 5);
            if (validStatus.contains(status)) {
                wrapper.eq(POrder::getOrderStatus, status);
            } else {
                return List.of();
            }
        }

        List<POrder> orderList = baseMapper.selectList(wrapper);

        return orderList.stream().map(order -> {
            OrderDetailVO detailVO = new OrderDetailVO();
            BeanUtils.copyProperties(order, detailVO);
            // 查询订单项
            LambdaQueryWrapper<POrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(POrderItem::getOrderId, order.getId())
                    .eq(POrderItem::getIsDelete, 0);
//            detailVO.setOrderItems(orderItemMapper.selectList(itemWrapper));
            List<POrderItem> orderItems = orderItemMapper.selectList(itemWrapper);
            detailVO.setOrderItems(orderItems);

            Integer productType = null;
            if (!orderItems.isEmpty()) {
                Long productId = orderItems.get(0).getProductId();
                ProductEntity product = productMapper.selectById(productId);
                if (product != null) {
                    productType = product.getProductType();
                }
            }
            detailVO.setProductType(productType);
            // 补充超时状态
            detailVO.setTimeout(isOrderTimeout(order.getId()));
            return detailVO;
        }).collect(Collectors.toList());
    }

    /**
     * 检查订单是否超时（创建时间+15分钟 < 当前时间）
     */
    @Override
    public boolean isOrderTimeout(Long orderId) {
        POrder order = baseMapper.selectById(orderId);
        if (order == null) {
            return true;
        }
        LocalDateTime createTime = order.getCreateTime(); // 获取订单创建时间（LocalDateTime类型）
        LocalDateTime timeoutTime = createTime.plusMinutes(15); // 创建时间 + 15分钟 = 超时时间
        LocalDateTime now = LocalDateTime.now(); // 当前时间

        // 当前时间晚于超时时间 → 订单超时（返回true）
        return now.isAfter(timeoutTime);
    }

    @Override
    public List<UserComicConTicketVO> getUserComicConTickets(Long userId) {
        LambdaQueryWrapper<POrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(POrder::getUserId, userId)
                .eq(POrder::getIsDelete, 0)
                .eq(POrder::getOrderType, 1)
                .orderByDesc(POrder::getCreateTime);
        List<POrder> orderList = baseMapper.selectList(wrapper);

        if (orderList.isEmpty()) {
            return Collections.emptyList();
        }

        List<UserComicConTicketVO> result = new ArrayList<>();

        for (POrder order : orderList) {
            LambdaQueryWrapper<POrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(POrderItem::getOrderId, order.getId())
                    .eq(POrderItem::getIsDelete, 0);
            List<POrderItem> orderItems = orderItemMapper.selectList(itemWrapper);
            if (orderItems.isEmpty()) continue;

            POrderItem firstItem = orderItems.get(0);
            ProductEntity product = productMapper.selectById(firstItem.getProductId());
            if (product == null || (product.getProductType() != 1 && product.getIsTicket() != 1)) continue;

            // 从数据库读所有核销记录，每张票一条
            List<com.anime.shop.entity.OrderTicketRelationEntity> ticketRelations =
                    orderTicketRelationMapper.selectByOrderId(order.getId());

            // 构建每张票的核销信息列表
            List<com.anime.shop.controller.dto.order.TicketVerifyVO> ticketVerifyList = new ArrayList<>();
            for (com.anime.shop.entity.OrderTicketRelationEntity relation : ticketRelations) {
                com.anime.shop.controller.dto.order.TicketVerifyVO ticketVerifyVO =
                        new com.anime.shop.controller.dto.order.TicketVerifyVO();
                ticketVerifyVO.setVerifyCode(relation.getVerifyCode());
                ticketVerifyVO.setIsVerified(relation.getVerifyStatus() != null && relation.getVerifyStatus() == 1);
                ticketVerifyVO.setBuyerName(relation.getRealName());
                // 身份证脱敏
                String idCard = relation.getIdCard();
                if (idCard != null && idCard.length() == 18) {
                    idCard = idCard.substring(0, 6) + "********" + idCard.substring(14);
                }
                ticketVerifyVO.setBuyerIdCard(idCard);
                ticketVerifyVO.setVerifyQrCodeUrl(
                        com.anime.shop.util.QrCodeUtils.generateVerifyQrCode(relation.getVerifyCode())
                );
                ticketVerifyList.add(ticketVerifyVO);
            }

            // 每个订单聚合为一个VO，多张票放进ticketVerifyList
            UserComicConTicketVO vo = new UserComicConTicketVO();
            vo.setOrderId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            vo.setCreateTime(order.getCreateTime());
            vo.setPayTime(order.getPayTime());
            vo.setOrderStatus(order.getOrderStatus());
            vo.setOrderStatusName(getOrderStatusName(order.getOrderStatus()));
            vo.setComicConName(product.getProductName());
            vo.setTicketType(firstItem.getTicketType() != null ? firstItem.getTicketType() : "普通票");
            vo.setTicketPrice(firstItem.getProductPrice() != null ? firstItem.getProductPrice() : BigDecimal.ZERO);
            vo.setTicketCount(orderItems.size());
            // 封面图从订单项的商品图取，兜底用商品coverImg
            String banner = firstItem.getProductImg();
            if (banner == null || banner.isEmpty()) {
                banner = product.getCoverImg();
            }
            vo.setComicConBanner(banner);
            vo.setTicketVerifyList(ticketVerifyList);
            // 兜底：第一张票的核销码放顶层，兼容前端旧逻辑
            if (!ticketVerifyList.isEmpty()) {
                vo.setVerifyCode(ticketVerifyList.get(0).getVerifyCode());
                vo.setVerifyQrCodeUrl(ticketVerifyList.get(0).getVerifyQrCodeUrl());
                vo.setIsVerified(ticketVerifyList.stream().allMatch(t -> Boolean.TRUE.equals(t.getIsVerified())));
            } else {
                vo.setVerifyCode("");
                vo.setIsVerified(order.getOrderStatus() == 3);
            }

            result.add(vo);
        }

        return result;
    }

    private String getOrderStatusName(Integer status) {
        if (status == null) {
            return "未知状态";
        }
        switch (status) {
            case 0:
                return "未支付";
            case 1:
                return "待核销";
            case 2:
                return "待收货";
            case 3:
                return "已核销";
            case 4:
                return "已取消";
            default:
                return "未知状态";
        }
    }

}
