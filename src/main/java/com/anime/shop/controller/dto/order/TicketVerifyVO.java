package com.anime.shop.controller.dto.order;

import lombok.Data;

/**
 * 单张票的核销信息VO（公共类，外部可访问）
 */
@Data
public class TicketVerifyVO {
    private String verifyCode; // 单张票的核销码
    private String verifyQrCodeUrl; // 单张票的二维码
    private Boolean isVerified; // 单张票是否已核销
    private String buyerName;         // 购票人姓名
    private String buyerIdCard;       // 脱敏后的身份证号
}