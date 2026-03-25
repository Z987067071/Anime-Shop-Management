package com.anime.shop.service;

import com.anime.shop.controller.dto.order.LikeResultVO;
import com.anime.shop.entity.ProductLike;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ProductLikeService extends IService<ProductLike> {

    /**
     * 商品点赞/取消点赞
     * @param productId 商品ID
     * @param userId 用户ID
     * @param isLike true=点赞，false=取消点赞
     * @return 操作结果（包含当前点赞状态和点赞数）
     */
    LikeResultVO likeProduct(Long productId, Long userId, Boolean isLike);

    /**
     * 查询商品点赞总数
     * @param productId 商品ID
     * @return 点赞数
     */
    int getProductLikeCount(Long productId);

    /**
     * 查询用户是否已点赞该商品
     * @param productId 商品ID
     * @param userId 用户ID
     * @return 是否点赞
     */
    boolean isUserLiked(Long productId, Long userId);
}
