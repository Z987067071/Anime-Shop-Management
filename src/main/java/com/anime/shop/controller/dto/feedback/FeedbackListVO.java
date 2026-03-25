package com.anime.shop.controller.dto.feedback;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FeedbackListVO {
    private Long id; // 工单ID
    private String feedbackContent; // 反馈内容
    private Integer status; // 工单状态
    private String statusDesc; // 状态描述（0=待审核/1=审核中/2=已解决/3=已驳回/4=已关闭）
    private LocalDateTime createTime; // 创建时间
    private List<String> imageUrls; // 图片URL列表
}
