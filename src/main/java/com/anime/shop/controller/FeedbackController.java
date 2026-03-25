package com.anime.shop.controller;

import com.anime.shop.admin.utils.FileUploadUtil;
import com.anime.shop.common.BizException;
import com.anime.shop.common.Result;
import com.anime.shop.common.ResultCode;
import com.anime.shop.controller.dto.feedback.*;
import com.anime.shop.service.FeedbackService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mobile/feedback")
public class FeedbackController {

    @Resource
    private FeedbackService feedbackService;
    @Resource
    private FileUploadUtil fileUploadUtil;

    /**
     * 添加工单回复
     */
    @PostMapping("/reply")
    public Result<Void> addFeedbackReply(@Valid @RequestBody FeedbackReplyDTO dto, BindingResult bindingResult) {
        // 参数校验
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("；"));
            return Result.error(ResultCode.PARAM_ERROR.getCode(), errorMsg);
        }

        try {
            feedbackService.addFeedbackReply(dto);
            return Result.success(null);
        } catch (BizException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(ResultCode.SERVER_ERROR.getCode(), "回复提交失败");
        }
    }

    /**
     * 查询工单回复列表
     */
    @GetMapping("/reply/list")
    public Result<List<FeedbackReplyVO>> getFeedbackReplyList(@RequestParam @NotNull Long feedbackId) {
        try {
            List<FeedbackReplyVO> replyList = feedbackService.getFeedbackReplyList(feedbackId);
            return Result.success(replyList);
        } catch (BizException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(ResultCode.SERVER_ERROR.getCode(), "查询回复列表失败");
        }
    }

    /**
     * 上传反馈图片（单独接口，先传图再提交工单）
     */
    @PostMapping("/uploadImage")
    public Result<List<String>> uploadImage(@RequestParam("files") List<MultipartFile> files) {
        if (CollectionUtils.isEmpty(files)) {
            throw new BizException(ResultCode.PARAM_ERROR, "请选择要上传的图片");
        }
        try {
            List<String> imageUrls = new ArrayList<>();
            // 替换Stream为普通循环，避免lambda中未捕获IOException
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String imageUrl = fileUploadUtil.uploadFeedbackImage(file);
                    imageUrls.add(imageUrl);
                }
            }
            return Result.success(imageUrls);
        } catch (IllegalArgumentException e) {
            throw new BizException(ResultCode.PARAM_ERROR, e.getMessage());
        } catch (IOException e) {
            throw new BizException(ResultCode.SERVER_ERROR, "图片上传失败：" + e.getMessage());
        } catch (Exception e) {
            throw new BizException(ResultCode.SERVER_ERROR, "图片上传异常：" + e.getMessage());
        }
    }

    /**
     * 提交工单反馈
     */
    @PostMapping("/add")
    public Result<Void> addFeedback(@Valid @RequestBody FeedbackAddDTO dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("；"));
            return Result.build(ResultCode.PARAM_ERROR.getCode(), errorMsg, null);
        }

        try {
            feedbackService.addFeedback(dto);
            return Result.ok(null);
        } catch (BizException e) {
            return Result.build(e.getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return Result.build(ResultCode.SERVER_ERROR.getCode(), "提交反馈失败", null);
        }
    }

    /**
     * 查询用户工单列表
     */
    @GetMapping("/list")
    public Result<IPage<FeedbackListVO>> getFeedbackList(
            @RequestParam @NotNull(message = "用户ID不能为空") Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        try {
            IPage<FeedbackListVO> pageResult = feedbackService.getUserFeedbackList(userId, status, page, size);
            return Result.success(pageResult); // 统一用 Result.success 而非 Result.ok
        } catch (BizException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            // 打印异常堆栈（关键！便于排查）
            e.printStackTrace();
            return Result.error(ResultCode.SERVER_ERROR.getCode(), "查询工单列表失败：" + e.getMessage());
        }
    }

    /**
     * 查询工单详情
     */
    @GetMapping("/detail/{id}")
    public Result<FeedbackDetailVO> getFeedbackDetail(
            @PathVariable Long id,
            @RequestParam @NotNull Long userId
    ) {
        try {
            FeedbackDetailVO detail = feedbackService.getFeedbackDetail(id, userId);
            return Result.ok(detail);
        } catch (BizException e) {
            return Result.build(e.getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return Result.build(ResultCode.SERVER_ERROR.getCode(), "查询工单详情失败", null);
        }
    }
}