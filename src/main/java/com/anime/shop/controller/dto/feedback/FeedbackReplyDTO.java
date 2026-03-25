package com.anime.shop.controller.dto.feedback;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FeedbackReplyDTO {
    /** 工单ID（必填） */
    @NotNull(message = "工单ID不能为空")
    private Long feedbackId;

    /** 回复人ID（用户ID/管理员ID，必填） */
    @NotNull(message = "回复人ID不能为空")
    private Long userId;

    /** 回复内容（必填） */
    @NotBlank(message = "回复内容不能为空")
    private String content;

    /** 是否管理员：0-用户 1-管理员（必填） */
    @NotNull(message = "回复类型不能为空")
    private Integer isAdmin;

    /** 回复人昵称（必填） */
    @NotBlank(message = "回复人昵称不能为空")
    private String replyer;
}
