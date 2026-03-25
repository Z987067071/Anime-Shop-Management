package com.anime.shop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("p_community_comment")
public class CommunityComment {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 帖子ID
     */
    private Long postId;

    /**
     * 评论用户ID
     */
    private Long userId;

    /**
     * 父评论ID（0为根评论）
     */
    private Long parentId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论图片（仅根评论可传）JSON格式
     */
    private String imageUrls;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    private String userName;
    private String userAvatar;
}