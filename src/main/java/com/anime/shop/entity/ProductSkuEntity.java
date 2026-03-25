package com.anime.shop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product_sku") // 匹配你的SKU表名
public class ProductSkuEntity {
    /** SKU ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 关联商品ID（p_product.id） */
    private Long productId;
    /** 关联规格ID（product_spec.id） */
    private Long specId;
    /** 规格值（冗余：早鸟票/VIP票，避免关联查询） */
    @TableField("spec_value") // 数据库字段名
    private String specValue;
    /** 该规格价格 */
    private BigDecimal price;
    /** 库存（-1=无限制） */
    private Integer stock = -1;
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private Integer status;
}
