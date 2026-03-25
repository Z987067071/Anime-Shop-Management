package com.anime.shop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("p_order_item")
public class OrderItemEntity {
    @TableId(type = IdType.AUTO)
    private Long id; // 订单项ID
    private Long orderId; // 订单ID
    private String orderNo; // 订单编号
    private Long productId; // 商品ID
    private String productName; // 商品名称
    private String productImg; // 商品图片
    private BigDecimal productPrice; // 商品单价
    private Integer quantity; // 购买数量
    private BigDecimal totalPrice; // 商品总价
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime; // 更新时间
    @TableLogic
    private Integer isDelete; // 逻辑删除
    /** 关联SKU ID（product_sku.id） */
    @TableField("sku_id")
    private Long skuId;
    /** 票种名称（早鸟票/VIP票） */
    @TableField("ticket_type")
    private String ticketType;
}
