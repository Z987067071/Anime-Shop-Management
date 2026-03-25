package com.anime.shop.admin.controller.dto.category;

import lombok.Data;

@Data
public class CategoryStatusDTO {
    private Long id;
    private Integer status; // 1=启用，0=禁用
}
