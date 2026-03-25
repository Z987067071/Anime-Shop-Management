package com.anime.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 社区帖子点赞实体类（适配你的表名 p_community_post_like）
 */
@Data
@TableName("p_community_post_like")
public class PostLike {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 帖子ID（关联p_community_post.id）
     */
    private Long postId;

    /**
     * 点赞用户ID
     */
    private Long userId;

    /**
     * 是否取消点赞：0=未取消，1=已取消
     */
    private Integer isCancel;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}