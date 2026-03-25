package com.anime.shop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商品收藏表实体类
 */
@Data
@TableName("p_product_collect") // 对应数据库表名
public class ProductCollectEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（关联用户表）
     */
    private Long userId;

    /**
     * 商品ID（关联p_product表）
     */
    private Long productId;

    /**
     * 收藏时间
     */
    @TableField(fill = FieldFill.INSERT) // 插入时自动填充
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入/更新时自动填充
    private LocalDateTime updateTime;

    /**
     * 是否删除（0=未删，1=已删）
     */
    private Integer isDelete = 0;
}