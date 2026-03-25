package com.anime.shop.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("comic_con")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComicCon {
    @TableId(type = IdType.AUTO)
    private Long id;          // 漫展ID
    private String name;      // 漫展名称
    private LocalDateTime startTime; // 开始时间
    private LocalDateTime endTime;   // 结束时间
    private String venue;     // 举办场馆
    private String description; // 简介
    private String bannerImg; // 封面图URL
    private Integer status;   // 状态：1-可售 0-停售 2-已结束
    private Long productId;   // 关联的商品ID（漫展票务商品）
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime; // 更新时间
}