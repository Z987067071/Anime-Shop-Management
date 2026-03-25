package com.anime.shop.controller.dto.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItemDTO {
    @NotNull(message = "商品ID不能为空")
    private Long productId;              // 商品ID
    @NotBlank(message = "商品名称不能为空")
    private String productName;          // 商品名称
    private String productImg;           // 商品图片
    @NotNull(message = "商品单价不能为空")
    private BigDecimal productPrice;     // 商品单价
    @NotNull(message = "购买数量不能为空")
    private Integer quantity;            // 购买数量
    @NotNull(message = "商品总价不能为空")
    private BigDecimal totalPrice;       // 商品总价
    private Long skuId;
    private String ticketType;
    private String skuName;
}
