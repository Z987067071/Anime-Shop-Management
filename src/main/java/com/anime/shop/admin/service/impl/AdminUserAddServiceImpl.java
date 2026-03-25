package com.anime.shop.admin.service.impl;

import com.anime.shop.admin.controller.dto.UserAddDTO;
import com.anime.shop.admin.service.AdminUserAddService;
import com.anime.shop.common.BizException;
import com.anime.shop.common.ResultCode;
import com.anime.shop.entity.UserEntity;
import com.anime.shop.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class AdminUserAddServiceImpl implements AdminUserAddService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 权限校验：仅admin/manager可新增账户，manager不能新增admin角色
     */
    private void checkPermission(String operatorRole, String targetRole) {
        // 非管理员/经理无权限
        if (!"admin".equals(operatorRole) && !"manager".equals(operatorRole)) {
            throw new BizException(ResultCode.ROLE_PERMISSION_DENIED);
        }
        // 经理不能新增超级管理员
        if ("manager".equals(operatorRole) && "admin".equals(targetRole)) {
            throw new BizException(ResultCode.ROLE_PERMISSION_DENIED);
        }
    }

    @Override
    public void checkUsernameUnique(String username) {
        long count = userMapper.selectCount(new QueryWrapper<UserEntity>().eq("username", username));
        if (count > 0) {
            throw new BizException(ResultCode.USER_EXIST);
        }
    }

    @Override
    @Transactional
    public void addUser(UserAddDTO dto) {
        // 1. 权限校验
        checkPermission(dto.getOperatorRole(), dto.getRole());

        // 2. 校验用户名唯一性（复用移动端同款校验逻辑）
        checkUsernameUnique(dto.getUsername());

        // 3. 组装用户实体（对齐移动端注册逻辑）
        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        // 密码加密：和移动端登录注册使用同一套PasswordEncoder
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
        user.setNickname(dto.getNickname());

        // 4. 选填字段处理
        user.setEmail(StringUtils.hasText(dto.getEmail()) ? dto.getEmail() : null);
        user.setTel(StringUtils.hasText(dto.getTel()) ? dto.getTel() : null);
        user.setIdCard(StringUtils.hasText(dto.getIdCard()) ? dto.getIdCard() : null);

        // 5. 默认值（新增账户默认启用）
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // 6. 插入数据库（和移动端注册同款insert逻辑）
        int insertCount = userMapper.insert(user);
        if (insertCount == 0) {
            throw new BizException(ResultCode.SERVER_ERROR);
        }
    }
}