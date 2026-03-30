package com.anime.shop.admin.service;

import com.anime.shop.admin.controller.dto.PasswordResetDTO;
import com.anime.shop.admin.utils.RoleLevelEnum;
import com.anime.shop.common.Result;
import com.anime.shop.common.ResultCode;
import com.anime.shop.entity.UserEntity;
import com.anime.shop.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Result<Map<String, Object>> getUsers(
        Long id, String username, String nickname,
        String tel, String email, String role,
        LocalDateTime createdAt, LocalDateTime updatedAt,
        String idCard, Integer status,
        int page, int size)
    {
        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();

        if (id != null) wrapper.eq("id", id);
        if (StringUtils.hasText(username)) wrapper.like("username", username);
        if (StringUtils.hasText(nickname)) wrapper.like("nickname", nickname);
        if (StringUtils.hasText(tel)) wrapper.like("tel", tel);
        if (StringUtils.hasText(email)) wrapper.like("email", email);
        if (StringUtils.hasText(role)) wrapper.eq("role", role);
        if (StringUtils.hasText(idCard)) wrapper.like("id_card", idCard);
        if (status != null) wrapper.eq("status", status);
        if (createdAt != null) wrapper.ge("created_at", createdAt);
        if (updatedAt != null) wrapper.le("updated_at", updatedAt);

        Page<UserEntity> pageParam = new Page<>(page, size);
        IPage<UserEntity> pageResult = userMapper.selectPage(pageParam, wrapper);

        List<Map<String, Object>> userList = pageResult.getRecords().stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", String.valueOf(user.getId()));
                    userMap.put("username", user.getUsername());
                    userMap.put("nickname", user.getNickname());
                    userMap.put("tel", user.getTel());
                    userMap.put("email", user.getEmail());
                    userMap.put("role", user.getRole());
                    userMap.put("idCard", user.getIdCard());
                    userMap.put("status", user.getStatus());
                    if (user.getCreatedAt() != null) {
                        userMap.put("createdAt", DATE_TIME_FORMATTER.format(user.getCreatedAt()));
                    }
                    if (user.getUpdatedAt() != null) {
                        userMap.put("updatedAt", DATE_TIME_FORMATTER.format(user.getUpdatedAt()));
                    }
                    return userMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> map = new HashMap<>();
        map.put("list", userList);
        map.put("total", pageResult.getTotal());
        map.put("page", pageResult.getCurrent());
        map.put("size", pageResult.getSize());

        return Result.ok(map);
    }
    /**
     * 管理员按ID重置用户密码
     */
    public Result<Void> adminResetPasswordById(PasswordResetDTO dto) {
        // 1. 基础参数校验（返回PARAM_ERROR）
        if (dto.getId() == null) {
            return Result.build(ResultCode.PARAM_ERROR.getCode(), "用户ID不能为空", null);
        }
        if (!StringUtils.hasText(dto.getNewPassword())) {
            return Result.build(ResultCode.PARAM_ERROR.getCode(), "新密码不能为空", null);
        }
        if (dto.getNewPassword().length() < 6 || dto.getNewPassword().length() > 20) {
            return Result.build(ResultCode.PARAM_ERROR.getCode(), "密码长度必须在6-20位之间", null);
        }
        if (!StringUtils.hasText(dto.getOperatorRole())) {
            return Result.build(ResultCode.PARAM_ERROR.getCode(), "操作人角色不能为空", null);
        }

        // 2. 操作人权限校验
        RoleLevelEnum operatorEnum = RoleLevelEnum.getByRole(dto.getOperatorRole());
        if (operatorEnum == null || operatorEnum == RoleLevelEnum.CONSUMER
                || operatorEnum == RoleLevelEnum.MEMBER) {
            return Result.build(ResultCode.ROLE_PERMISSION_DENIED.getCode(), "无权限重置密码", null);
        }

        // 3. 查询用户是否存在
        UserEntity user = userMapper.selectById(dto.getId());
        if (user == null) {
            return Result.build(ResultCode.USER_NOT_FOUND.getCode(), ResultCode.USER_NOT_FOUND.getMsg(), null);
        }

        // 校验层级：不能重置同级或更高权限用户的密码
        RoleLevelEnum targetEnum = RoleLevelEnum.getByRole(user.getRole());
        if (!operatorEnum.canEdit(targetEnum)) {
            return Result.build(ResultCode.ROLE_PERMISSION_DENIED.getCode(), "无权限重置同级或更高权限账户的密码", null);
        }

        try {
            // 4. 加密新密码并执行更新
            String encodedPassword = passwordEncoder.encode(dto.getNewPassword());
            UserEntity updateUser = new UserEntity();
            updateUser.setId(dto.getId());
            updateUser.setPassword(encodedPassword);
            updateUser.setUpdatedAt(LocalDateTime.now());

            int updateRows = userMapper.updateById(updateUser);
            if (updateRows == 0) {
                // 密码重置失败（用ERROR基础码+自定义提示）
                return Result.build(ResultCode.ERROR.getCode(), "密码重置失败", null);
            }

            // 5. 重置成功（返回SUCCESS，data为null）
            return Result.build(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), null);
        } catch (Exception e) {
            // 6. 系统异常（返回SERVER_ERROR）
            e.printStackTrace();
            return Result.build(ResultCode.SERVER_ERROR.getCode(), ResultCode.SERVER_ERROR.getMsg(), null);
        }
    }

    /**
     * 管理员删除账户（仅按ID删除，校验权限+用户存在性）
     * @param id 用户ID
     * @param operatorRole 操作人角色
     * @return Result<Void>
     */
    public Result<Void> deleteUserById(Long id, String operatorRole) {
        // 1. 参数校验
        if (id == null) {
            return Result.build(ResultCode.PARAM_ERROR.getCode(), "用户ID不能为空", null);
        }
        if (!StringUtils.hasText(operatorRole)) {
            return Result.build(ResultCode.PARAM_ERROR.getCode(), "操作人角色不能为空", null);
        }

        // 2. 权限校验：操作人只能删除权限比自己低的用户
        RoleLevelEnum operatorEnum = RoleLevelEnum.getByRole(operatorRole);
        if (operatorEnum == null || operatorEnum == RoleLevelEnum.CONSUMER
                || operatorEnum == RoleLevelEnum.MEMBER) {
            return Result.build(ResultCode.ROLE_PERMISSION_DENIED.getCode(), "无权限删除账户", null);
        }

        // 3. 查询用户是否存在 + 校验层级
        UserEntity user = userMapper.selectById(id);
        if (user == null) {
            return Result.build(ResultCode.NOT_FOUND.getCode(), "账户不存在", null);
        }
        RoleLevelEnum targetEnum = RoleLevelEnum.getByRole(user.getRole());
        if (!operatorEnum.canEdit(targetEnum)) {
            return Result.build(ResultCode.ROLE_PERMISSION_DENIED.getCode(), "无权限删除同级或更高权限账户", null);
        }

        try {
            // 4. 执行删除（物理删除，如需逻辑删除可改为更新status字段）
            int deleteRows = userMapper.deleteById(id);
            if (deleteRows == 0) {
                return Result.build(ResultCode.ERROR.getCode(), "账户删除失败", null);
            }

            // 5. 删除成功
            return Result.build(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), null);
        } catch (Exception e) {
            // 6. 系统异常
            e.printStackTrace();
            return Result.build(ResultCode.SERVER_ERROR.getCode(), ResultCode.SERVER_ERROR.getMsg(), null);
        }
    }

    /**
     * 更新用户头像（支持用户自己更新/管理员代更）
     * @param userId 用户ID
     * @param avatarUrl 头像URL
     * @return Result<String> 返回更新后的头像URL
     */
    public Result<String> updateUserAvatar(Long userId, String avatarUrl) {
        // 1. 参数校验
        if (userId == null) {
            return Result.build(ResultCode.PARAM_ERROR.getCode(), "用户ID不能为空", null);
        }
        if (!StringUtils.hasText(avatarUrl)) {
            return Result.build(ResultCode.PARAM_ERROR.getCode(), "头像URL不能为空", null);
        }

        // 2. 查询用户是否存在
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            return Result.build(ResultCode.USER_NOT_FOUND.getCode(), "用户不存在", null);
        }

        try {
            // 3. 更新头像和更新时间
            UserEntity updateUser = new UserEntity();
            updateUser.setId(userId);
            updateUser.setAvatar(avatarUrl); // 对应数据库avatar字段
            updateUser.setUpdatedAt(LocalDateTime.now());

            int updateRows = userMapper.updateById(updateUser);
            if (updateRows == 0) {
                return Result.build(ResultCode.ERROR.getCode(), "头像更新失败", null);
            }

            // 4. 返回成功结果（携带头像URL）
            return Result.ok(avatarUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.build(ResultCode.SERVER_ERROR.getCode(), "服务器内部错误，头像更新失败", null);
        }
    }

    /**
     * 查询用户头像URL（供前端初始化显示）
     * @param userId 用户ID
     * @return Result<String> 头像URL
     */
    public Result<String> getUserAvatar(Long userId) {
        // 1. 参数校验
        if (userId == null) {
            return Result.build(ResultCode.PARAM_ERROR.getCode(), "用户ID不能为空", null);
        }

        // 2. 查询用户
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            return Result.build(ResultCode.USER_NOT_FOUND.getCode(), "用户不存在", null);
        }

        // 3. 返回头像URL（空则返回空字符串）
        return Result.ok(user.getAvatar() == null ? "" : user.getAvatar());
    }
}