package com.anime.shop.admin.service.impl;// com/anime/shop/admin/service/impl/AdminTicketVerifyServiceImpl.java

import com.anime.shop.admin.controller.dto.ticket.TicketVerifyDTO;
import com.anime.shop.admin.controller.dto.ticket.TicketVerifyLogQueryDTO;
import com.anime.shop.admin.mapper.order.OrderEntityMapper;
import com.anime.shop.admin.service.AdminTicketVerifyService;
import com.anime.shop.entity.OrderEntity;
import com.anime.shop.entity.OrderTicketRelationEntity;
import com.anime.shop.entity.TicketVerifyLogEntity;
import com.anime.shop.mapper.OrderTicketRelationMapper;
import com.anime.shop.mapper.TicketVerifyLogMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AdminTicketVerifyServiceImpl implements AdminTicketVerifyService {

    @Resource
    private OrderTicketRelationMapper orderTicketRelationMapper;

    @Resource
    private TicketVerifyLogMapper ticketVerifyLogMapper;


    // 如需更新订单状态，注入OrderEntityMapper
    @Resource
    private OrderEntityMapper orderEntityMapper;

    /**
     * 核心：执行票务核销
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean verifyTicket(TicketVerifyDTO dto) {
        // 1. 先查询核销码是否存在
        OrderTicketRelationEntity ticketRelation = orderTicketRelationMapper.selectByVerifyCode(dto.getVerifyCode());
        TicketVerifyLogEntity logEntity = new TicketVerifyLogEntity();
        logEntity.setVerifyCode(dto.getVerifyCode());
        logEntity.setVerifyStaff(dto.getVerifyStaff());
        logEntity.setVerifyIp(dto.getVerifyIp());
        logEntity.setCreateTime(LocalDateTime.now());

        // 2. 校验核销码有效性
        if (ticketRelation == null) {
            // 核销失败：核销码不存在
            logEntity.setVerifyResult(0);
            logEntity.setFailReason("核销码不存在");
            ticketVerifyLogMapper.insert(logEntity);
            return false;
        }

        // 3. 校验是否已核销
        if (ticketRelation.getVerifyStatus() == 1) {
            // 核销失败：已核销
            logEntity.setVerifyResult(0);
            logEntity.setFailReason("该票务已核销，不可重复核销");
            ticketVerifyLogMapper.insert(logEntity);
            return false;
        }

        // 4. 校验是否已退票
        if (ticketRelation.getVerifyStatus() == 2) {
            // 核销失败：已退票
            logEntity.setVerifyResult(0);
            logEntity.setFailReason("该票务已退票，无法核销");
            ticketVerifyLogMapper.insert(logEntity);
            return false;
        }

        // 5. 执行核销：更新票务核销状态
        LambdaUpdateWrapper<OrderTicketRelationEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(OrderTicketRelationEntity::getId, ticketRelation.getId())
                .set(OrderTicketRelationEntity::getVerifyStatus, 1) // 改为已核销
                .set(OrderTicketRelationEntity::getVerifyTime, LocalDateTime.now())
                .set(OrderTicketRelationEntity::getVerifyStaff, dto.getVerifyStaff());
        orderTicketRelationMapper.update(null, updateWrapper);

        // 6. 同步更新订单状态为"已完成"（票务订单无物流，核销即完成）
        if (ticketRelation.getOrderId() != null) {
            LambdaUpdateWrapper<OrderEntity> orderWrapper = new LambdaUpdateWrapper<>();
            orderWrapper.eq(OrderEntity::getId, ticketRelation.getOrderId())
                    .set(OrderEntity::getOrderStatus, 3) // 3=已完成
                    .set(OrderEntity::getReceiveTime, LocalDateTime.now());
            orderEntityMapper.update(null, orderWrapper);
        }

        // 7. 记录核销成功日志
        logEntity.setVerifyResult(1);
        logEntity.setOrderId(ticketRelation.getOrderId());
        logEntity.setTicketRelationId(ticketRelation.getId());
        ticketVerifyLogMapper.insert(logEntity);

        return true;
    }

    /**
     * 分页查询核销日志
     */
    @Override
    public IPage<TicketVerifyLogEntity> getVerifyLogPage(TicketVerifyLogQueryDTO query) {
        Page<TicketVerifyLogEntity> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<TicketVerifyLogEntity> queryWrapper = new LambdaQueryWrapper<>();

        // 动态拼接查询条件
        if (query.getVerifyCode() != null && !query.getVerifyCode().isEmpty()) {
            queryWrapper.like(TicketVerifyLogEntity::getVerifyCode, query.getVerifyCode());
        }
        if (query.getVerifyStaff() != null && !query.getVerifyStaff().isEmpty()) {
            queryWrapper.like(TicketVerifyLogEntity::getVerifyStaff, query.getVerifyStaff());
        }
        if (query.getVerifyResult() != null) {
            queryWrapper.eq(TicketVerifyLogEntity::getVerifyResult, query.getVerifyResult());
        }
        if (query.getOrderId() != null) {
            queryWrapper.eq(TicketVerifyLogEntity::getOrderId, query.getOrderId());
        }
        // 时间范围筛选（简单处理，如需精准可转Date）
        if (query.getStartTime() != null && !query.getStartTime().isEmpty()) {
            queryWrapper.ge(TicketVerifyLogEntity::getCreateTime, query.getStartTime());
        }
        if (query.getEndTime() != null && !query.getEndTime().isEmpty()) {
            queryWrapper.le(TicketVerifyLogEntity::getCreateTime, query.getEndTime());
        }

        // 按核销时间倒序
        queryWrapper.orderByDesc(TicketVerifyLogEntity::getCreateTime);

        return ticketVerifyLogMapper.selectPage(page, queryWrapper);
    }
}