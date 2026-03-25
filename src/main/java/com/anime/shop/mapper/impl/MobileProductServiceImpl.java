package com.anime.shop.mapper.impl;

import com.anime.shop.admin.controller.dto.product.ProductVO;
import com.anime.shop.admin.mapper.product.ProductImageMapper;
import com.anime.shop.admin.mapper.product.ProductMapper;
import com.anime.shop.admin.mapper.product.ProductSkuMapper;
import com.anime.shop.admin.service.AdminComicConTicketService;
import com.anime.shop.admin.service.AdminProductSkuService;
import com.anime.shop.entity.*;
import com.anime.shop.mapper.ProductCollectMapper;
import com.anime.shop.service.MobileProductService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MobileProductServiceImpl implements MobileProductService {

    @Resource
    private ProductMapper productMapper;
    @Resource
    private ProductImageMapper productImageMapper;
    @Resource
    private ProductSkuMapper productSkuMapper;
    @Resource
    private AdminProductSkuService productSkuService;
    @Resource
    private ProductCollectMapper productCollectMapper;
    @Resource
    private AdminComicConTicketService adminComicConTicketService;

    /**
     * 校验商品/票种状态（适配前端checkProductStatus接口）
     * @param productId 商品ID
     * @param skuId 票种SKU ID（可选）
     * @return true=有效，false=无效
     */
    @Override
    public boolean checkProductStatus(Long productId, Long skuId) {
        // 1. 基础校验：商品ID不能为空
        if (productId == null || productId <= 0) {
            return false;
        }

        // 2. 查询商品基础信息，校验商品状态
        ProductEntity product = productMapper.selectById(productId);
        if (product == null) {
            // 商品不存在
            return false;
        }
        if (product.getStatus() != 1) {
            // 商品未上架（status=1为上架状态）
            return false;
        }

        // 3. 如果是票务商品（传了skuId），校验票种状态
        if (skuId != null && skuId > 0) {
            ProductSkuEntity sku = productSkuMapper.selectById(skuId);
            if (sku == null) {
                // 票种不存在
                return false;
            }
            if (!sku.getProductId().equals(productId)) {
                // 票种不属于当前商品
                return false;
            }
            if (sku.getStock() <= 0) {
                // 票种库存不足
                return false;
            }
        }
        // 4. 普通商品校验库存
        else {
            if (product.getRemainStock() <= 0) {
                // 商品库存不足
                return false;
            }
        }

        // 所有校验通过
        return true;
    }
    /**
     * 首页查询全部上架商品（带分页）
     */
    @Override
    public Page<ProductVO> listAll(Integer pageNum, Integer pageSize) {
        // 1. 构建分页对象
        Page<ProductEntity> page = new Page<>(pageNum, pageSize);

        // 2. 构建查询条件（仅上架，按排序降序）
        LambdaQueryWrapper<ProductEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductEntity::getStatus, 1) // 仅上架商品
                .orderByDesc(ProductEntity::getSort);

        // 3. 分页查询
        Page<ProductEntity> productPage = productMapper.selectPage(page, wrapper);

        // 4. 转换为VO并关联图片列表（核心修复）
        return convertProductPageToVOPage(productPage);
    }

    /**
     * 按一级分类查询上架商品（修复 imageList 为空）
     */
    @Override
    public Page<ProductVO> listByFirstCategory(Long firstCategoryId, Integer pageNum, Integer pageSize) {
        // 1. 构建分页对象
        Page<ProductEntity> page = new Page<>(pageNum, pageSize);

        // 2. 构建查询条件（一级分类+上架+排序）
        LambdaQueryWrapper<ProductEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductEntity::getFirstCategoryId, firstCategoryId)
                .eq(ProductEntity::getStatus, 1)
                .orderByDesc(ProductEntity::getSort);

        // 3. 分页查询
        Page<ProductEntity> productPage = productMapper.selectPage(page, wrapper);

        // 4. 转换为VO并关联图片列表
        return convertProductPageToVOPage(productPage);
    }

    @Override
    public Page<ProductVO> listByCategory(Long categoryId, Integer pageNum, Integer pageSize) {
        // 1. 构建分页对象
        Page<ProductEntity> page = new Page<>(pageNum, pageSize);

        // 2. 构建查询条件
        LambdaQueryWrapper<ProductEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductEntity::getCategoryId, categoryId)
                .eq(ProductEntity::getStatus, 1)
                .orderByDesc(ProductEntity::getSort);

        // 3. 分页查询
        Page<ProductEntity> productPage = productMapper.selectPage(page, wrapper);

        // 4. 转换为VO并关联图片列表
        return convertProductPageToVOPage(productPage);
    }

    @Override
    public Page<ProductVO> listByTag(String tag, Integer pageNum, Integer pageSize) {
        // 1. 构建分页对象
        Page<ProductEntity> page = new Page<>(pageNum, pageSize);

        // 2. 构建查询条件（模糊匹配tag）
        LambdaQueryWrapper<ProductEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(ProductEntity::getTag, tag)
                .eq(ProductEntity::getStatus, 1)
                .orderByDesc(ProductEntity::getSort);

        // 3. 分页查询
        Page<ProductEntity> productPage = productMapper.selectPage(page, wrapper);

        // 4. 转换为VO并关联图片列表
        return convertProductPageToVOPage(productPage);
    }

    /**
     * 首页推荐商品（按sort排序，取前N条）
     */
    @Override
    public List<ProductVO> listRecommend(Integer limit) {
        LambdaQueryWrapper<ProductEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductEntity::getStatus, 1)
                .orderByDesc(ProductEntity::getSort)
                .last("LIMIT " + limit); // 取前N条

        List<ProductEntity> productList = productMapper.selectList(wrapper);

        // 关联图片列表
        return convertProductListToVOList(productList);
    }

    @Override
    public ProductVO getDetail(Long id) {
        // 1. 校验参数
        if (id == null || id <= 0) {
            throw new RuntimeException("商品ID无效");
        }
        // 2. 查询商品基础+详情
        ProductVO productVO = productMapper.selectProductDetailById(id);
        if (productVO == null) {
            throw new RuntimeException("商品不存在");
        }
        // 3. 查询商品图片并封装
        List<ProductImageEntity> imageList = productImageMapper.selectByProductId(id);
        if (imageList == null) {
            imageList = new ArrayList<>();
        }
        List<ProductVO.ProductImageVO> imageVOList = imageList.stream().map(image -> {
            ProductVO.ProductImageVO imageVO = new ProductVO.ProductImageVO();
            BeanUtils.copyProperties(image, imageVO);
            return imageVO;
        }).collect(Collectors.toList());
        productVO.setImageList(imageVOList);

        if (productVO.getIsTicket() != null && productVO.getIsTicket() == 1) {
            List<ProductSkuEntity> skuList = productSkuService.listByProductId(id);

            // ================== 🔥 我加的核心修复 ==================
            // 1. 获取当前商品所有票种
            List<ComicConTicket> ticketList = adminComicConTicketService.lambdaQuery()
                    .eq(ComicConTicket::getProductId, id)
                    .list();

            // 2. 建立 skuId -> ticket 的映射
            Map<Long, ComicConTicket> ticketMap = ticketList.stream()
                    .collect(Collectors.toMap(ComicConTicket::getSkuId, t -> t));

            // 3. 把 status 塞进每个 sku
            for (ProductSkuEntity sku : skuList) {
                ComicConTicket ticket = ticketMap.get(sku.getId());
                if (ticket != null) {
                    sku.setStatus(ticket.getStatus()); // 🔥 直接赋值
                }
            }

            productVO.setSkuList(skuList);
        }

        return productVO;
    }

    @Override
    public Page<ProductVO> searchProduct(String keyword, Integer isTicket, String sort, String order, Integer pageNum, Integer pageSize) {
//        if (!StringUtils.hasText(keyword)) {
//            throw new RuntimeException("搜索关键词不能为空");
//        }
        Page<ProductEntity> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ProductEntity> wrapper = new LambdaQueryWrapper<>();
        // 搜索关键词
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w ->
                    w.like(ProductEntity::getProductName, keyword)
                            .or()
                            .like(ProductEntity::getTag, keyword)
            );
        }
        wrapper.eq(ProductEntity::getStatus, 1);

        if (isTicket != null) {
            wrapper.eq(ProductEntity::getIsTicket, isTicket);
        }
        boolean isAsc = "asc".equalsIgnoreCase(order);

        switch (sort.toLowerCase()) {
            case "default":
            default:
                wrapper.orderByDesc(ProductEntity::getSort);
                break;

            // 销量
            case "sales":
                wrapper.orderByDesc(ProductEntity::getSales);
                break;

            // 价格
            case "price":
                if (isAsc) {
                    wrapper.orderByAsc(ProductEntity::getPrice);
                } else {
                    wrapper.orderByDesc(ProductEntity::getPrice);
                }
                break;
            case "new":
                wrapper.and(w ->
                        w.like(ProductEntity::getTag, "new")
                                .or()
                                .like(ProductEntity::getTag, "新品")
                );
                wrapper.orderByDesc(ProductEntity::getCreateTime);
                break;
            case "good":
                wrapper.and(w ->
                        w.like(ProductEntity::getTag, "good")
                                .or()
                                .like(ProductEntity::getTag, "好评如潮!")
                );
                wrapper.orderByDesc(ProductEntity::getCreateTime);
                break;
        }

        Page<ProductEntity> productPage = productMapper.selectPage(page, wrapper);
        return convertProductPageToVOPage(productPage);
    }

    @Override
    public List<ProductSkuEntity> listProductSku(Long productId) {
        if (productId == null || productId <= 0) {
            throw new RuntimeException("商品ID无效");
        }
        // 复用后台AdminProductSkuService的查询逻辑
        return productSkuService.listByProductId(productId);
    }

    /**
     * 通用方法：ProductEntity分页 → ProductVO分页（批量关联图片，避免N+1查询）
     */
    private Page<ProductVO> convertProductPageToVOPage(Page<ProductEntity> productPage) {
        Page<ProductVO> voPage = new Page<>();
        BeanUtils.copyProperties(productPage, voPage); // 复制分页信息（total/size/current等）

        // 转换商品列表并关联图片
        voPage.setRecords(convertProductListToVOList(productPage.getRecords()));

        return voPage;
    }

    /**
     * 通用方法：ProductEntity列表 → ProductVO列表（批量关联图片）
     */
    private List<ProductVO> convertProductListToVOList(List<ProductEntity> productList) {
        if (CollectionUtils.isEmpty(productList)) {
            return new ArrayList<>();
        }

        // 1. 提取所有商品ID
        List<Long> productIds = productList.stream()
                .map(ProductEntity::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 2. 批量查询所有商品的图片（一次查询，性能最优）
        Map<Long, List<ProductVO.ProductImageVO>> imageMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(productIds)) {
            List<ProductImageEntity> allImages = productImageMapper.selectList(
                    new QueryWrapper<ProductImageEntity>()
                            .in("product_id", productIds)
                            .orderByAsc("sort") // 按排序值升序
            );

            // 按商品ID分组转换为ImageVO
            for (ProductImageEntity image : allImages) {
                ProductVO.ProductImageVO imageVO = new ProductVO.ProductImageVO();
                BeanUtils.copyProperties(image, imageVO);
                imageMap.computeIfAbsent(image.getProductId(), k -> new ArrayList<>()).add(imageVO);
            }
        }

        // 3. 转换商品实体为VO，并设置图片列表
        return productList.stream().map(entity -> {
            ProductVO vo = new ProductVO();
            BeanUtils.copyProperties(entity, vo);

            // 设置图片列表
            List<ProductVO.ProductImageVO> imageVOList = imageMap.getOrDefault(entity.getId(), new ArrayList<>());
            vo.setImageList(imageVOList);

            // 自动填充封面图：如果coverImg为空，取第一张图片
            if (StringUtils.isEmpty(vo.getCoverImg()) && !CollectionUtils.isEmpty(imageVOList)) {
                vo.setCoverImg(imageVOList.get(0).getImageUrl());
            }

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean collectProduct(Long userId, Long productId, Integer isCollect) {
        // 前置校验：参数不能为空
        if (userId == null || productId == null || isCollect == null) {
            throw new RuntimeException("参数不能为空");
        }

        // 1. 查询用户与该商品的所有收藏记录（包含已软删除的）
        LambdaQueryWrapper<ProductCollectEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductCollectEntity::getUserId, userId)
                .eq(ProductCollectEntity::getProductId, productId);

        ProductCollectEntity collect = productCollectMapper.selectOne(wrapper);

        // 2. 收藏操作（isCollect=1）
        if (isCollect == 1) {
            if (collect == null) {
                // 情况1：无任何记录 → 新增
                ProductCollectEntity newCollect = new ProductCollectEntity();
                newCollect.setUserId(userId);
                newCollect.setProductId(productId);
                newCollect.setIsDelete(0); // 有效收藏
                newCollect.setCreateTime(LocalDateTime.now());
                newCollect.setUpdateTime(LocalDateTime.now());
                return productCollectMapper.insert(newCollect) > 0;
            } else {
                // 情况2：有记录（无论是否删除）→ 恢复为有效收藏
                if (collect.getIsDelete() == 1) {
                    collect.setIsDelete(0);
                    collect.setUpdateTime(LocalDateTime.now());
                    return productCollectMapper.updateById(collect) > 0;
                }
                // 情况3：已处于有效收藏状态 → 直接返回成功
                return true;
            }
        }
        // 3. 取消收藏操作（isCollect=0）
        else {
            if (collect == null) {
                // 情况1：无任何记录 → 直接返回成功
                return true;
            } else {
                // 情况2：有记录且是有效收藏 → 软删除
                if (collect.getIsDelete() == 0) {
                    collect.setIsDelete(1);
                    collect.setUpdateTime(LocalDateTime.now());
                    return productCollectMapper.updateById(collect) > 0;
                }
                // 情况3：已处于删除状态 → 直接返回成功
                return true;
            }
        }
    }

    @Override
    public Page<ProductVO> getCollectList(Long userId, Integer pageNum, Integer pageSize) {
        // 1. 分页查询用户的收藏记录（未删除）
        Page<ProductCollectEntity> collectPage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ProductCollectEntity> collectWrapper = new LambdaQueryWrapper<>();
        collectWrapper.eq(ProductCollectEntity::getUserId, userId)
                .eq(ProductCollectEntity::getIsDelete, 0)
                .orderByDesc(ProductCollectEntity::getCreateTime); // 按收藏时间倒序

        productCollectMapper.selectPage(collectPage, collectWrapper);

        // 2. 提取收藏的商品ID列表
        List<Long> productIds = collectPage.getRecords().stream()
                .map(ProductCollectEntity::getProductId)
                .filter(Objects::nonNull) // 过滤空ID
                .collect(Collectors.toList());

        // 3. 分页查询商品详情（仅上架、未删除的商品）
        Page<ProductEntity> entityPage = new Page<>(pageNum, pageSize);
        if (!CollectionUtils.isEmpty(productIds)) {
            LambdaQueryWrapper<ProductEntity> productWrapper = new LambdaQueryWrapper<>();
            productWrapper.in(ProductEntity::getId, productIds)
                    .eq(ProductEntity::getStatus, 1); // 仅上架商品

            entityPage = productMapper.selectPage(entityPage, productWrapper);
        }

        // 4. 核心：复用你已有的convertProductPageToVOPage方法（自动关联图片）
        Page<ProductVO> productVOPage = convertProductPageToVOPage(entityPage);

        // 5. 同步收藏记录的总数（如果商品列表为空，但有收藏记录）
        if (productVOPage.getTotal() == 0 && collectPage.getTotal() > 0) {
            productVOPage.setTotal(collectPage.getTotal());
            productVOPage.setPages(collectPage.getPages());
        }

        return productVOPage;
    }

    @Override
    public boolean existsCollection(Long userId, Long productId) {
        LambdaQueryWrapper<ProductCollectEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductCollectEntity::getUserId, userId)
                .eq(ProductCollectEntity::getProductId, productId)
                .eq(ProductCollectEntity::getIsDelete, 0); // 未删除才是有效收藏

        return productCollectMapper.selectCount(wrapper) > 0;
    }
}