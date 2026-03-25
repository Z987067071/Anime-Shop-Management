package com.anime.shop.admin.controller.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新增分类参数
 */
@Data
public class CategoryAddDTO {

    /**
     * 父分类ID（0为一级分类）
     */
    @NotNull(message = "父分类ID不能为空")
    private Long parentId;

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    private String categoryName;

    /**
     * 排序值
     */
    private Integer sort = 0;

    /**
     * 分类图标URL
     */
    private String icon = "";

    /**
     * 状态：1=启用，0=禁用
     */
    private Integer status = 1;
}