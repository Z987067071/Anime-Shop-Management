package com.anime.shop.service;

import com.anime.shop.admin.controller.dto.category.CategoryGroupDTO;
import com.anime.shop.controller.dto.category.MobileCategoryVO;

import java.util.List;

public interface MobileCategoryService {

    /**
     * 获取移动端分类树形列表
     * 规则：
     * 1. 仅返回status=1的启用分类；
     * 2. 一级分类（parent_id=0）+ 其下属所有二级分类（无论二级是否启用）；
     * 3. 按sort降序排序
     * @return 树形分类列表
     */
    List<MobileCategoryVO> listMobileCategoryTree();

    List<CategoryGroupDTO> getCategoryGroupList();
}
