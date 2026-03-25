package com.anime.shop.service;

import com.anime.shop.admin.controller.dto.feedback.FeedbackAuditDTO;
import com.anime.shop.admin.controller.dto.feedback.FeedbackPageDTO;
import com.anime.shop.controller.dto.feedback.*;
import com.anime.shop.entity.FeedbackEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface FeedbackService {
    /**
     * 移动端提交反馈
     */
    void addFeedback(FeedbackAddDTO dto);

    /**
     * 用户查询工单列表
     */
    IPage<FeedbackListVO> getUserFeedbackList(Long userId, Integer status, Integer page, Integer size);

    /**
     * 移动端查询工单详情（需2个参数：工单ID+用户ID）
     */
    FeedbackDetailVO getFeedbackDetail(Long id, Long userId);

    /**
     * 新增：Admin端查询工单详情（仅需1个参数：工单ID）
     */
    FeedbackDetailVO getAdminFeedbackDetail(Long id);

    /**
     * 后台分页查询工单
     */
    IPage<FeedbackEntity> getAdminFeedbackPage(FeedbackPageDTO dto);

    /**
     * 后台审核工单
     */
    void auditFeedback(FeedbackAuditDTO dto);

    /**
     * 添加工单回复（原方法，Controller中误写为replyFeedback）
     */
    void addFeedbackReply(FeedbackReplyDTO dto);

    /**
     * 根据工单ID查询回复列表
     */
    List<FeedbackReplyVO> getFeedbackReplyList(Long feedbackId);
}