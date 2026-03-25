package com.anime.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("p_comment_like")
public class CommentLike {
    @TableId(type = IdType.AUTO)
    private Long id;                // 主键ID
    private Long commentId;         // 评论ID
    private Long userId;            // 点赞用户ID
    private LocalDateTime createTime; // 点赞时间
}
