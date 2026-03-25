package com.anime.shop.admin.controller.dto.ticket;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TicketVerifyDTO {
    @NotBlank(message = "核销码不能为空")
    private String verifyCode;           // 核销码

    @NotBlank(message = "核销员不能为空")
    private String verifyStaff;          // 核销员姓名/账号

    private String verifyIp;             // 核销IP（前端/网关传）
}
