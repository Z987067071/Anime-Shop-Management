package com.anime.shop.admin.controller.dto.category;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 修改分类参数
 */
@Data
public class CategoryEditDTO {

    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
    private Long id;

    /**
     * 分类名称
     */
    @NotBlank(message = "分类名称不能为空")
    private String categoryName;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 分类图标URL
     */
    private String icon;

    /**
     * 状态：1=启用，0=禁用
     */
    private Integer status;
}