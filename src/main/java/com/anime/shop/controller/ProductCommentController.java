package com.anime.shop.controller;

import com.anime.shop.admin.utils.FileUploadUtil;
import com.anime.shop.common.Result;
import com.anime.shop.controller.dto.productdetail.ProductCommentAddDTO;
import com.anime.shop.controller.dto.productdetail.ProductCommentVO;
import com.anime.shop.service.ProductCommentService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商品评论控制器
 */
@RestController
@RequestMapping("/api/mobile/product/comment")
@RequiredArgsConstructor
@Validated
public class ProductCommentController {

    private final ProductCommentService commentService;
    private final FileUploadUtil fileUploadUtil;

    /** 发布商品评论（需登录，userId 由拦截器注入） */
    @PostMapping("/add")
    public Result<?> addComment(
            @Valid @RequestBody ProductCommentAddDTO dto,
            @RequestAttribute Long userId) {
        Long commentId = commentService.addComment(dto, userId);
        return Result.success(commentId);
    }

    /** 获取商品评论列表（可选登录） */
    @GetMapping("/list")
    public Result<?> getCommentList(
            @RequestParam @NotNull Long productId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestAttribute(required = false) Long userId) {
        Page<ProductCommentVO> page = commentService.getCommentList(productId, pageNum, pageSize, userId);
        return Result.success(page);
    }

    /** 商品评论点赞（需登录） */
    @PostMapping("/like/{commentId}")
    public Result<?> toggleLike(
            @PathVariable Long commentId,
            @RequestAttribute Long userId) {
        commentService.toggleLike(commentId, userId);
        return Result.success("操作成功");
    }

    /** 删除商品评论（需登录） */
    @PostMapping("/delete/{commentId}")
    public Result<?> deleteComment(
            @PathVariable Long commentId,
            @RequestAttribute Long userId,
            @RequestAttribute String role) {
        commentService.deleteComment(commentId, userId, role);
        return Result.success("删除成功");
    }

    /** 获取商品评论数 */
    @GetMapping("/count/{productId}")
    public Result<?> getCommentCount(@PathVariable @NotNull Long productId) {
        return Result.success(commentService.getCommentCount(productId));
    }

    /** 上传评论图片（需登录） */
    @PostMapping("/upload/image")
    public Result<?> uploadCommentImage(
            @RequestParam("file") MultipartFile file,
            @RequestAttribute Long userId) throws java.io.IOException {
        return Result.success(fileUploadUtil.uploadCommentImage(file));
    }
}
