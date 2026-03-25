package com.anime.shop.controller.dto.cart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CartDTO {
    private Long userId;
    private Long goodsId;
    private Integer num;
    private String skuId;        // 票种/SKU ID
    private String ticketType;   // 票种名称
    private String goodsName;    // 商品名称（可选，按需添加）
    private String goodsImage;   // 商品图片（可选，按需添加）
    private String price;        // 单价（可选，按需添加）
}
