package com.anime.shop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("order_ticket_relation")
public class OrderTicketRelationEntity {
    @TableId(type = IdType.AUTO)
    private Long id;                     // 主键ID

    private Long orderId;                // 关联p_order.id
    private Long orderItemId;            // 关联p_order_item.id
    private Long userId;                 // 购票用户ID
    private Long comicConTicketId;       // 关联漫展票种ID（comic_con_ticket.id）

    // 实名信息
    private String realName;             // 购票人真实姓名
    private String idCard;               // 购票人身份证号

    // 核销相关
    private String verifyCode;           // 唯一核销码（8位随机字符串）
    private String qrCode;               // 核销二维码（存储二维码地址/Base64）
    private Integer verifyStatus;        // 核销状态 0=未核销 1=已核销 2=已退票

    private LocalDateTime verifyTime;             // 核销时间
    private String verifyStaff;          // 核销员账号/姓名

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;             // 创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;             // 更新时间
}
