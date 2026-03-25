package com.anime.shop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("p_report")
public class ProductReportEntity {
    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 举报目标类型：1=商品，2=商品评论，3=社区帖子，4=社区帖子评论 */
    private Integer targetType;

    /** 举报目标ID（商品ID/评论ID/帖子ID/帖子评论ID） */
    private Long targetId;

    /** 举报用户ID */
    private Long userId;

    /** 举报理由枚举：1=违反法律规定,2=色情低俗...99=其他 */
    private Integer reportReason;

    /** 自定义举报理由（仅report_reason=99时填写） */
    private String customReason;

    /** 举报处理状态：0=待处理,1=已受理,2=已驳回,3=已处理 */
    private Integer status;

    /** 处理人ID（管理员ID） */
    private Long handlerId;

    /** 处理时间 */
    private LocalDateTime handleTime;

    /** 处理备注（管理员填写） */
    private String handleNote;

    /** 举报时间（自动填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间（自动填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}