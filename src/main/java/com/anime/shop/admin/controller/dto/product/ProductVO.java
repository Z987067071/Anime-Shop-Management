package com.anime.shop.admin.controller.dto.product;

import com.anime.shop.entity.ProductSkuEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductVO {
    /** 商品ID */
    private Long id;
    /** 商品名称 */
    private String productName;
    /** 二级分类ID */
    private Long categoryId;
    /** 二级分类名称 */
    private String categoryName;
    /** 一级分类ID */
    private Long firstCategoryId;
    /** 一级分类名称 */
    private String firstCategoryName;
    /** 封面图 */
    private String coverImg;
    /** 售价 */
    private BigDecimal price;
    /** 原价 */
    private BigDecimal originalPrice;
    /** 库存 */
    private Integer remainStock;
    private Integer totalStock;
    /** 状态：1=上架，0=下架 */
    private Integer status;
    /** 排序 */
    private Integer sort;
    /** 标签 */
    private String tag;
    /** 创建时间 */
    private LocalDateTime createTime;
    /** 详情内容 */
    private String detailContent;
    /** 图片列表 */
    private List<ProductImageVO> imageList;
    /** 商品类型 0-普通商品 1-漫展虚拟商品 */
    private Integer productType;
    private Integer isTicket;
    private Long comicConId;
    private List<ProductSkuEntity> skuList;
    private Integer sales;

    @Data
    public static class ProductImageVO {
        private Long id;
        private String imageUrl;
        private Integer type;
        private Integer sort;
    }
}