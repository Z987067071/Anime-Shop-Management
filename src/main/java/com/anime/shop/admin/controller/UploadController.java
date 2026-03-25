package com.anime.shop.admin.controller;

import com.anime.shop.common.Result;
import com.anime.shop.common.ResultCode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/product/upload")
public class UploadController {

    // 本地上传示例（生产环境建议用OSS/MinIO）
    @PostMapping("/file")
    public Result<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 1. 校验文件
            if (file.isEmpty()) {
                return Result.build(ResultCode.ERROR.getCode(), "文件不能为空", null);
            }
            // 2. 定义上传路径（如：项目根目录/upload）
            String uploadPath = System.getProperty("user.dir") + "/upload/";
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 3. 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + suffix;
            // 4. 保存文件
            File dest = new File(uploadPath + fileName);
            file.transferTo(dest);
            // 5. 返回图片访问地址（根据实际部署路径调整）
            String fileUrl = "http://localhost:8080/upload/" + fileName;
            return Result.success(fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.build(ResultCode.ERROR.getCode(), "文件上传失败", e.getMessage());
        }
    }
}
