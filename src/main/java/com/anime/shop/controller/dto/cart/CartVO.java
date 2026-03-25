package com.anime.shop.controller.dto.cart;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartVO {
    private Long id;
    private Long userId;
    private Long goodsId;
    private String goodsName;
    private String goodsImage;
    private BigDecimal price;
    private Integer num;
    private Integer selected;
}
