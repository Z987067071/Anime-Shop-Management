package com.anime.shop.admin.controller;

import com.anime.shop.admin.controller.dto.order.OrderQueryDTO;
import com.anime.shop.admin.controller.dto.order.OrderStatusDTO;
import com.anime.shop.admin.service.AdminOrderService;
import com.anime.shop.common.Result;
import com.anime.shop.entity.OrderEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/order")
@Validated // 开启参数校验
public class AdminOrderController {

    /**
     * 注入后台订单管理Service
     */
    @Resource
    private AdminOrderService adminOrderService;

    /**
     * 分页查询订单列表（支持多条件筛选）
     * 请求方式：POST
     * 请求体：OrderQueryDTO（页码、页大小、订单编号、收货人、状态等）
     * 返回：分页订单数据
     */
    @GetMapping("/page")
    public Result<IPage<OrderEntity>> getOrderPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String consignee,
            @RequestParam(required = false) String consigneePhone,
            @RequestParam(required = false) Integer orderStatus,
            @RequestParam(required = false) Integer payStatus
    ) {
        OrderQueryDTO query = new OrderQueryDTO();
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        query.setOrderNo(orderNo);
        query.setConsignee(consignee);
        query.setConsigneePhone(consigneePhone);
        query.setOrderStatus(orderStatus);
        query.setPayStatus(payStatus);

        IPage<OrderEntity> orderPage = adminOrderService.getOrderPage(query);
        return Result.success(orderPage);
    }

    /**
     * 查询订单详情（含订单项）
     * 请求方式：GET
     * 路径参数：id（订单ID）
     * 返回：订单主信息 + 订单项列表
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getOrderDetail(@PathVariable Long id) {
        // 调用Service查询详情
        Map<String, Object> orderDetail = adminOrderService.getOrderDetail(id);
        if (orderDetail == null) {
            return Result.fail("订单不存在或已删除");
        }
        return Result.success(orderDetail);
    }

    /**
     * 修改订单状态（发货/收货/取消等）
     * 请求方式：POST
     * 请求体：OrderStatusDTO（订单ID、新状态、物流信息等）
     * 返回：修改结果
     */
    @PostMapping("/updateStatus")
    public Result<Boolean> updateOrderStatus(@Valid @RequestBody OrderStatusDTO dto) {
        boolean isSuccess = adminOrderService.updateOrderStatus(dto);
        if (isSuccess) {
            return Result.success(true);
        } else {
            return Result.fail("修改订单状态失败");
        }
    }

    /**
     * 逻辑删除订单
     * 请求方式：DELETE
     * 路径参数：id（订单ID）
     * 返回：删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteOrder(@PathVariable Long id) {
        // 调用Service删除订单
        boolean isSuccess = adminOrderService.deleteOrder(id);
        if (isSuccess) {
            return Result.success(true);
        } else {
            return Result.fail("删除订单失败");
        }
    }
}