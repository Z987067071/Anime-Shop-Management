package com.anime.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("f_feedback_image")
public class FeedbackImageEntity {
    /** 图片ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联工单ID */
    private Long feedbackId;

    /** 图片URL */
    private String imageUrl;

    /** 图片排序 */
    private Integer sort;

    /** 创建时间 */
    private LocalDateTime createTime;
}
