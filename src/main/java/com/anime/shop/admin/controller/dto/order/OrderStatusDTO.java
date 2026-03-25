package com.anime.shop.admin.controller.dto.order;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class OrderStatusDTO {
    @NotNull(message = "订单ID不能为空")
    private Long id; // 订单ID
    @NotNull(message = "订单状态不能为空")
    private Integer orderStatus; // 新订单状态
    private String deliveryCompany; // 快递公司（仅发货时填）
    private String deliverySn; // 物流单号（仅发货时填）
    private Integer verifyStatus; // 核销状态
    private String verifyStaff; // 核销员
    private String verifyCode; // 核销码
}
