package com.anime.shop.controller;

import com.anime.shop.admin.utils.FileUploadUtil;
import com.anime.shop.common.Result;
import com.anime.shop.controller.community.PostDTO;
import com.anime.shop.controller.community.PostDetailDTO;
import com.anime.shop.controller.community.PostPageDTO;
import com.anime.shop.service.CommunityPostService;
import com.anime.shop.service.PostLikeService;
import com.anime.shop.util.JwtUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mobile/community/post")
@RequiredArgsConstructor
public class CommunityPostController {

    private final CommunityPostService postService;
    private final FileUploadUtil fileUploadUtil;
    private final JwtUtil jwtUtil;
    private final PostLikeService postLikeService;

    /**
     * 分页获取帖子列表（严格匹配表结构）
     */
    @GetMapping("/list")
    public Result<IPage<PostDTO>> getPostList(PostPageDTO pageDTO) {
        try {
            IPage<PostDTO> postPage = postService.getPostList(pageDTO);
            return Result.success(postPage);
        } catch (Exception e) {
            return Result.fail("获取帖子列表失败：" + e.getMessage());
        }
    }

    @PostMapping("/addPost")
    public Result<Boolean> addPost(
            @Valid @RequestBody PostDTO postDTO,
            @RequestHeader("token") String token
    ) {
        try {
            // 1. 校验Token有效性
            if (!jwtUtil.validateToken(token)) {
                return Result.fail("Token无效或已过期");
            }

            // 2. 解析用户ID（从Token中获取，避免前端传错）
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return Result.fail("无法解析用户信息");
            }
            postDTO.setUserId(userId); // 强制设置当前登录用户ID

            // 3. 调用Service发布帖子
            boolean success = postService.publishPost(postDTO);
            if (success) {
                return Result.success(true);
            } else {
                return Result.fail("发布帖子失败");
            }
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            return Result.fail("发布帖子失败：" + e.getMessage());
        }
    }

    @PostMapping("/uploadImage")
    public Result<String> uploadPostImage(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "token", required = false) String token // 可选：校验登录
    ) {
        try {
            // 核心：调用你已有的uploadCommunityPostImage方法上传图片
            String imageUrl = fileUploadUtil.uploadCommunityPostImage(file);
            return Result.success(imageUrl);
        } catch (IllegalArgumentException e) {
            // 捕获文件格式/空文件等参数错误
            return Result.fail(e.getMessage());
        } catch (RuntimeException e) {
            // 捕获目录创建失败等运行时异常
            return Result.fail("图片上传失败：" + e.getMessage());
        } catch (Exception e) {
            return Result.fail("图片上传失败：" + e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Map<String, Object>> getPostDetail(
            @PathVariable Long id,
            @RequestHeader(value = "token", required = false) String token
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            PostDetailDTO detailDTO = postService.getPostDetail(id);
            if (token != null && jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserIdFromToken(token);
                Boolean isLiked = postLikeService.isLiked(id, userId);
                detailDTO.setIsLiked(isLiked);
            } else {
                detailDTO.setIsLiked(false);
            }

            result.put("code", 0);
            result.put("msg", "查询成功");
            result.put("data", detailDTO);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            result.put("code", 400);
            result.put("msg", "参数错误：" + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        } catch (RuntimeException e) {
            result.put("code", 404);
            result.put("msg", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "查询失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @GetMapping("/like/{postId}")
    public ResponseEntity<Map<String, Object>> likePost(
            @PathVariable Long postId,
            @RequestHeader("token") String token
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 校验Token有效性
            if (!jwtUtil.validateToken(token)) {
                result.put("code", 401);
                result.put("msg", "Token无效或已过期");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
            }

            // 2. 解析用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                result.put("code", 400);
                result.put("msg", "无法解析用户信息");
                return ResponseEntity.badRequest().body(result);
            }

            // 3. 调用点赞服务
            var likeResult = postLikeService.toggleLike(postId, userId);

            // 4. 适配你的返回格式（和detail接口一致）
            result.put("code", likeResult.getCode());
            result.put("msg", likeResult.getMsg());
            result.put("data", Map.of(
                    "isLiked", likeResult.getIsLiked(),
                    "likeCount", likeResult.getLikeCount()
            ));

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            result.put("code", 400);
            result.put("msg", "参数错误：" + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        } catch (RuntimeException e) {
            result.put("code", 500);
            result.put("msg", "操作失败：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}