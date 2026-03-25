package com.anime.shop.controller.dto.feedback;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FeedbackDetailVO {
    private Long id;
    private String feedbackContent;
    private Integer status;
    private String statusDesc;
    private String replyContent; // 后台回复
    private LocalDateTime createTime;
    private LocalDateTime auditTime;
    private List<String> imageUrls;
}