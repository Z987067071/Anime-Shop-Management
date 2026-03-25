package com.anime.shop.controller.dto.mine;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 移动端修改用户名DTO
 */
@Data
public class MobileNicknameEditDTO {
    /**
     * 用户ID（当前登录用户ID）
     */
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    /**
     * 新用户名
     * 规则：4-20位，仅包含字母、数字、下划线，不能以数字开头
     */
    @NotBlank(message = "昵称不能为空")
    @Size(min = 1, max = 20, message = "昵称长度需在1-20位之间")
    @Pattern(regexp = "^[\u4e00-\u9fa5a-zA-Z0-9]+$", message = "昵称仅支持中文、字母、数字")
    private String newNickname;
}
