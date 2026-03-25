package com.anime.shop.controller.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 轮播图实体类（对应b_banner表）
 */
@Data
@TableName("b_banner") // 精准匹配数据库表名
public class BannerEntity {

    /** 轮播图id */
    @TableId(type = IdType.AUTO) // 自增主键，匹配数据库AUTO_INCREMENT
    private Long id;

    /** 标题 */
    private String title;

    /** 图url（数据库img_url → 驼峰imgUrl，MyBatis-Plus自动适配） */
    private String imgUrl;

    /** 跳转链接（数据库link_url → 驼峰linkUrl） */
    private String linkUrl;

    /** 排序值 小靠前 */
    private Integer sort;

    /** 状态 1显示 0隐藏（数据库tinyint → 用Integer避免布尔值转换问题） */
    private Integer status;
}
