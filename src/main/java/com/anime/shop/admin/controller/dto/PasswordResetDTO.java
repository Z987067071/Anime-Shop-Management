package com.anime.shop.admin.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordResetDTO {
    /**
     * 被重置用户的ID（必填）
     */
    @NotNull(message = "用户ID不能为空")
    private Long id;

    /**
     * 新密码（必填，6-20位）
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    private String newPassword;

    /**
     * 操作人角色（必须是admin/manager，用于权限校验）
     */
    @NotBlank(message = "操作人角色不能为空")
    private String operatorRole;
}
