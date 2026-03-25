package com.anime.shop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ticket_verify_log")
public class TicketVerifyLogEntity {
    @TableId(type = IdType.AUTO)
    private Long id;                     // 主键ID

    private Long orderId;                // 关联订单ID
    private Long ticketRelationId;       // 关联order_ticket_relation.id
    private String verifyCode;           // 核销码
    private String verifyStaff;          // 核销员
    private String verifyIp;             // 核销IP地址
    private Integer verifyResult;        // 核销结果 0=失败 1=成功
    private String failReason;           // 失败原因（如：已核销、核销码不存在）

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;             // 核销时间
}
