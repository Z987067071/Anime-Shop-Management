package com.anime.shop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商品分类
 */
@Data
@TableName("p_category")
public class ProductCategoryEntity {

    /**
     * 分类ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 父分类ID：0为一级分类
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类层级：1/2/3
     */
    private Integer categoryLevel;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 分类图标URL
     */
    private String icon;

    /**
     * 状态：1=启用，0=禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT) // 新增时自动填充
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE) // 新增/修改时自动填充
    private LocalDateTime updateTime;
}
