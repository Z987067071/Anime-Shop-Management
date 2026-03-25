package com.anime.shop.admin.service.impl;

import com.anime.shop.admin.controller.dto.product.ProductAddDTO;
import com.anime.shop.admin.controller.dto.product.ProductEditDTO;
import com.anime.shop.admin.controller.dto.product.ProductQueryDTO;
import com.anime.shop.admin.controller.dto.product.ProductVO;
import com.anime.shop.admin.mapper.product.ProductDetailMapper;
import com.anime.shop.admin.mapper.product.ProductImageMapper;
import com.anime.shop.admin.mapper.product.ProductMapper;
import com.anime.shop.admin.service.AdminProductService;
import com.anime.shop.admin.service.AdminProductSkuService;
import com.anime.shop.common.BizException;
import com.anime.shop.common.ResultCode;
import com.anime.shop.entity.ProductDetailEntity;
import com.anime.shop.entity.ProductEntity;
import com.anime.shop.entity.ProductImageEntity;
import com.anime.shop.entity.ProductSkuEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminProductServiceImpl extends ServiceImpl<ProductMapper, ProductEntity> implements AdminProductService {

    @Resource
    private ProductMapper productMapper;
    @Resource
    private ProductDetailMapper productDetailMapper;
    @Resource
    private ProductImageMapper productImageMapper;
    @Resource
    private AdminProductSkuService productSkuService; // 注意你的Service名称是AdminProductSkuService

    @Override
    public List<ProductSkuEntity> listProductSku(Long productId) {
        return productSkuService.listByProductId(productId);
    }

    @Override
    public IPage<ProductVO> pageProduct(ProductQueryDTO dto) {
        Page<ProductVO> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        // 1. 分页查询商品基础信息
        IPage<ProductVO> productPage = productMapper.selectProductPage(page, dto);

        // 2. 批量关联查询图片列表（最终修复）
        if (!CollectionUtils.isEmpty(productPage.getRecords())) {
            List<Long> productIds = productPage.getRecords().stream()
                    .map(ProductVO::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(productIds)) {
                // 核心修复：查询时不限制type（兼容所有图片类型）
                List<ProductImageEntity> allImages = productImageMapper.selectList(
                        new QueryWrapper<ProductImageEntity>()
                                .in("product_id", productIds)
                                .orderByAsc("sort")
                );

                // 打印查询结果（调试）
                System.out.println("批量查询到图片总数：" + allImages.size());

                Map<Long, List<ProductVO.ProductImageVO>> imageMap = new HashMap<>();
                for (ProductImageEntity image : allImages) {
                    ProductVO.ProductImageVO imageVO = new ProductVO.ProductImageVO();
                    BeanUtils.copyProperties(image, imageVO);
                    // 确保imageUrl不为空
                    imageVO.setImageUrl(StringUtils.isEmpty(image.getImageUrl()) ? "" : image.getImageUrl());
                    imageMap.computeIfAbsent(image.getProductId(), k -> new ArrayList<>()).add(imageVO);
                }

                // 给每个商品VO设置图片列表
                productPage.getRecords().forEach(vo -> {
                    List<ProductVO.ProductImageVO> imageVOList = imageMap.getOrDefault(vo.getId(), new ArrayList<>());
                    vo.setImageList(imageVOList);
                    System.out.println("商品" + vo.getId() + "绑定图片数量：" + imageVOList.size());

                    // 自动填充封面图
                    if (StringUtils.isEmpty(vo.getCoverImg()) && !CollectionUtils.isEmpty(imageVOList)) {
                        vo.setCoverImg(imageVOList.get(0).getImageUrl());
                    }
                });
            }
        }

        return productPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProduct(ProductAddDTO dto) {
        // 1. 新增商品基础信息
        ProductEntity product = new ProductEntity();
        BeanUtils.copyProperties(dto, product);
        product.setStatus(1); // 默认上架
        if (dto.getProductType() != null && dto.getProductType() == 1) {
            product.setProductType(1);
            product.setIsTicket(1);
        } else {
            product.setProductType(0);
            product.setIsTicket(0);
        }

        productMapper.insert(product);
        Long productId = product.getId();

        // 2. 新增商品详情
        if (dto.getDetailContent() != null && !dto.getDetailContent().isEmpty()) {
            ProductDetailEntity detail = new ProductDetailEntity();
            detail.setProductId(productId);
            detail.setDetailContent(dto.getDetailContent());
            productDetailMapper.insert(detail);
        }

        // 3. 新增商品图片（核心修复：兼容漫展商品，放宽校验）
        if (!CollectionUtils.isEmpty(dto.getImageList())) {
            int insertCount = 0;
            for (ProductAddDTO.ProductImageDTO imageDTO : dto.getImageList()) {
                // 核心修复：只校验URL非空，type默认1（不再跳过）
                if (imageDTO == null || imageDTO.getUrl() == null || imageDTO.getUrl().trim().isEmpty()) {
                    continue;
                }

                ProductImageEntity image = new ProductImageEntity();
                image.setProductId(productId);
                image.setImageUrl(imageDTO.getUrl().trim());
                // 修复：sort默认0 → 1（和编辑逻辑统一）
                image.setSort(imageDTO.getSort() != null ? imageDTO.getSort() : 1);
                // 修复：type取DTO值，无则默认1（兼容前端不传type）
                image.setType(imageDTO.getType() != null ? imageDTO.getType() : 1);

                // 打印完整参数（调试关键）
                System.out.println("新增商品图片：productId=" + productId + ", url=" + imageDTO.getUrl().trim() + ", type=" + image.getType() + ", sort=" + image.getSort());

                productImageMapper.insert(image);
                insertCount++;
            }
            System.out.println("商品" + productId + "新增图片总数：" + insertCount);
        } else {
            System.out.println("商品" + productId + "无图片需要新增");
        }

        // 4. 保存SKU
        if (dto.getSkuList() != null && !dto.getSkuList().isEmpty()) {
            productSkuService.saveBatch(productId, dto.getSkuList());
            System.out.println("商品" + productId + "新增SKU数量：" + dto.getSkuList().size());
        }
    }

    @Override
    public ProductVO getProductDetail(Long id) {
        if (id == null) {
            throw new BizException(ResultCode.PARAM_ERROR);
        }
        ProductVO productVO = productMapper.selectProductDetailById(id);
        if (productVO == null) {
            throw new BizException(ResultCode.PRODUCT_NOT_FOUND);
        }

        // 核心修复：查询所有类型的图片，不限制type
        List<ProductImageEntity> imageList = productImageMapper.selectList(
                new QueryWrapper<ProductImageEntity>()
                        .eq("product_id", id)
                        .orderByAsc("sort")
        );
        System.out.println("商品" + id + "详情查询到图片数量：" + imageList.size());

        List<ProductVO.ProductImageVO> imageVOList = imageList.stream().map(image -> {
            ProductVO.ProductImageVO imageVO = new ProductVO.ProductImageVO();
            BeanUtils.copyProperties(image, imageVO);
            return imageVO;
        }).collect(Collectors.toList());
        productVO.setImageList(imageVOList);

        // 查询SKU
        List<ProductSkuEntity> skuList = productSkuService.listByProductId(id);
        productVO.setSkuList(skuList);

        return productVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editProduct(ProductEditDTO dto) {
        // 1. 校验商品是否存在
        Long productId = dto.getId();
        ProductEntity existProduct = productMapper.selectById(productId);
        if (existProduct == null) {
            throw new BizException(ResultCode.PRODUCT_NOT_FOUND);
        }

        // 2. 更新商品基础信息
        ProductEntity product = new ProductEntity();
        BeanUtils.copyProperties(dto, product);
        if (dto.getProductType() != null) {
            if (dto.getProductType() == 1) {
                product.setProductType(1);
                product.setIsTicket(1);
            } else {
                product.setProductType(0);
                product.setIsTicket(0);
            }
        } else {
            product.setProductType(existProduct.getProductType());
            product.setIsTicket(existProduct.getIsTicket());
        }
        productMapper.updateById(product);

        // 3. 更新商品详情
        productDetailMapper.deleteByProductId(productId);
        if (dto.getDetailContent() != null && !dto.getDetailContent().isEmpty()) {
            ProductDetailEntity detail = new ProductDetailEntity();
            detail.setProductId(productId);
            detail.setDetailContent(dto.getDetailContent());
            productDetailMapper.insert(detail);
        }

        // 4. 更新商品图片（核心修复：前端未传imageList则不删旧图片）
        boolean hasNewImages = !CollectionUtils.isEmpty(dto.getImageList());
        int deleteCount = 0;
        if (hasNewImages) {
            // 只有前端传了新图片，才删除旧图片
            deleteCount = productImageMapper.deleteByProductId(productId);
            System.out.println("删除商品" + productId + "的旧图片数量：" + deleteCount);

            // 插入新图片（放宽校验）
            int insertCount = 0;
            for (ProductEditDTO.ProductImageDTO imageDTO : dto.getImageList()) {
                // 核心修复：只校验URL非空，type/sort为空则默认值
                if (imageDTO == null || imageDTO.getUrl() == null || imageDTO.getUrl().trim().isEmpty()) {
                    continue;
                }

                ProductImageEntity image = new ProductImageEntity();
                image.setProductId(productId);
                image.setImageUrl(imageDTO.getUrl().trim());
                // 修复：type为空则默认1（不再跳过）
                image.setType(imageDTO.getType() != null ? imageDTO.getType() : 1);
                image.setSort(imageDTO.getSort() != null ? imageDTO.getSort() : 1);

                System.out.println("编辑商品图片：productId=" + productId + ", url=" + imageDTO.getUrl().trim() + ", type=" + image.getType());
                productImageMapper.insert(image);
                insertCount++;
            }
            System.out.println("插入商品" + productId + "的新图片数量：" + insertCount);
        } else {
            System.out.println("商品" + productId + "未传新图片，保留旧图片");
        }

        // 5. 更新SKU
        if (dto.getSkuList() != null && !dto.getSkuList().isEmpty()) {
            productSkuService.saveBatch(productId, dto.getSkuList());
            System.out.println("商品" + productId + "更新SKU数量：" + dto.getSkuList().size());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductStatus(Long id, Integer status) {
        if (id == null || status == null || (status != 0 && status != 1)) {
            throw new BizException(ResultCode.PARAM_ERROR);
        }
        ProductEntity product = productMapper.selectById(id);
        if (product == null) {
            throw new BizException(ResultCode.PRODUCT_NOT_FOUND);
        }
        product.setStatus(status);
        productMapper.updateById(product);
    }

    // ========== 修复后的deleteProduct（兼容SKU删除） ==========
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long id) {
        if (id == null) {
            throw new BizException(ResultCode.PARAM_ERROR);
        }

        // 1. 新增：先删除该商品的SKU（票种）
        productSkuService.deleteByProductId(id); // 需在AdminProductSkuService中实现该方法
        System.out.println("删除商品" + id + "的SKU完成");

        // 2. 删除商品基础信息（原有逻辑）
        productMapper.deleteById(id);

        // 3. 删除详情（用已有方法，避免冲突）
        productDetailMapper.deleteByProductId(id);

        // 4. 删除图片（用已有方法，避免冲突）
        productImageMapper.deleteByProductId(id);

        System.out.println("商品" + id + "删除完成（含SKU/详情/图片）");
    }
}