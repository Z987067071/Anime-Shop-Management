package com.anime.shop.admin.controller.dto.feedback;

import lombok.Data;

@Data
public class FeedbackPageDTO {
    private Integer page = 1;
    private Integer size = 10;
    private Integer status; // 工单状态
    private Long userId; // 筛选用户ID
    private String keyword; // 反馈内容关键词
    private String startTime;
    private String endTime;
}
