package com.anime.shop.admin.controller.dto.order;

import lombok.Data;

@Data
public class OrderQueryDTO {
    private Integer pageNum = 1; // 页码
    private Integer pageSize = 10; // 页大小
    private String orderNo; // 订单编号
    private String consignee; // 收货人
    private String consigneePhone; // 收货人手机号
    private Integer orderStatus; // 订单状态
    private Integer payStatus; // 支付状态
    private Integer orderType; // 订单类型 0=普通 1=票务
    private Long comicConId; // 漫展ID
    private Integer verifyStatus; // 核销状态（0=未核销 1=已核销 2=已退票）
}
