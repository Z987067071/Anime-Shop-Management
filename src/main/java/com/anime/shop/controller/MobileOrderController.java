package com.anime.shop.controller;

import com.anime.shop.common.Result;
import com.anime.shop.controller.dto.order.OrderDetailVO;
import com.anime.shop.controller.dto.order.OrderSubmitDTO;
import com.anime.shop.service.OrderService;
import com.anime.shop.util.ValidatorUtil;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/mobile/order")
public class MobileOrderController {

    @Resource
    private OrderService orderService;

    @Resource
    private ValidatorUtil validatorUtil;

    /**
     * 提交订单
     */
    @PostMapping("/submit")
    public Result<Long> submitOrder(@RequestBody OrderSubmitDTO submitDTO) {
        try {
            validatorUtil.validate(submitDTO);
            if (submitDTO.getIsTicket() == 0) {
                validatorUtil.validate(submitDTO, OrderSubmitDTO.NormalOrderGroup.class);
                submitDTO.setBuyerList(null);
            } else if (submitDTO.getIsTicket() == 1) {
                validatorUtil.validate(submitDTO, OrderSubmitDTO.TicketOrderGroup.class);
                submitDTO.setConsignee("");
                submitDTO.setConsigneePhone("");
                submitDTO.setConsigneeAddress("");
            } else {
                return Result.fail("票务订单标识只能是0或1");
            }
            Long orderId = orderService.submitOrder(submitDTO);
            return Result.success(orderId);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("提交订单失败：" + e.getMessage());
        }
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/detail/{orderId}")
    public Result<OrderDetailVO> getOrderDetail(@PathVariable Long orderId) {
        OrderDetailVO detailVO = orderService.getOrderDetail(orderId);
        if (detailVO == null) {
            return Result.fail("订单不存在或已删除");
        }
        return Result.success(detailVO);
    }

    /**
     * 模拟支付
     * @param orderId 订单ID
     * @param payType 支付方式（1=支付宝，2=微信）
     * @return 支付结果
     */
    @PostMapping("/pay/{orderId}/{payType}")
    public Result<Boolean> payOrder(@PathVariable Long orderId, @PathVariable Integer payType) {
        boolean success = orderService.payOrder(orderId, payType);
        if (success) {
            return Result.success(true);
        } else {
            return Result.fail("支付失败：订单不存在/已超时/已支付");
        }
    }

    /**
     * 取消订单（手动/超时自动调用）
     * @param orderId 订单ID
     * @return 取消结果
     */
    @PostMapping("/cancel/{orderId}")
    public Result<Boolean> cancelOrder(@PathVariable Long orderId) {
        boolean success = orderService.cancelOrder(orderId);
        if (success) {
            return Result.success(true);
        } else {
            return Result.fail("取消失败：订单不存在/已支付");
        }
    }

    /**
     * 获取用户所有订单
     * @param userId 用户ID
     * @return 订单列表
     */
    @GetMapping("/list/{userId}")
    public Result<List<OrderDetailVO>> getUserOrders(@PathVariable Long userId, @RequestParam(required = false) Integer status) {
        List<OrderDetailVO> orderList = orderService.getUserOrders(userId, status);
        return Result.success(orderList);
    }

    /**
     * 检查订单是否超时
     * @param orderId 订单ID
     * @return 超时状态
     */
    @GetMapping("/timeout/{orderId}")
    public Result<Boolean> checkOrderTimeout(@PathVariable Long orderId) {
        boolean timeout = orderService.isOrderTimeout(orderId);
        return Result.success(timeout);
    }
}
