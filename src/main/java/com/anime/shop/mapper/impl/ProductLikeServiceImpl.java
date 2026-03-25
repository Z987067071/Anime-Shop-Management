package com.anime.shop.mapper.impl;

import com.anime.shop.controller.dto.order.LikeResultVO;
import com.anime.shop.entity.ProductLike;
import com.anime.shop.mapper.ProductLikeMapper;
import com.anime.shop.service.ProductLikeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductLikeServiceImpl extends ServiceImpl<ProductLikeMapper, ProductLike>
        implements ProductLikeService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LikeResultVO likeProduct(Long productId, Long userId, Boolean isLike) {
        // 参数校验
        if (productId == null || userId == null || isLike == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        LikeResultVO result = new LikeResultVO();
        boolean hasLiked = isUserLiked(productId, userId);

        if (isLike) {
            // 点赞操作：未点赞则新增
            if (!hasLiked) {
                ProductLike productLike = new ProductLike();
                productLike.setProductId(productId);
                productLike.setUserId(userId);
                this.save(productLike);
                result.setHasLiked(true);
            } else {
                result.setHasLiked(true); // 已点赞则返回当前状态
            }
        } else {
            if (hasLiked) {
                this.remove(
                        new LambdaQueryWrapper<ProductLike>()
                                .eq(ProductLike::getProductId, productId)
                                .eq(ProductLike::getUserId, userId)
                );
                result.setHasLiked(false);
            }
        }

        // 查询最新点赞数
        int likeCount = getProductLikeCount(productId);
        result.setLikeCount(likeCount);
        return result;
    }

    @Override
    public int getProductLikeCount(Long productId) {
        return baseMapper.countProductLike(productId);
    }

    @Override
    public boolean isUserLiked(Long productId, Long userId) {
        return baseMapper.checkUserLike(productId, userId) > 0;
    }
}
