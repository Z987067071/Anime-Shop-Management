package com.anime.shop.controller;

import com.anime.shop.admin.controller.dto.category.CategoryGroupDTO;
import com.anime.shop.common.Result;
import com.anime.shop.controller.dto.category.MobileCategoryVO;
import com.anime.shop.service.MobileCategoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/mobile/category")
public class MobileCategoryController {

    @Resource
    private MobileCategoryService mobileCategoryService;

    /**
     * 获取移动端分类树形列表
     */
    @GetMapping("/tree/list")
    public Result<List<MobileCategoryVO>> getMobileCategoryTree() {
        return Result.success(mobileCategoryService.listMobileCategoryTree());
    }

    @GetMapping("/group/list")
    public Result<List<CategoryGroupDTO>> getCategoryGroupList() {
        // 注意：这里要调用对应的 service 方法（和 Admin 端共用同一个 service 逻辑）
        List<CategoryGroupDTO> groupList = mobileCategoryService.getCategoryGroupList();
        return Result.success(groupList);
    }
}