package com.anime.shop.controller.dto.feedback;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class FeedbackAddDTO {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "反馈内容不能为空")
    private String feedbackContent;

    private String creator; // 用户昵称（冗余）
    private List<String> imageUrls; // 图片URL列表（上传后返回的URL）
}
