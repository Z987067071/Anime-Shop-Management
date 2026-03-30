package com.anime.shop.controller;

import com.anime.shop.admin.utils.FileUploadUtil;
import com.anime.shop.common.Result;
import com.anime.shop.controller.community.CommunityCommentAddDTO;
import com.anime.shop.controller.community.CommunityCommentVO;
import com.anime.shop.service.CommunityCommentService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 社区评论控制器
 */
@RestController
@RequestMapping("/api/mobile/community/comment")
@RequiredArgsConstructor
@Validated
public class CommunityCommentController {

    private final CommunityCommentService commentService;
    private final FileUploadUtil fileUploadUtil;

    /** 分页获取帖子评论（可选登录） */
    @GetMapping("/list")
    public Result<?> getCommentList(
            @RequestParam @NotNull Long postId,
            @RequestParam(defaultValue = "0") Long parentId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestAttribute(required = false) Long userId) {
        Page<CommunityCommentVO> page = commentService.getCommentList(postId, pageNum, pageSize, userId, parentId);
        return Result.success(page);
    }

    /** 发布评论/回复（需登录） */
    @PostMapping("/add")
    public Result<?> addComment(
            @Valid @RequestBody CommunityCommentAddDTO dto,
            @RequestAttribute Long userId) {
        Long commentId = commentService.addComment(dto, userId);
        return Result.success(commentId);
    }

    /** 删除评论（需登录） */
    @PostMapping("/delete/{commentId}")
    public Result<?> deleteComment(
            @PathVariable Long commentId,
            @RequestAttribute Long userId,
            @RequestAttribute String role) {
        commentService.deleteComment(commentId, userId, role);
        return Result.success("删除成功");
    }

    /** 评论点赞（需登录） */
    @PostMapping("/like/{commentId}")
    public Result<?> toggleLike(
            @PathVariable Long commentId,
            @RequestAttribute Long userId) {
        return commentService.toggleLike(commentId, userId);
    }

    /** 上传社区评论图片（需登录） */
    @PostMapping("/upload/image")
    public Result<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestAttribute Long userId) throws java.io.IOException {
        return Result.success(fileUploadUtil.uploadCommunityPostCommentImage(file));
    }
}
