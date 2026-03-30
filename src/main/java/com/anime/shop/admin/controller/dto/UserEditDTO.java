package com.anime.shop.admin.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户编辑请求DTO
 */
@Data
public class UserEditDTO {
    /**
     * 被编辑用户ID（必填）
     */
    @NotNull(message = "用户ID不能为空")
    private Long id;

    /**
     * 用户昵称（可选，若传则校验格式）
     */
    @Size(min = 2, max = 20, message = "昵称长度必须在2-20位之间")
    private String nickname;

    /**
     * 身份证号（可选，若传则校验格式）
     */
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$",
            message = "身份证号格式错误")
    private String idCard;

    /**
     * 手机号（可选，若传则校验格式）
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String tel;

    /**
     * 角色（可选，支持 admin/manager/leader/member/consumer，允许降级为普通用户）
     */
    @Pattern(regexp = "^(admin|manager|leader|member|consumer)$", message = "角色只能是admin/manager/leader/member/consumer")
    private String role;

    /**
     * 邮箱（可选，若传则校验格式）
     */
    @Pattern(regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$",
            message = "邮箱格式错误")
    private String email;

    /**
     * 状态（可选，0禁用/1启用）
     */
    @Pattern(regexp = "^[01]$", message = "状态只能是0（禁用）或1（启用）")
    private String status;

    /**
     * 当前操作人角色（必填，用于权限判断）
     */
    @NotBlank(message = "操作人角色不能为空")
    private String operatorRole;
}