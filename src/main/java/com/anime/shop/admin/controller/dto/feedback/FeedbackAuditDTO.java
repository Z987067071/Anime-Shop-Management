package com.anime.shop.admin.controller.dto.feedback;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FeedbackAuditDTO {
    @NotNull(message = "工单ID不能为空")
    private Long id;

    @NotNull(message = "审核状态不能为空")
    private Integer status; // 2=已解决/3=已驳回/4=已关闭

    private String replyContent; // 审核回复

    @NotNull(message = "审核人昵称不能为空")
    private String auditor; // 审核人昵称

    @NotNull(message = "审核人ID不能为空")
    private Long auditorId;
}