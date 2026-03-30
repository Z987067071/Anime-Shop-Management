package com.anime.shop.admin.controller;

import com.anime.shop.admin.controller.dto.PasswordResetDTO;
import com.anime.shop.admin.controller.dto.UserAddDTO;
import com.anime.shop.admin.controller.dto.UserEditDTO;
import com.anime.shop.admin.service.AdminUserAddService;
import com.anime.shop.admin.service.AdminUserEditService;
import com.anime.shop.admin.service.AdminUserService;
import com.anime.shop.admin.utils.FileUploadUtil;
import com.anime.shop.common.BizException;
import com.anime.shop.common.Result;
import com.anime.shop.common.ResultCode;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@Validated
public class AdminUserController {

    @Resource
    private AdminUserService adminUserService;
    @Resource
    private AdminUserAddService adminUserAddService;
    @Resource
    private AdminUserEditService adminUserEditService;
    @Resource
    private FileUploadUtil fileUploadUtil;

    @GetMapping
    public Result<Map<String, Object>> users(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String tel,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String idCard,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) LocalDateTime createdAt,
            @RequestParam(required = false) LocalDateTime updatedAt,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return adminUserService.getUsers(id, username, nickname, tel, email, role,
                createdAt, updatedAt, idCard, status, page, size);
    }

    @PostMapping("/add")
    public Result<Void> addUser(@Valid @RequestBody UserAddDTO addDTO) {
        adminUserAddService.addUser(addDTO);
        return Result.success();
    }

    @PostMapping("/edit")
    public Result<Void> editUser(@Valid @RequestBody UserEditDTO editDTO) {
        adminUserEditService.editUser(editDTO);
        return Result.success();
    }

    @PostMapping("/resetPassword")
    public Result<String> resetPassword(@Validated @RequestBody PasswordResetDTO dto) {
        adminUserService.adminResetPasswordById(dto);
        return Result.success("密码重置成功");
    }

    @DeleteMapping("/deleteAccount/{id}")
    public Result<Void> deleteUser(
            @PathVariable Long id,
            @RequestParam String operatorRole) {
        return adminUserService.deleteUserById(id, operatorRole);
    }

    /**
     * 上传头像（userId 必须由前端传入，不再使用硬编码默认值）
     */
    @PostMapping("/avatar/upload")
    public Result<String> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "operatorRole", defaultValue = "admin") String operatorRole) throws java.io.IOException {
        if (file.isEmpty()) {
            throw new BizException(ResultCode.PARAM_ERROR, "请选择要上传的头像文件");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.matches("(?i).*\\.(jpg|jpeg|png)$")) {
            throw new BizException(ResultCode.PARAM_ERROR, "仅支持上传 jpg、png、jpeg 格式的头像");
        }
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new BizException(ResultCode.PARAM_ERROR, "头像文件大小不能超过 2MB");
        }
        String avatarUrl = fileUploadUtil.uploadAvatar(file);
        Result<String> updateResult = adminUserService.updateUserAvatar(userId, avatarUrl);
        if (updateResult.getCode() != ResultCode.SUCCESS.getCode()) {
            return updateResult;
        }
        return Result.success(avatarUrl);
    }

    @GetMapping("/avatar/{userId}")
    public Result<String> getUserAvatar(@PathVariable Long userId) {
        return adminUserService.getUserAvatar(userId);
    }

    @GetMapping("/checkUsernameUnique")
    public Result<Boolean> checkUsernameUnique(@RequestParam String username) {
        try {
            adminUserAddService.checkUsernameUnique(username);
            return Result.success(true);
        } catch (BizException e) {
            return Result.success(false);
        }
    }
}
