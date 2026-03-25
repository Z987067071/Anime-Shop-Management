package com.anime.shop.admin.service.impl;


import com.anime.shop.admin.controller.dto.category.*;
import com.anime.shop.admin.mapper.category.ProductCategoryMapper;
import com.anime.shop.admin.service.AdminCategoryService;
import com.anime.shop.common.BizException;
import com.anime.shop.common.ResultCode;
import com.anime.shop.entity.ProductCategoryEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminCategoryServiceImpl implements AdminCategoryService {
    @Autowired
    private ProductCategoryMapper categoryMapper;

    /**
     * 修改状态
     * @param id
     * @param status
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategoryStatus(Long id, Integer status) {
        // 1. 校验参数
        if (id == null || status == null || (status != 0 && status != 1)) {
            throw new BizException(ResultCode.PARAM_ERROR);
        }
        // 2. 校验分类是否存在
        ProductCategoryEntity category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BizException(ResultCode.CATEGORY_NULLFOUND);
        }

        // 禁用一级的时候一起禁用该下面的二级
        LambdaQueryWrapper<ProductCategoryEntity> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(ProductCategoryEntity::getParentId, id);
        List<ProductCategoryEntity> children = categoryMapper.selectList(childWrapper);
        for (ProductCategoryEntity child : children) {
            child.setStatus(status);
            child.setUpdateTime(LocalDateTime.now());
            categoryMapper.updateById(child);
        }

        // 3. 修改状态+更新时间
        category.setStatus(status);
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.updateById(category);

    }

    /**
     * 新增分类（核心：层级自动计算+重名校验）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCategory(CategoryAddDTO addDTO) {
        // 1. 统一参数处理
        Long parentId = addDTO.getParentId() == null ? 0L : addDTO.getParentId().longValue();
        String categoryName = addDTO.getCategoryName().trim();

        // 空值校验
        if (categoryName.isEmpty()) {
            throw new BizException(ResultCode.PARAM_ERROR); // 分类名称不能为空
        }

        // 2. 校验父分类（父ID≠0时必须存在）
        ProductCategoryEntity parentCategory = null;
        if (parentId != 0) {
            parentCategory = categoryMapper.selectById(parentId);
            if (parentCategory == null) {
                throw new BizException(ResultCode.CATEGORY_NULLFOUND); // 父分类不存在
            }
            // 层级校验：最多支持3级分类
            if (parentCategory.getCategoryLevel() >= 3) {
                throw new BizException(ResultCode.CATEGORY_MAXTIPS); // 超过最大层级
            }
        }

        // 3. 校验：同父级下分类名称是否重复（核心，避免唯一索引冲突）
        LambdaQueryWrapper<ProductCategoryEntity> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(ProductCategoryEntity::getParentId, parentId)
                .eq(ProductCategoryEntity::getCategoryName, categoryName);
        Long count = categoryMapper.selectCount(nameWrapper);
        if (count > 0) {
            throw new BizException(ResultCode.CATEGORY_ALREADY_EXISTS); // 分类已存在
        }

        // 4. 新增分类（纯新增，无恢复逻辑）
        ProductCategoryEntity category = new ProductCategoryEntity();
        BeanUtils.copyProperties(addDTO, category);
        category.setParentId(parentId);
        category.setCategoryName(categoryName);
        // 自动计算层级：父级层级+1（父ID=0为1级）
        category.setCategoryLevel(parentId == 0 ? 1 : parentCategory.getCategoryLevel() + 1);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.insert(category);
    }

    /**
     * 修改分类（不允许修改层级/父ID，避免混乱）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editCategory(CategoryEditDTO editDTO) {
        // 1. 校验分类是否存在
        ProductCategoryEntity existCategory = categoryMapper.selectById(editDTO.getId());
        if (existCategory == null) {
            throw new BizException(ResultCode.PARAM_ERROR, "分类不存在");
        }

        // 2. 校验新名称是否与同层级重复（排除自身）
        LambdaQueryWrapper<ProductCategoryEntity> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(ProductCategoryEntity::getParentId, existCategory.getParentId())
                .eq(ProductCategoryEntity::getCategoryName, editDTO.getCategoryName())
                .ne(ProductCategoryEntity::getId, editDTO.getId());
        Long count = categoryMapper.selectCount(nameWrapper);
        if (count > 0) {
            throw new BizException(ResultCode.PARAM_ERROR, "同层级分类名称已存在");
        }

        // 3. 组装修改参数（只改名称、排序、图标、状态）
        ProductCategoryEntity updateCategory = new ProductCategoryEntity();
        updateCategory.setId(editDTO.getId());
        updateCategory.setCategoryName(editDTO.getCategoryName());
        updateCategory.setSort(editDTO.getSort());
        updateCategory.setIcon(editDTO.getIcon());
        updateCategory.setStatus(editDTO.getStatus());

        // 4. 更新数据库
        categoryMapper.updateById(updateCategory);
    }

    /**
     * 删除分类（校验是否有子分类/关联商品）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        // 1. 参数校验
        if (id == null || id <= 0) {
            throw new BizException(ResultCode.PARAM_ERROR); // 参数错误
        }

        // 2. 校验分类是否存在
        ProductCategoryEntity category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BizException(ResultCode.CATEGORY_NULLFOUND); // 分类不存在
        }

        // 3. 级联删除子分类（先删子，再删父，避免外键冲突）
        LambdaQueryWrapper<ProductCategoryEntity> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(ProductCategoryEntity::getParentId, id);
        categoryMapper.delete(childWrapper);

        // 4. 物理删除当前分类（核心：直接从数据库删除）
        categoryMapper.deleteById(id);
    }

    /**
     * 根据ID查询分类
     */
    @Override
    public CategoryVO getCategoryById(Long id) {
        ProductCategoryEntity category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BizException(ResultCode.PARAM_ERROR, "分类不存在");
        }
        CategoryVO categoryVO = new CategoryVO();
        BeanUtils.copyProperties(category, categoryVO);
        return categoryVO;
    }

    /**
     * 查询所有分类（支持按父ID/状态筛选）
     */
    @Override
    public List<CategoryVO> listCategories(Long parentId, Integer status) {
        LambdaQueryWrapper<ProductCategoryEntity> wrapper = new LambdaQueryWrapper<>();
        if (parentId != null) {
            wrapper.eq(ProductCategoryEntity::getParentId, parentId);
        }
        if (status != null) {
            wrapper.eq(ProductCategoryEntity::getStatus, status);
        }
        wrapper.orderByDesc(ProductCategoryEntity::getSort)
                .orderByAsc(ProductCategoryEntity::getId);

        List<ProductCategoryEntity> categories = categoryMapper.selectList(wrapper);
        // 转换为VO返回
        return categories.stream().map(category -> {
            CategoryVO vo = new CategoryVO();
            BeanUtils.copyProperties(category, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 查询所有一级分类（前端下拉用）
     */
    @Override
    public List<CategoryVO> listFirstLevelCategories() {
        return listCategories(0L, 1); // 父ID=0，状态=启用
    }

    @Override
    public List<CategoryGroupDTO> getCategoryGroupList() {
        // 1. 查询所有启用的一级分类（parent_id=0，status=1）
        LambdaQueryWrapper<ProductCategoryEntity> firstWrapper = new LambdaQueryWrapper<>();
        firstWrapper.eq(ProductCategoryEntity::getParentId, 0)
                .eq(ProductCategoryEntity::getStatus, 1)
                .orderByDesc(ProductCategoryEntity::getSort);
        List<ProductCategoryEntity> firstCategoryList = categoryMapper.selectList(firstWrapper);

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
            List<ProductCategoryEntity> secondCategoryList = categoryMapper.selectList(secondWrapper);

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


    @Override
    public List<CategoryTreeVO> listCategoryTree() {
        // 1. 查询所有启用的分类（扁平化列表）
        LambdaQueryWrapper<ProductCategoryEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ProductCategoryEntity::getSort);
        List<ProductCategoryEntity> allCategory = categoryMapper.selectList(wrapper);

        // 2. 转换为树形VO
        List<CategoryTreeVO> allTreeVO = allCategory.stream().map(entity -> {
            CategoryTreeVO vo = new CategoryTreeVO();
            BeanUtils.copyProperties(entity, vo);
            // 注意：如果实体类id是Long，VO是String，需要手动转换
            vo.setId(entity.getId().toString());
            vo.setParentId(entity.getParentId().toString());
            vo.setChildren(new ArrayList<>()); // 初始化children
            return vo;
        }).collect(Collectors.toList());

        // 3. 构建树形结构：先找一级分类（parentId=0），再给每个一级分类加二级分类
        List<CategoryTreeVO> treeList = new ArrayList<>();
        for (CategoryTreeVO vo : allTreeVO) {
            // 一级分类
            if ("0".equals(vo.getParentId())) {
                treeList.add(vo);
            } else {
                // 二级分类：找到对应的父分类，加入children
                for (CategoryTreeVO parentVO : allTreeVO) {
                    if (vo.getParentId().equals(parentVO.getId())) {
                        parentVO.getChildren().add(vo);
                        break;
                    }
                }
            }
        }

        return treeList;
    }
}
