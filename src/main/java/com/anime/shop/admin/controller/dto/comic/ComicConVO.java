package com.anime.shop.admin.controller.dto.comic;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ComicConVO {
    private Long id; // 漫展ID
    private String name; // 漫展名称
    private LocalDateTime startTime; // 开始时间
    private LocalDateTime endTime; // 结束时间
    private String venue; // 举办地点
    private String description; // 漫展描述
    private String bannerImg; // 横幅图片
    private Integer status; // 状态
    private Long productId; // 关联商品ID
    private String productName; // 新增：关联商品名称
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}