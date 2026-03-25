package com.anime.shop.controller.dto.order;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserComicConTicketVO {
    // 订单基础信息
    private Long orderId;          // 订单ID
    private String orderNo;        // 订单编号
    private LocalDateTime createTime; // 下单时间
    private LocalDateTime payTime;    // 支付时间
    private Integer orderStatus;   // 订单状态（0=未支付，1=待核销，3=已完成，4=已取消）
    private String orderStatusName; // 订单状态名称（前端展示用）

    // 票务核心信息
    private String comicConName;   // 漫展名称（如：上海·2026CP32）
    private String ticketType;     // 票种（如：早鸟票/VIP票）
    private BigDecimal ticketPrice; // 票价
    private String verifyCode;     // 核销码（后续生成二维码用）
    private Boolean isVerified;    // 是否已核销（true/false）

    private String comicConBanner; // 漫展封面图URL（前端显示票的图片）
    private String verifyQrCodeUrl; // 核销二维码Base64（微信可扫码）

    private Integer ticketCount; // 购票数量（如：2）
    private List<TicketVerifyVO> ticketVerifyList; // 每张票的核销码+二维码
}
