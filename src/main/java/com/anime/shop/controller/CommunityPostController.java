package com.anime.shop.controller;

import com.anime.shop.admin.utils.FileUploadUtil;
import com.anime.shop.common.Result;
import com.anime.shop.controller.community.PostDTO;
import com.anime.shop.controller.community.PostDetailDTO;
import com.anime.shop.controller.community.PostPageDTO;
import com.anime.shop.service.CommunityPostService;
import com.anime.shop.service.PostLikeService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/mobile/community/post")
@RequiredArgsConstructor
public class CommunityPostController {

    private final CommunityPostService postService;
    private final FileUploadUtil fileUploadUtil;
    private final PostLikeService postLikeService;

    /** 分页获取帖子列表 */
    @GetMapping("/list")
    public Result<IPage<PostDTO>> getPostList(PostPageDTO pageDTO) {
        return Result.success(postService.getPostList(pageDTO));
    }

    /** 发布帖子（需登录，userId 由拦截器注入） */
    @PostMapping("/addPost")
    public Result<Boolean> addPost(
            @Valid @RequestBody PostDTO postDTO,
            @RequestAttribute Long userId) {
        postDTO.setUserId(userId);
        return Result.success(postService.publishPost(postDTO));
    }

    /** 上传帖子图片（需登录） */
    @PostMapping("/uploadImage")
    public Result<String> uploadPostImage(
            @RequestParam("file") MultipartFile file,
            @RequestAttribute Long userId) throws java.io.IOException {
        return Result.success(fileUploadUtil.uploadCommunityPostImage(file));
    }

    /** 获取帖子详情（可选登录） */
    @GetMapping("/detail/{id}")
    public Result<PostDetailDTO> getPostDetail(
            @PathVariable Long id,
            @RequestAttribute(required = false) Long userId) {
        PostDetailDTO detail = postService.getPostDetail(id);
        detail.setIsLiked(userId != null && postLikeService.isLiked(id, userId));
        return Result.success(detail);
    }

    /** 帖子点赞/取消点赞（需登录） */
    @GetMapping("/like/{postId}")
    public Result<?> likePost(
            @PathVariable Long postId,
            @RequestAttribute Long userId) {
        return Result.success(postLikeService.toggleLike(postId, userId));
    }
}
