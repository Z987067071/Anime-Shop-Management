package com.anime.shop.service;

import com.anime.shop.controller.dto.productdetail.ProductCommentAddDTO;
import com.anime.shop.controller.dto.productdetail.ProductCommentVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface ProductCommentService {
    // 新增评论（含根评论/回复）
    Long addComment(ProductCommentAddDTO dto, Long userId);
    // 获取商品评论列表（根评论+嵌套回复）
    Page<ProductCommentVO> getCommentList(Long productId, Integer pageNum, Integer pageSize, Long currentUserId);
    // 评论点赞/取消点赞
    void toggleLike(Long commentId, Long userId);
    // 删除评论（仅本人/管理员）
    void deleteComment(Long commentId, Long userId, String role);

    Integer getCommentCount(Long productId);
}
