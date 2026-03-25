package com.anime.shop.controller.dto.feedback;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeedbackReplyVO {
    /** 回复ID */
    private Long id;

    /** 工单ID */
    private Long feedbackId;

    /** 回复内容 */
    private String content;

    /** 是否管理员回复：0-用户 1-管理员 */
    private Integer isAdmin;

    /** 回复人昵称 */
    private String replyer;

    /** 回复时间 */
    private LocalDateTime createTime;
}