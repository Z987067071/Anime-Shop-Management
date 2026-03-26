package com.anime.shop.admin.controller.dto.product;

import com.anime.shop.entity.ProductSkuEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductEditDTO {
    /** 商品ID（编辑时必填） */
    @NotNull(message = "商品ID不能为空")
    private Long id;

    /** 商品名称（可选修改） */
    private String productName;

    /** 二级分类ID（可选修改） */
    private Long categoryId;

    /** 封面图（可选修改） */
    private String coverImg;

    /** 售价（可选修改） */
    private BigDecimal price;

    /** 原价（可选修改） */
    private BigDecimal originalPrice;

    /** 库存（可选修改） */
    private Integer remainStock;

    private Integer totalStock;

    /** 状态：1=上架，0=下架（可选修改，不传则不更新） */
    private Integer status;

    /** 排序（可选修改） */
    private Integer sort;

    /** 标签（可选修改，如"新品,热销,海贼王"） */
    private String tag;

    /** 详情内容（可选修改） */
    private String detailContent;

    /** 商品图片列表（可选修改，传则覆盖原有图片） */
    private List<ProductImageDTO> imageList;

    /** 商品类型 0-普通商品 1-漫展虚拟商品*/
    private Integer productType;

    private Integer isTicket;
    private Long comicConId;
    private List<ProductSkuEntity> skuList;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductImageDTO {
        /** 图片ID（编辑时可选：有ID则更新，无ID则新增） */
        private Long id;

        /** 图片URL（新增/修改时必填） */
        @NotBlank(message = "图片URL不能为空")
        private String url;

        /** 类型：1=轮播图，2=详情图（必填） */
        @NotNull(message = "图片类型不能为空")
        private Integer type;

        /** 排序（可选，默认1） */
        private Integer sort = 1;
    }
}
