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

    @PostMapping("/file")
    public Result<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.build(ResultCode.ERROR.getCode(), "文件不能为空", null);
            }
            String uploadPath = System.getProperty("user.dir") + "/upload/";
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + suffix;
            File dest = new File(uploadPath + fileName);
            file.transferTo(dest);
            String fileUrl = "http://localhost:8080/upload/" + fileName;
            return Result.success(fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.build(ResultCode.ERROR.getCode(), "文件上传失败", e.getMessage());
        }
    }
}
