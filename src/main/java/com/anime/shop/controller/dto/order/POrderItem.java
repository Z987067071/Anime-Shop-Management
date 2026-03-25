package com.anime.shop.controller.dto.order;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("p_order_item")
public class POrderItem {
    /**
     * 订单项ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID（关联p_order.id）
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 订单编号（冗余字段，方便查询）
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 商品ID
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 票种SKU ID（关联product_sku.id）
     */
    @TableField("sku_id")
    private Long skuId;

    /**
     * 票种名称（早鸟票/VIP票/普通票）
     */
    @TableField("ticket_type")
    private String ticketType;

    /**
     * 商品名称
     */
    @TableField("product_name")
    private String productName;

    /**
     * 商品图片
     */
    @TableField("product_img")
    private String productImg;

    /**
     * 商品单价（下单时的价格，避免商品价格变动影响订单）
     */
    @TableField("product_price")
    private BigDecimal productPrice;

    /**
     * 购买数量
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 商品总价（product_price * quantity）
     */
    @TableField("total_price")
    private BigDecimal totalPrice;

    /**
     * 逻辑删除（0-未删除 1-已删除）
     */
    @TableField("is_delete")
    private Integer isDelete;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private Long buyerId; // 购票人ID
    private String buyerName; // 购票人姓名
    private String buyerIdCard; // 购票人身份证号
}
