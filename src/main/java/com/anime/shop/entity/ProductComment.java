package com.anime.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("p_product_comment")
public class ProductComment {
    @TableId(type = IdType.AUTO)
    private Long id;                // 主键ID
    private Long productId;         // 商品ID
    private Long userId;            // 评论用户ID
    private Long parentId = 0L;     // 父评论ID（默认0）
    private String content;         // 评论内容
    private String imageUrls = "";  // 评论图片URL（逗号分隔）
    private Integer likeCount = 0;  // 评论点赞数
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
    private Integer isDelete = 0;   // 是否删除（0=否，1=是）
}
