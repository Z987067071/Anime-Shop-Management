package com.anime.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("u_cart")
public class CartEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long goodsId;
    private String goodsName;
    private String goodsImage;
    private BigDecimal price;
    private Integer num;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
