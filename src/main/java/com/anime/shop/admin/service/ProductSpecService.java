package com.anime.shop.admin.service;

public interface ProductSpecService {
    /**
     * 根据规格值查询spec_id（不存在则自动创建）
     */
    Long getOrCreateSpecId(String specValue);
}