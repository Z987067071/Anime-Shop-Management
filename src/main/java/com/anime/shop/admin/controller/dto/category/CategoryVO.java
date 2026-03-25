package com.anime.shop.admin.controller.dto.category;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 分类返回视图对象
 */
@Data
public class CategoryVO {
    private Long id;
    private Long parentId;
    private String categoryName;
    private Integer categoryLevel;
    private Integer sort;
    private String icon;
    private Integer status;
    private LocalDateTime createTime;
}