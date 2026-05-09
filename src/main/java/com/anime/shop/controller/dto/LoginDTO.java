package com.anime.shop.controller.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String username;
    private String password;
    private String captcha;
    private String platform; // 平台标识：admin-管理端，mobile-移动端
}