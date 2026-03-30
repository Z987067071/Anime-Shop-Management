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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/mobile/feedback")
public class FeedbackController {

    @Resource
    private FeedbackService feedbackService;
    @Resource
    private FileUploadUtil fileUploadUtil;

    /** 添加工单回复 */
    @PostMapping("/reply")
    public Result<Void> addFeedbackReply(@Valid @RequestBody FeedbackReplyDTO dto) {
        feedbackService.addFeedbackReply(dto);
        return Result.success();
    }

    /** 查询工单回复列表 */
    @GetMapping("/reply/list")
    public Result<List<FeedbackReplyVO>> getFeedbackReplyList(@RequestParam @NotNull Long feedbackId) {
        return Result.success(feedbackService.getFeedbackReplyList(feedbackId));
    }

    /** 上传反馈图片 */
    @PostMapping("/uploadImage")
    public Result<List<String>> uploadImage(@RequestParam("files") List<MultipartFile> files) throws IOException {
        if (CollectionUtils.isEmpty(files)) {
            throw new BizException(ResultCode.PARAM_ERROR, "请选择要上传的图片");
        }
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                imageUrls.add(fileUploadUtil.uploadFeedbackImage(file));
            }
        }
        return Result.success(imageUrls);
    }

    /** 提交工单反馈 */
    @PostMapping("/add")
    public Result<Void> addFeedback(@Valid @RequestBody FeedbackAddDTO dto) {
        feedbackService.addFeedback(dto);
        return Result.success();
    }

    /** 查询用户工单列表 */
    @GetMapping("/list")
    public Result<IPage<FeedbackListVO>> getFeedbackList(
            @RequestParam @NotNull(message = "用户ID不能为空") Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(feedbackService.getUserFeedbackList(userId, status, page, size));
    }

    /** 查询工单详情 */
    @GetMapping("/detail/{id}")
    public Result<FeedbackDetailVO> getFeedbackDetail(
            @PathVariable Long id,
            @RequestParam @NotNull Long userId) {
        return Result.success(feedbackService.getFeedbackDetail(id, userId));
    }
}
