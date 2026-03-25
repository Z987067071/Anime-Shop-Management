package com.anime.shop.controller.dto.category;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 移动端分类VO
 * 核心：仅保留移动端需要的字段，结构为树形（一级分类+下属二级分类）
 */
@Data
public class MobileCategoryVO {
    /** 分类ID（字符串类型，适配前端常用格式） */
    private String id;

    /** 分类名称 */
    private String categoryName;

    /** 父分类ID（一级分类为"0"） */
    private String parentId;

    /** 分类图标（移动端展示用） */
    private String icon;

    /** 排序值（前端按sort降序展示） */
    private Integer sort;

    /** 下属二级分类列表（一级分类才有值） */
    private List<MobileCategoryVO> children = new ArrayList<>();
}
