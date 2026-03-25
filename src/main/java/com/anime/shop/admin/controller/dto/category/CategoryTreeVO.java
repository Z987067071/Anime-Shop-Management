package com.anime.shop.admin.controller.dto.category;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CategoryTreeVO {
    private String id;
    private String parentId;
    private String categoryName;
    private Integer categoryLevel;
    private Integer sort;
    private String icon;
    private Integer status;
    private LocalDateTime createTime;
    // 核心：下属二级分类列表
    private List<CategoryTreeVO> children;
}