package com.anime.shop.admin.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class FileUploadUtil {

    // ========== 基础配置（统一用外部目录，避免classpath） ==========
    // 头像存储（保留原有逻辑，但移到外部目录）
    private String avatarUploadPath = "D:/cxdownload/bishe/anime-shop/src/main/resources/avatar/";
    private String avatarAccessPath = "/avatar/";
    // 商品图片存储（复用原有配置）
    private String productUploadPath = "D:/cxdownload/bishe/anime-shop/src/main/resources/product/";
    private String productAccessPath = "/product/";
    // 工单/评论图片存储（核心：移到外部upload目录）
    private String feedbackUploadPath = "D:/cxdownload/bishe/anime-shop/src/main/resources/feedback/";
    private String feedbackAccessPath = "/feedback/";

    private String commentUploadPath = "D:/cxdownload/bishe/anime-shop/src/main/resources/comment/";
    private String commentAccessPath = "/comment/";

    private String communityPostUploadPath = "D:/cxdownload/bishe/anime-shop/src/main/resources/communityPost/";
    private String communityPostAccessPath = "/communityPost/";

    private String communityPostCommentUploadPath = "D:/cxdownload/bishe/anime-shop/src/main/resources/communityPostComment/";
    private String communityPostCommentAccessPath = "/communityPostComment/";

    // 服务器访问前缀
    private String serverUrl = "http://localhost:8080";

    public String uploadAvatar(MultipartFile file) throws IOException {
        return uploadImage(file, avatarUploadPath, avatarAccessPath);
    }

    public String uploadProductImage(MultipartFile file) throws IOException {
        return uploadImage(file, productUploadPath, productAccessPath);
    }

    public String uploadFeedbackImage(MultipartFile file) throws IOException {
        return uploadImage(file, feedbackUploadPath, feedbackAccessPath);
    }

    public String uploadCommentImage(MultipartFile file) throws IOException {
        return uploadImage(file, commentUploadPath, commentAccessPath);
    }

    public String uploadCommunityPostImage(MultipartFile file) throws IOException {
        return uploadImage(file, communityPostUploadPath, communityPostAccessPath);
    }

    public String uploadCommunityPostCommentImage(MultipartFile file) throws IOException {
        return uploadImage(file, communityPostCommentUploadPath, communityPostCommentAccessPath);
    }

    private String uploadImage(MultipartFile file, String uploadPath, String accessPath) throws IOException {
        // 1. 基础校验
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传的文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.lastIndexOf(".") == -1) {
            throw new IllegalArgumentException("文件格式不正确，无后缀名");
        }
        String suffix = FileUtil.extName(originalFilename);
        if (!"jpg,jpeg,png,gif".contains(suffix.toLowerCase())) {
            throw new IllegalArgumentException("仅支持jpg、jpeg、png、gif格式的图片");
        }

        // 2. 生成唯一文件名
        String fileName = IdUtil.simpleUUID() + "." + suffix;

        // 3. 创建存储目录（外部目录，非classpath）
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            boolean mkdirs = uploadDir.mkdirs();
            if (!mkdirs) {
                throw new RuntimeException("创建存储目录失败：" + uploadPath);
            }
        }

        // 4. 核心修复：同步写入文件 + 强制刷新缓冲区
        File destFile = new File(uploadPath + fileName);
        if (destFile.exists()) {
            destFile.delete();
        }
        // 替换transferTo()为字节流同步写入，确保文件完全写入磁盘
        try (OutputStream os = new FileOutputStream(destFile)) {
            byte[] bytes = file.getBytes();
            os.write(bytes);
            os.flush(); // 强制刷新，避免异步写入延迟
        }

        // 5. 校验文件是否真的写入成功
        if (!destFile.exists() || destFile.length() == 0) {
            throw new IOException("文件写入失败，目标文件为空：" + destFile.getAbsolutePath());
        }

        // 6. 生成完整访问URL
        return serverUrl + accessPath + fileName;
    }
}