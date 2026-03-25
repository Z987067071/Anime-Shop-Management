package com.anime.shop.config;

import com.anime.shop.controller.dto.order.POrder;
import com.anime.shop.enums.OrderStatusEnum;
import com.anime.shop.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderTimeoutTask {

    @Resource
    private OrderService orderService;

    /**
     * 每1分钟扫描一次未支付订单，取消超时的
     */
    @Scheduled(fixedRate = 60 * 1000) // 60秒执行一次
    public void cancelTimeoutOrders() {
        // 查询所有未支付订单
        List<POrder> unpaidOrders = orderService.list(
                new LambdaQueryWrapper<POrder>()
                        .eq(POrder::getOrderStatus, OrderStatusEnum.UNPAID.getCode())
                        .eq(POrder::getIsDelete, 0)
        );
        // 遍历取消超时订单
        for (POrder order : unpaidOrders) {
            if (orderService.isOrderTimeout(order.getId())) {
                orderService.cancelOrder(order.getId());
            }
        }
    }
}
