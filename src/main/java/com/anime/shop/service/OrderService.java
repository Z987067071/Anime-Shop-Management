package com.anime.shop.service;

import com.anime.shop.controller.dto.order.OrderDetailVO;
import com.anime.shop.controller.dto.order.OrderSubmitDTO;
import com.anime.shop.controller.dto.order.POrder;
import com.anime.shop.controller.dto.order.UserComicConTicketVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface OrderService extends IService<POrder> {
    /**
     * 提交订单
     * @param submitDTO 订单提交参数
     * @return 订单ID
     */
    Long submitOrder(OrderSubmitDTO submitDTO);

    /**
     * 获取订单详情
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderDetailVO getOrderDetail(Long orderId);
    /**
     * 模拟支付（修改订单支付状态）
     * @param orderId 订单ID
     * @param payType 支付方式（1=支付宝，2=微信）
     * @return 是否成功
     */
    boolean payOrder(Long orderId, Integer payType);

    /**
     * 取消订单（超时/手动取消）
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean cancelOrder(Long orderId);

    /**
     * 获取用户所有订单
     * @param userId 用户ID
     * @return 订单列表
     */
    List<OrderDetailVO> getUserOrders(Long userId, Integer status);

    default List<OrderDetailVO> getUserOrders(Long userId) {
        return getUserOrders(userId, null);
    }



    /**
     * 检查订单是否超时（15分钟）
     * @param orderId 订单ID
     * @return true=超时，false=未超时
     */
    boolean isOrderTimeout(Long orderId);

    /**
     * 查询当前用户的漫展票务订单
     * @param userId 用户ID
     * @return 票务订单列表
     */
    List<UserComicConTicketVO> getUserComicConTickets(Long userId);
}
