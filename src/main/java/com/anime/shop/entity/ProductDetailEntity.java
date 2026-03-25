package com.anime.shop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("p_product_detail")
public class ProductDetailEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 商品ID */
    private Long productId;
    /** 详情富文本 */
    private String detailContent;
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
