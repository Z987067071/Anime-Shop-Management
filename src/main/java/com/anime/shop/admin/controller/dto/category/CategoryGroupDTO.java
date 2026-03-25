package com.anime.shop.admin.controller.dto.category;

import lombok.Data;

import java.util.List;

@Data
public class CategoryGroupDTO {
    // 一级分类ID
    private Long id;
    // 一级分类名称
    private String categoryName;
    // 一级分类排序值
    private Integer sort;
    // 该一级分类下的所有二级分类
    private List<CategoryVO> secondCategoryList;
}
