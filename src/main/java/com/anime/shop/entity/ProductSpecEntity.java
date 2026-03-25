package com.anime.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("product_spec")
public class ProductSpecEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String specName; // 规格名称（固定为“票种”）
    private String specValue; // 规格值（早鸟票/VIP票/周六普通票）
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}