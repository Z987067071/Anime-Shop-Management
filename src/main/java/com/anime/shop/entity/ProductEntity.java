package com.anime.shop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("p_product")
public class ProductEntity {
    /** 商品ID */
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 商品名称 */
    private String productName;
    /** 二级分类ID */
    private Long categoryId;
    /** 封面图URL */
    private String coverImg;
    /** 售价 */
    private BigDecimal price;
    /** 原价 */
    private BigDecimal originalPrice;
    /** 剩余库存 */
    private Integer remainStock;
    /** 总库存 */
    private Integer totalStock;
    /** 状态：1=上架，0=下架 */
    private Integer status;
    /** 排序 */
    private Integer sort;
    /** 标签（新品,热销,海贼王） */
    private String tag;
    /** 一级分类ID */
    private Long firstCategoryId;
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 商品类型 0-普通商品（默认） 1-漫展虚拟商品 */
    @TableField("product_type")
    private Integer productType = 0;

    /** 是否为漫展票务 0=否 1=是 */
    @TableField("is_ticket")
    private Integer isTicket = 0;

    /** 关联漫展ID（comic_con.id） */
    @TableField("comic_con_id")
    private Long comicConId;
    /** 已售量 */
    private Integer sales;
}
