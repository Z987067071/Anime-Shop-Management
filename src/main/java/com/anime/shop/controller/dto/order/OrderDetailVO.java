package com.anime.shop.controller.dto.order;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailVO {
    private Long id;                     // 订单ID
    private String orderNo;              // 订单编号
    private Long userId;                 // 用户ID
    private BigDecimal totalAmount;      // 订单总金额
    private BigDecimal payAmount;        // 实付金额
    private BigDecimal freightAmount;    // 运费
    private BigDecimal discountAmount;   // 优惠金额
    private Integer payType;             // 支付方式
    private Integer payStatus;           // 支付状态
    private Integer orderStatus;         // 订单状态
    private String consignee;            // 收货人姓名
    private String consigneePhone;       // 收货人手机号
    private String consigneeAddress;     // 收货人详细地址
    private String deliveryCompany;      // 快递公司
    private String deliverySn;           // 物流单号
    private String remark;               // 订单备注
    private LocalDateTime payTime;                // 支付时间
    private LocalDateTime deliveryTime;           // 发货时间
    private LocalDateTime receiveTime;            // 收货时间
    private LocalDateTime createTime;             // 创建时间
    private Boolean timeout;                        // 是否超时
    // 订单项列表
    private List<POrderItem> orderItems;
    // 新增：商品类型（区分漫展票/普通商品）
    private Integer productType;
    private Integer isTicket; // 是否票务订单
    private String buyerIds; // 逗号分隔的购票人ID
    private String[] buyerIdsArr; // 购票人ID数组（方便前端展示）

}
