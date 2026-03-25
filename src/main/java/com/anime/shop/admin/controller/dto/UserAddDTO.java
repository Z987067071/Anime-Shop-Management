package com.anime.shop.admin.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserAddDTO {

    //  登录账号
    @NotBlank(message = "登录账号不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "账号只能包含字母、数字、下划线，长度4-20位")
    private String username;

    // 密码
    @NotBlank(message = "登录密码不能为空")
    @Pattern(regexp = "^[\\S]{6,20}$", message = "密码长度为6-20位，不能包含空格")
    private String password;

    // 身份role
    @NotBlank(message = "账户角色不能为空")
    @Pattern(regexp = "^(admin|manager|leader|member|consumer)$", message = "角色值非法")
    private String role;

    // 用户名
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9_]{2,20}$", message = "用户名长度2-20位，支持中文、字母、数字、下划线")
    private String nickname;

    // 邮箱
    @Pattern(regexp = "^$|^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", message = "邮箱格式错误")
    private String email;

    // 手机号
    @Pattern(regexp = "^$|^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String tel;

    // 身份证
    @Pattern(regexp = "^$|^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", message = "身份证号格式错误")
    private String idCard;

    // 当前操作人
    @NotBlank(message = "操作人角色不能为空")
    private String operatorRole;
}
