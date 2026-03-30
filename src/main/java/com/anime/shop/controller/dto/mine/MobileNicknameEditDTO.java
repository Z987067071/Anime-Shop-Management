package com.anime.shop.controller.dto.mine;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MobileNicknameEditDTO {

    @NotBlank(message = "昵称不能为空")
    @Size(min = 1, max = 20, message = "昵称长度需在1-20位之间")
    @Pattern(regexp = "^[\u4e00-\u9fa5a-zA-Z0-9]+$", message = "昵称仅支持中文、字母、数字")
    private String newNickname;
}
