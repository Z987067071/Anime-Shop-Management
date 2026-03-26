package com.anime.shop.admin.controller;

import com.anime.shop.admin.controller.dto.feedback.FeedbackAuditDTO;
import com.anime.shop.admin.controller.dto.feedback.FeedbackPageDTO;
import com.anime.shop.common.BizException;
import com.anime.shop.common.Result;
import com.anime.shop.common.ResultCode;
import com.anime.shop.controller.dto.feedback.FeedbackDetailVO;
import com.anime.shop.controller.dto.feedback.FeedbackReplyDTO;
import com.anime.shop.controller.dto.feedback.FeedbackReplyVO;
import com.anime.shop.entity.FeedbackEntity;
import com.anime.shop.service.FeedbackService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/feedback")
public class AdminFeedbackController {

    @Resource
    private FeedbackService feedbackService;

    /**
     * 后台分页查询工单
     */
    @PostMapping("/page")
    public Result<IPage<FeedbackEntity>> getFeedbackPage(@RequestBody FeedbackPageDTO dto) {
        try {
            IPage<FeedbackEntity> pageResult = feedbackService.getAdminFeedbackPage(dto);
            return Result.ok(pageResult);
        } catch (Exception e) {
            return Result.build(ResultCode.SERVER_ERROR.getCode(), "查询工单失败", null);
        }
    }

    /**
     * 后台审核工单
     */
    @PostMapping("/audit")
    public Result<Void> auditFeedback(@Valid @RequestBody FeedbackAuditDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("；"));
            return Result.build(ResultCode.PARAM_ERROR.getCode(), errorMsg, null);
        }

        try {
            feedbackService.auditFeedback(dto);
            return Result.ok(null);
        } catch (BizException e) {
            return Result.build(e.getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return Result.build(ResultCode.SERVER_ERROR.getCode(), "审核工单失败", null);
        }
    }

    /**
     * 更改工单状态（快捷操作）
     */
    @PostMapping("/updateStatus/{id}")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam @NotNull Integer status
    ) {
        try {
            FeedbackAuditDTO dto = new FeedbackAuditDTO();
            dto.setId(id);
            dto.setStatus(status);
            feedbackService.auditFeedback(dto);
            return Result.ok(null);
        } catch (BizException e) {
            return Result.build(e.getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return Result.build(ResultCode.SERVER_ERROR.getCode(), "修改状态失败", null);
        }
    }

    /**
     * 获取工单详情（含回复记录）
     */
    @GetMapping("/detail/{id}")
    public Result<FeedbackDetailVO> getFeedbackDetail(@PathVariable Long id) {
        try {
            FeedbackDetailVO detailVO = feedbackService.getAdminFeedbackDetail(id);
            return Result.ok(detailVO);
        } catch (BizException e) {
            return Result.build(e.getCode(), e.getMessage(), null);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.build(ResultCode.SERVER_ERROR.getCode(), "获取工单详情失败", null);
        }
    }

    /**
     * 回复工单（Admin端固定isAdmin=1）
     */
    @PostMapping("/reply")
    public Result<Void> replyFeedback(@Valid @RequestBody FeedbackReplyDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("；"));
            return Result.build(ResultCode.PARAM_ERROR.getCode(), errorMsg, null);
        }

        dto.setIsAdmin(1);

        try {
            feedbackService.addFeedbackReply(dto);
            return Result.ok(null);
        } catch (BizException e) {
            return Result.build(e.getCode(), e.getMessage(), null);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.build(ResultCode.SERVER_ERROR.getCode(), "回复工单失败", null);
        }
    }
    /**
     * 获取工单回复列表
     */
    @GetMapping("/reply/list/{feedbackId}")
    public Result<List<FeedbackReplyVO>> getFeedbackReplyList(@PathVariable Long feedbackId) {
        try {
            List<FeedbackReplyVO> replyList = feedbackService.getFeedbackReplyList(feedbackId);
            return Result.ok(replyList);
        } catch (BizException e) {
            return Result.build(e.getCode(), e.getMessage(), null);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.build(ResultCode.SERVER_ERROR.getCode(), "获取回复列表失败", null);
        }
    }
}
