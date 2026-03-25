package com.anime.shop.controller;

import com.anime.shop.admin.utils.FileUploadUtil;
import com.anime.shop.common.Result;
import com.anime.shop.controller.dto.productdetail.ProductCommentAddDTO;
import com.anime.shop.controller.dto.productdetail.ProductCommentVO;
import com.anime.shop.service.ProductCommentService;
import com.anime.shop.util.JwtUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商品评论控制器（独立目录，和社区评论彻底分离）
 */
@RestController
@RequestMapping("/api/mobile/product/comment") // 独立路径
@RequiredArgsConstructor
@Validated
public class ProductCommentController {

    private final ProductCommentService commentService;
    private final JwtUtil jwtUtil;
    private final FileUploadUtil fileUploadUtil;

    /**
     * 发布商品评论
     */
    @PostMapping("/add")
    public Result<?> addComment(
            @Valid @RequestBody ProductCommentAddDTO dto,
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
     * 获取商品评论列表
     */
    @GetMapping("/list")
    public Result<?> getCommentList(
            @RequestParam @NotNull Long productId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestHeader(value = "token", required = false) String token) {
        Long currentUserId = null;
        if (token != null && jwtUtil.validateToken(token)) {
            currentUserId = jwtUtil.getUserIdFromToken(token);
        }
        Page<ProductCommentVO> page = commentService.getCommentList(productId, pageNum, pageSize, currentUserId);
        return Result.success(page);
    }

    /**
     * 商品评论点赞
     */
    @PostMapping("/like/{commentId}")
    public Result<?> toggleLike(
            @PathVariable Long commentId,
            @RequestHeader("token") String token) {
        if (!jwtUtil.validateToken(token)) {
            return Result.error(401, "Token无效");
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        try {
            commentService.toggleLike(commentId, userId);
            return Result.success("操作成功");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 删除商品评论
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

    @GetMapping("/count/{productId}")
    public Result<?> getCommentCount(@PathVariable @NotNull(message = "商品ID不能为空") Long productId) {
        try {
            Integer count = commentService.getCommentCount(productId);
            return Result.success(count); // 返回评论数（无数据时返回0）
        } catch (Exception e) {
            return Result.error(1, "获取评论数失败：" + e.getMessage());
        }
    }

    @PostMapping("/upload/image")
    public Result<?> uploadCommentImage(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("token") String token) {
        // 1. 校验登录状态
        if (!jwtUtil.validateToken(token)) {
            return Result.error(401, "Token无效或已过期");
        }
        try {
            // 2. 调用你原有工具类的上传方法
            String imageUrl = fileUploadUtil.uploadCommentImage(file);
            return Result.success(imageUrl);
        } catch (IllegalArgumentException e) {
            return Result.error(1, e.getMessage()); // 文件格式/空文件等校验错误
        } catch (Exception e) {
            return Result.error(1, "图片上传失败：" + e.getMessage()); // 其他异常（如目录创建失败）
        }
    }
}