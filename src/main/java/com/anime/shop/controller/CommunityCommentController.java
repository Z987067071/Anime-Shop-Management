package com.anime.shop.controller;

import com.anime.shop.admin.utils.FileUploadUtil;
import com.anime.shop.common.Result;
import com.anime.shop.controller.community.CommunityCommentAddDTO;
import com.anime.shop.controller.community.CommunityCommentVO;
import com.anime.shop.mapper.UserMapper;
import com.anime.shop.service.CommunityCommentService;
import com.anime.shop.util.JwtUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 社区评论控制器（独立目录，避免和商品评论混淆）
 */
@RestController
@RequestMapping("/api/mobile/community/comment")
@RequiredArgsConstructor
@Validated
public class CommunityCommentController {

    private final CommunityCommentService commentService;
    private final JwtUtil jwtUtil;
    private final FileUploadUtil fileUploadUtil;
    private final UserMapper UserMapper;

    /**
     * 分页获取帖子评论
     */

    @GetMapping("/list")
    public Result<?> getCommentList(
            @RequestParam @NotNull Long postId,
            @RequestParam(defaultValue = "0") Long parentId, // 新增：接收parentId参数
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestHeader(value = "token", required = false) String token) {
        Long currentUserId = null;
        if (token != null && jwtUtil.validateToken(token)) {
            currentUserId = jwtUtil.getUserIdFromToken(token);
        }
        // 关键：把parentId传给Service
        Page<CommunityCommentVO> page = commentService.getCommentList(postId, pageNum, pageSize, currentUserId, parentId);
        return Result.success(page);
    }

    /**
     * 发布评论/回复
     */

    @PostMapping("/add")
    public Result<?> addComment(
            @Valid @RequestBody CommunityCommentAddDTO dto,
            @RequestHeader("token") String token) {
        if (!jwtUtil.validateToken(token)) {
            return Result.error(401, "Token无效");
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        try {
            Long commentId = commentService.addComment(dto, userId);
            return Result.success(commentId);
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 删除评论
     */
    @PostMapping("/delete/{commentId}")
    public Result<?> deleteComment(
            @PathVariable Long commentId,
            @RequestHeader("token") String token) {
        if (!jwtUtil.validateToken(token)) {
            return Result.error(401, "Token无效");
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        try {
            commentService.deleteComment(commentId, userId, role);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/like/{commentId}")
    public Result<?> toggleLike(
            @PathVariable Long commentId,
            @RequestHeader("token") String token) {
        if (!jwtUtil.validateToken(token)) {
            return Result.error(401, "Token无效");
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        try {
            return commentService.toggleLike(commentId, userId);
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }



    @PostMapping("/upload/image")
    public Result<?> uploadCommunityPostCommentImage(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("token") String token) {
        // 1. 校验登录状态（仅登录用户可上传）
        if (!jwtUtil.validateToken(token)) {
            return Result.error(401, "Token无效或已过期");
        }
        try {
            // 2. 调用你工具类中专属的社区评论图片上传方法
            String imageUrl = fileUploadUtil.uploadCommunityPostCommentImage(file);
            return Result.success(imageUrl);
        } catch (IllegalArgumentException e) {
            // 文件格式/空文件等校验错误
            return Result.error(1, e.getMessage());
        } catch (Exception e) {
            // 其他异常（目录创建失败/文件写入失败等）
            return Result.error(1, "图片上传失败：" + e.getMessage());
        }
    }

//    @GetMapping("/count/{postId}")
//    public Result<?> getCommentCount(@PathVariable @NotNull(message = "帖子ID不能为空") Long postId) {
//        try {
//            Integer count = commentService.getCommentCount(postId);
//            return Result.success(count);
//        } catch (Exception e) {
//            return Result.error(1, "获取评论数失败：" + e.getMessage());
//        }
//    }
}