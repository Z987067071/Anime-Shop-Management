package com.anime.shop.admin.service.impl;

import com.anime.shop.admin.mapper.product.ProductSpecMapper;
import com.anime.shop.admin.service.ProductSpecService;
import com.anime.shop.entity.ProductSpecEntity;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ProductSpecServiceImpl implements ProductSpecService {

    @Resource
    private ProductSpecMapper productSpecMapper;

    @Override
    public Long getOrCreateSpecId(String specValue) {
        // 1. 先查询是否已有该规格值
        Long specId = productSpecMapper.selectSpecIdByValue(specValue);
        if (specId != null) {
            return specId; // 已有则直接返回ID
        }

        // 2. 不存在则自动创建（spec_name固定为“票种”）
        ProductSpecEntity spec = new ProductSpecEntity();
        spec.setSpecName("票种"); // 固定值
        spec.setSpecValue(specValue); // 票种名称（如“周六VIP票”）
        productSpecMapper.insert(spec);

        return spec.getId(); // 返回新创建的spec_id
    }
}
