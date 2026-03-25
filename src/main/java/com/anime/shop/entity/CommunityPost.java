package com.anime.shop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("p_community_post") // 严格匹配你的表名
public class CommunityPost {
    @TableId(type = IdType.AUTO)
    private Long id; // 主键ID

    @TableField("user_id")
    private Long userId; // 发帖用户ID

    @TableField("title")
    private String title; // 帖子标题

    @TableField("content")
    private String content; // 帖子内容

    @TableField("image_urls")
    private String imageUrls; // 帖子图片（逗号分隔）

    @TableField("like_count")
    private Integer likeCount; // 点赞数

    @TableField("comment_count")
    private Integer commentCount; // 评论数

    @TableField("view_count")
    private Integer viewCount; // 浏览数

    @TableField("status")
    private Integer status; // 状态：0=删除,1=正常,2=审核中,3=封禁

    @TableField("create_time")
    private LocalDateTime createTime; // 创建时间

    @TableField("update_time")
    private LocalDateTime updateTime; // 更新时间

    @TableField("user_avatar")
    private String userAvatar;

    @TableField("user_name")
    private String userName;
}