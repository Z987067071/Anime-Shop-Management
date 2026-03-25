package com.anime.shop.admin.service;

import com.anime.shop.admin.mapper.product.ProductSkuMapper;
import com.anime.shop.entity.ProductSkuEntity;

import java.util.List;

public interface AdminProductSkuService {
    /**
     * 根据商品ID查询票种SKU列表
     */
    List<ProductSkuEntity> listByProductId(Long productId);

    /**
     * 批量保存商品SKU（管理端配置票种用）
     */
    void saveBatch(Long productId, List<ProductSkuEntity> skuList);

    void deleteByProductId(Long productId);

    ProductSkuMapper getBaseMapper();
}
