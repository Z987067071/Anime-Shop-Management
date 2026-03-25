package com.anime.shop.controller.dto.order;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@TableName("p_order")
public class POrder {
    @TableId(type = IdType.AUTO)
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
    private LocalDateTime cancelTime;             // 取消时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;             // 创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;             // 更新时间
    private Integer isDelete;            // 逻辑删除
    @TableField(exist = false)
    private Integer productType;
    private Integer isTicket;
    private String buyerIds;
}