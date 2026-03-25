package com.anime.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("p_product_image")
public class ProductImageEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 商品ID */
    private Long productId;
    /** 图片URL */
    private String imageUrl;
    /** 类型：1=轮播图，2=详情图 */
    private Integer type;
    /** 排序 */
    private Integer sort;
}