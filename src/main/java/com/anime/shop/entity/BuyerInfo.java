package com.anime.shop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("buyer_info")
public class BuyerInfo {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;          // 所属用户ID
    private String name;          // 购票人姓名
    private String idCard;        // 身份证号
    private Integer auditStatus;  // 审核状态：0-待审核 1-通过 2-驳回
    private String auditRemark;   // 驳回理由
    private LocalDateTime auditTime; // 审核时间

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
