package com.anime.shop.controller.dto.productdetail;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductCommentVO {
    private Long id;                // 评论ID
    private Long productId;         // 商品ID
    private Long userId;            // 评论用户ID
    private String userName;        // 评论用户昵称（联用户表）
    private String userAvatar;      // 评论用户头像（联用户表）
    private Long parentId;          // 父评论ID
    private String content;         // 评论内容
    private List<String> imageUrls; // 图片URL列表（拆分逗号）
    private Integer likeCount;      // 点赞数
    private Boolean isLiked;        // 当前用户是否点赞
    private LocalDateTime createTime; // 创建时间
    private List<ProductCommentVO> replyList; // 回复列表（嵌套）
}
