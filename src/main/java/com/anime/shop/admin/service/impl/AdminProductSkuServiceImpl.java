package com.anime.shop.admin.service.impl;

import com.anime.shop.admin.mapper.product.ProductSkuMapper;
import com.anime.shop.admin.service.AdminProductSkuService;
import com.anime.shop.entity.ProductSkuEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminProductSkuServiceImpl implements AdminProductSkuService {

    @Resource // 或@Autowired，注入Mapper
    private ProductSkuMapper productSkuMapper;

    @Override
    public List<ProductSkuEntity> listByProductId(Long productId) {
        // 实现查询逻辑（之前提供的代码）
        return productSkuMapper.selectByProductId(productId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(Long productId, List<ProductSkuEntity> skuList) {
        // 先删除原有SKU
        LambdaQueryWrapper<ProductSkuEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductSkuEntity::getProductId, productId);
        productSkuMapper.delete(wrapper);

        // 批量新增
        if (skuList != null && !skuList.isEmpty()) {
            skuList.forEach(sku -> {
                sku.setProductId(productId);
                productSkuMapper.insert(sku);
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByProductId(Long productId) {
        LambdaQueryWrapper<ProductSkuEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductSkuEntity::getProductId, productId);
        productSkuMapper.delete(wrapper);
    }

    @Override
    public ProductSkuMapper getBaseMapper() {
        return this.productSkuMapper;
    }
}
