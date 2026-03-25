package com.anime.shop.mapper.impl;

import com.anime.shop.admin.controller.dto.category.CategoryGroupDTO;
import com.anime.shop.admin.controller.dto.category.CategoryVO;
import com.anime.shop.admin.mapper.category.ProductCategoryMapper;
import com.anime.shop.controller.dto.category.MobileCategoryVO;
import com.anime.shop.entity.ProductCategoryEntity;
import com.anime.shop.service.MobileCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MobileCategoryServiceImpl implements MobileCategoryService {

    @Resource
    private ProductCategoryMapper productCategoryMapper;

    @Override
    public List<MobileCategoryVO> listMobileCategoryTree() {
        // 步骤1：查询所有启用的分类（status=1）
        LambdaQueryWrapper<ProductCategoryEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductCategoryEntity::getStatus, 1)
                .orderByDesc(ProductCategoryEntity::getSort);
        List<ProductCategoryEntity> allEnableCategory = productCategoryMapper.selectList(wrapper);

        // 步骤2：筛选启用的一级分类（parent_id=0）
        List<ProductCategoryEntity> firstLevelList = allEnableCategory.stream()
                .filter(entity -> entity.getParentId().equals(0L))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(firstLevelList)) {
            return new ArrayList<>();
        }

        // 步骤3：提取一级分类ID，筛选其下属的二级分类（无需判断二级状态）
        Set<Long> firstLevelIds = firstLevelList.stream()
                .map(ProductCategoryEntity::getId)
                .collect(Collectors.toSet());
        // 查询所有二级分类（无论状态，只要父分类是启用的一级分类）
        LambdaQueryWrapper<ProductCategoryEntity> secondLevelWrapper = new LambdaQueryWrapper<>();
        secondLevelWrapper.in(ProductCategoryEntity::getParentId, firstLevelIds)
                .orderByDesc(ProductCategoryEntity::getSort);
        List<ProductCategoryEntity> secondLevelList = productCategoryMapper.selectList(secondLevelWrapper);

        // 步骤4：转换为移动端VO并组装树形结构
        // 4.1 转换一级分类
        List<MobileCategoryVO> treeVOList = firstLevelList.stream().map(entity -> {
            MobileCategoryVO vo = new MobileCategoryVO();
            BeanUtils.copyProperties(entity, vo);
            // 适配前端：Long转String
            vo.setId(entity.getId().toString());
            vo.setParentId(entity.getParentId().toString());
            vo.setIcon(entity.getIcon()); // 分类图标（从你的分类表中获取）
            return vo;
        }).collect(Collectors.toList());

        // 4.2 转换二级分类并关联到一级分类
        List<MobileCategoryVO> secondLevelVOList = secondLevelList.stream().map(entity -> {
            MobileCategoryVO vo = new MobileCategoryVO();
            BeanUtils.copyProperties(entity, vo);
            vo.setId(entity.getId().toString());
            vo.setParentId(entity.getParentId().toString());
            vo.setIcon(entity.getIcon());
            return vo;
        }).collect(Collectors.toList());

        // 4.3 关联二级分类到对应一级分类
        for (MobileCategoryVO secondVO : secondLevelVOList) {
            for (MobileCategoryVO firstVO : treeVOList) {
                if (secondVO.getParentId().equals(firstVO.getId())) {
                    firstVO.getChildren().add(secondVO);
                    break;
                }
            }
        }

        return treeVOList;
    }

    @Override
    public List<CategoryGroupDTO> getCategoryGroupList() {
        // 1. 查询所有启用的一级分类（parent_id=0，status=1）
        LambdaQueryWrapper<ProductCategoryEntity> firstWrapper = new LambdaQueryWrapper<>();
        firstWrapper.eq(ProductCategoryEntity::getParentId, 0)
                .eq(ProductCategoryEntity::getStatus, 1)
                .orderByDesc(ProductCategoryEntity::getSort);
        List<ProductCategoryEntity> firstCategoryList = productCategoryMapper.selectList(firstWrapper);

        // 2. 构建分组结构
        List<CategoryGroupDTO> groupList = new ArrayList<>();
        for (ProductCategoryEntity first : firstCategoryList) {
            CategoryGroupDTO group = new CategoryGroupDTO();
            group.setId(first.getId());
            group.setCategoryName(first.getCategoryName());
            group.setSort(first.getSort());

            // 3. 查询当前一级分类下的所有二级分类
            LambdaQueryWrapper<ProductCategoryEntity> secondWrapper = new LambdaQueryWrapper<>();
            secondWrapper.eq(ProductCategoryEntity::getParentId, first.getId())
                    .eq(ProductCategoryEntity::getStatus, 1)
                    .orderByDesc(ProductCategoryEntity::getSort);
            List<ProductCategoryEntity> secondCategoryList = productCategoryMapper.selectList(secondWrapper);

            // 4. 转换为VO
            List<CategoryVO> secondVOList = secondCategoryList.stream()
                    .map(second -> {
                        CategoryVO vo = new CategoryVO();
                        BeanUtils.copyProperties(second, vo);
                        return vo;
                    })
                    .collect(Collectors.toList());

            group.setSecondCategoryList(secondVOList);
            groupList.add(group);
        }

        return groupList;
    }
}