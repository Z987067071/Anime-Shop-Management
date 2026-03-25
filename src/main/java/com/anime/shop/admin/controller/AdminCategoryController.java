package com.anime.shop.admin.controller;

import com.anime.shop.admin.controller.dto.category.*;
import com.anime.shop.admin.service.AdminCategoryService;
import com.anime.shop.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/category")
public class AdminCategoryController {
    @Autowired
    private AdminCategoryService categoryService;

    /**
     * 新增分类
     */
    @PostMapping("/add")
    public Result<Void> addCategory(@Validated @RequestBody CategoryAddDTO addDTO) {
        categoryService.addCategory(addDTO);
        return Result.success();
    }

    /**
     * 修改分类
     */
    @PostMapping("/edit")
    public Result<Void> editCategory(@Validated @RequestBody CategoryEditDTO editDTO) {
        categoryService.editCategory(editDTO);
        return Result.success();
    }

    /**
     * 删除分类
     */
    @PostMapping("/delete/{id}")
    public Result<Void> deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }

    /**
     * 根据ID查询分类
     */
    @GetMapping("/get/{id}")
    public Result<CategoryVO> getCategoryById(@PathVariable Long id) {
        return Result.success(categoryService.getCategoryById(id));
    }

    /**
     * 查询分类列表（支持按父ID/状态筛选）
     */
    @GetMapping("/list")
    public Result<List<CategoryVO>> listCategories(
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) Integer status) {
        return Result.success(categoryService.listCategories(parentId, status));
    }

    /**
     * 查询所有一级分类
     */
    @GetMapping("/first-level")
    public Result<List<CategoryVO>> listFirstLevelCategories() {
        return Result.success(categoryService.listFirstLevelCategories());
    }

    /**
     * 获取分类分组列表（移动端专用：一级+二级）
     */
    @GetMapping("/group/list")
    public Result<List<CategoryGroupDTO>> getCategoryGroupList() {
        List<CategoryGroupDTO> groupList = categoryService.getCategoryGroupList();
        return Result.success(groupList);
    }

    @GetMapping("/tree/list")
    public Result<List<CategoryTreeVO>> listCategoryTree() {
        List<CategoryTreeVO> treeList = categoryService.listCategoryTree();
        return Result.success(treeList);
    }

    @PostMapping("/updateStatus")
    public Result<Void> updateCategoryStatus(@RequestBody CategoryStatusDTO dto) {
        categoryService.updateCategoryStatus(dto.getId(), dto.getStatus());
        return Result.success();
    }
}
