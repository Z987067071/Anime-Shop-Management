package com.anime.shop.admin.controller.dto.product;

import lombok.Data;

@Data
public class ProductQueryDTO {
    /** 页码 */
    private Integer pageNum = 1;
    /** 页大小 */
    private Integer pageSize = 10;
    /** 商品名称（模糊查询） */
    private String productName;
    /** 二级分类ID */
    private Long categoryId;
    /** 一级分类ID（用于筛选下属二级分类的商品） */
    private Long firstCategoryId;
    /** 状态：1=上架，0=下架 */
    private Integer status;
    /** 标签（IP） */
    private String tag;
    /** 商品类型 0-普通商品 1-漫展虚拟商品*/
    private Integer productType;
    private Integer isTicket; // 1=票务商品，0=普通商品
}