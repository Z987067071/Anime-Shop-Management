package com.anime.shop.admin.service;

import com.anime.shop.admin.controller.dto.category.*;

import java.util.List;

public interface  AdminCategoryService {
    /**
     * 新增分类
     */
    void addCategory(CategoryAddDTO addDTO);

    /**
     * 修改分类
     */
    void editCategory(CategoryEditDTO editDTO);

    /**
     * 删除分类（软删除）
     */
    void deleteCategory(Long id);

    /**
     * 根据ID查询分类
     */
    CategoryVO getCategoryById(Long id);

    /**
     * 查询所有分类（分页/筛选）
     */
    List<CategoryVO> listCategories(Long parentId, Integer status);

    /**
     * 查询所有一级分类（用于前端下拉）
     */
    List<CategoryVO> listFirstLevelCategories();

    /**
     * 移动端分类分组查询方法（接口中声明）
     */
    List<CategoryGroupDTO> getCategoryGroupList();

    List<CategoryTreeVO> listCategoryTree();

    void updateCategoryStatus(Long id, Integer status);
}
