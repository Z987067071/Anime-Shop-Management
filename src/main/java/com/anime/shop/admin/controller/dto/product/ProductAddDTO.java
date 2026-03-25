package com.anime.shop.admin.controller.dto.product;

import com.anime.shop.entity.ProductSkuEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductAddDTO {
    /** 商品名称 */
    @NotBlank(message = "商品名称不能为空")
    private String productName;
    /** 二级分类ID */
    @NotNull(message = "分类不能为空")
    private Long categoryId;
    /** 封面图 */
    private String coverImg;
    /** 售价 */
    @NotNull(message = "售价不能为空")
    private BigDecimal price;
    /** 原价 */
    private BigDecimal originalPrice;
    /** 库存 */
    @NotNull(message = "库存不能为空")
    private Integer remainStock;
    private Integer totalStock;
    /** 排序 */
    private Integer sort = 1;
    /** 标签 */
    private String tag;
    /** 详情内容 */
    private String detailContent;
    /** 商品图片列表 */
    private List<ProductImageDTO> imageList;
    /** 商品类型 0-普通商品（默认） 1-漫展虚拟商品*/
    private Integer productType = 0;

    private Integer isTicket; // 新增：是否为票务
    private Long comicConId; // 新增：关联漫展ID
    private List<ProductSkuEntity> skuList; //票种SKU列表

    // 图片DTO内部类
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductImageDTO {
        /** 图片URL */
        @NotBlank(message = "图片URL不能为空")
        private String url;
        private String name;
        /** 类型：1=轮播图，2=详情图 */
        @NotNull(message = "图片类型不能为空")
        private Integer type;
        /** 排序 */
        private Integer sort = 1;
    }
}
