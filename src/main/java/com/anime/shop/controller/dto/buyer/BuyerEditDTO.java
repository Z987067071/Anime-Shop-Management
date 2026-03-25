package com.anime.shop.controller.dto.buyer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BuyerEditDTO {
    @NotBlank(message = "购票人姓名不能为空")
    private String name; // 真实姓名

    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$",
            message = "身份证号格式不正确")
    private String idCard; // 身份证号
    private Long userId;
}
