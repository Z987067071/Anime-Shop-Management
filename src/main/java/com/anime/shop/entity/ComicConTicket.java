package com.anime.shop.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("comic_con_ticket")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComicConTicket {
    @TableId(type = IdType.AUTO)
    private Long id;                // 票种ID（主键）
    private Long productId;
    private Long comicConId;        // 关联漫展ID
    private String ticketName;      // 票种名称
    private BigDecimal price;       // 售价
    private Integer stock;          // 剩余库存
    private Integer totalStock;     // 总库存
    private Boolean needRealName;   // 是否需要实名
    private Integer maxBuy;         // 单人限购张数
    private LocalDateTime saleStart;
    private LocalDateTime saleEnd;
    private Integer status;         // 状态：1=正常/0=下架/2=售罄
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime; // 更新时间
    @TableField("sku_id")
    private Long skuId;
}
