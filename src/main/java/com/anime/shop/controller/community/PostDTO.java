package com.anime.shop.controller.community;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDTO {
    private Long id; // 主键ID（发布时无需传）
    private Long userId; // 发帖用户ID（必填）
    private String title; // 帖子标题（必填）
    private String content; // 帖子内容（必填）
    private String imageUrls; // 数据库存储的逗号分隔字符串（查询用）
    private List<String> imageUrlsList; // 前端传的图片URL列表（发布用）
    private Integer likeCount; // 点赞数（发布时无需传）
    private Integer commentCount; // 评论数（发布时无需传）
    private Integer viewCount; // 浏览数（发布时无需传）
    private Integer status; // 状态（发布时默认1）
    private String userAvatar;
    private String userName;
    private LocalDateTime createTime; // 创建时间（数据库自动生成）
    private LocalDateTime updateTime; // 更新时间（数据库自动生成）
}