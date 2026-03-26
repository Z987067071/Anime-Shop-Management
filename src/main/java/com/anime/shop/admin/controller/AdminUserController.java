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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

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
            @RequestParam(defaultValue = "10") int size
    ) {
        return adminUserService.getUsers(
                id, username, nickname, tel, email, role,
                createdAt, updatedAt,
                idCard, status,
                page, size
        );
    }

    /**
     * 新增用户
     * @param addDTO
     * @param bindingResult
     * @return
     */
    @PostMapping("/add")
    public Result<Void> addUser(@Valid @RequestBody UserAddDTO addDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("；"));
            return Result.build(ResultCode.PARAM_ERROR.getCode(), errorMsg, null);
        }

        try {
            adminUserAddService.addUser(addDTO);
            return Result.ok(null);
        } catch (BizException e) {
            return Result.build(e.getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return Result.build(ResultCode.SERVER_ERROR.getCode(), ResultCode.SERVER_ERROR.getMsg(), null);
        }
    }

    /**
     * 编辑用户信息
     */
    @PostMapping("/edit")
    public Result<Void> editUser(@Valid @RequestBody UserEditDTO editDTO) {
        try {
            adminUserEditService.editUser(editDTO);
            return Result.ok(null);
        } catch (BizException e) {
            return Result.build(e.getCode(), e.getMessage(), null);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.build(ResultCode.SERVER_ERROR.getCode(), ResultCode.SERVER_ERROR.getMsg(), null);
        }
    }

    @PostMapping("/resetPassword")
    public Result<String> resetPassword(@Validated @RequestBody PasswordResetDTO dto) {
        adminUserService.adminResetPasswordById(dto);
        return Result.ok("密码重置成功");
    }

    /**
     * 删除账户
     * @param id 用户ID（路径参数）
     * @param operatorRole 操作人角色（请求参数）
     * @return Result<Void>
     */
    @DeleteMapping("/deleteAccount/{id}")
    public Result<Void> deleteUser(
            @PathVariable("id") Long id,
            @RequestParam("operatorRole") String operatorRole) {
        return adminUserService.deleteUserById(id, operatorRole);
    }


    /**
     * 管理员/用户上传头像
     * @param file 头像文件
     * @param userIdStr 获取登录用户ID（需替换为你的认证逻辑）
     * @return Result<String> 头像URL
     */
    @PostMapping("/avatar/upload")
    public Result<String> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userIdStr,
            @RequestParam(value = "operatorRole", defaultValue = "admin") String operatorRole) {
        try {
            if (userIdStr == null || "undefined".equals(userIdStr) || userIdStr.trim().isEmpty()) {
                userIdStr = "1999834598331097089"; // 默认管理员ID
            }
            Long userId = Long.parseLong(userIdStr);

            if (file.isEmpty()) {
                return Result.build(ResultCode.PARAM_ERROR.getCode(), "请选择要上传的头像文件！", null);
            }
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                return Result.build(ResultCode.PARAM_ERROR.getCode(), "头像文件格式错误，请上传jpg/png/jpeg格式！", null);
            }
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            if (!suffix.equalsIgnoreCase(".jpg") && !suffix.equalsIgnoreCase(".png") && !suffix.equalsIgnoreCase(".jpeg")) {
                return Result.build(ResultCode.PARAM_ERROR.getCode(), "仅支持上传jpg、png、jpeg格式的头像！", null);
            }
            if (file.getSize() > 2 * 1024 * 1024) {
                return Result.build(ResultCode.PARAM_ERROR.getCode(), "头像文件大小不能超过2MB！", null);
            }

            String avatarUrl = fileUploadUtil.uploadAvatar(file);

            Result<String> updateResult = adminUserService.updateUserAvatar(userId, avatarUrl);
            if (updateResult.getCode() != ResultCode.SUCCESS.getCode()) {
                return updateResult;
            }

            return Result.ok(avatarUrl);
        } catch (NumberFormatException e) {
            return Result.build(ResultCode.PARAM_ERROR.getCode(), "用户ID格式错误，请传递数字！", null);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.build(ResultCode.SERVER_ERROR.getCode(), "头像上传失败：" + e.getMessage(), null);
        }
    }

    @GetMapping("/avatar/{userId}")
    public Result<String> getUserAvatar(@PathVariable("userId") Long userId) {
        return adminUserService.getUserAvatar(userId);
    }

    /**
     * 校验用户名唯一性
     */
    @GetMapping("/checkUsernameUnique")
    public Result<Boolean> checkUsernameUnique(@RequestParam String username) {
        try {
            adminUserAddService.checkUsernameUnique(username);
            return Result.ok(true);
        } catch (BizException e) {
            return Result.ok(false);
        }
    }
}