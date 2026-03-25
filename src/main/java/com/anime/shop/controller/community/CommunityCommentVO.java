package com.anime.shop.controller.community;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommunityCommentVO {
    private Long id;                // 评论ID
    private Long postId;            // 帖子ID
    private Long userId;            // 评论用户ID
    private Long parentId;          // 父评论ID
    private String content;         // 评论内容
    private List<String> imageUrls; // 图片URL列表
    private Integer likeCount;      // 点赞数
    private Boolean isLiked;        // 当前用户是否点赞
    private String userName;        // 用户名
    private String userAvatar;      // 用户头像
    private LocalDateTime createTime; // 创建时间
    private List<CommunityCommentVO> replyList; // 回复列表
}