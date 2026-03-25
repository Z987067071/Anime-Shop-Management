package com.anime.shop.admin.service;

import com.anime.shop.admin.controller.dto.UserEditDTO;
import com.anime.shop.admin.utils.RoleLevelEnum;
import com.anime.shop.common.BizException;
import com.anime.shop.common.ResultCode;
import com.anime.shop.entity.UserEntity;
import com.anime.shop.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 后台用户编辑Service
 */
@Service
public class AdminUserEditService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 编辑用户信息（核心接口）
     */
    @Transactional(rollbackFor = Exception.class)
    public void editUser(UserEditDTO editDTO) {
        // 1. 校验操作人角色有效性
        RoleLevelEnum operatorRoleEnum = RoleLevelEnum.getByRole(editDTO.getOperatorRole());
        if (operatorRoleEnum == null || operatorRoleEnum == RoleLevelEnum.CONSUMER) {
            // 抛业务异常：权限不足
            throw new BizException(ResultCode.ROLE_PERMISSION_DENIED);
        }

        // 2. 查询被编辑用户是否存在
        UserEntity targetUser = userMapper.selectById(editDTO.getId());
        if (targetUser == null) {
            // 抛业务异常：用户不存在（适配你的 ResultCode.USER_NOT_FOUND）
            throw new BizException(ResultCode.USER_NOT_FOUND);
        }

        // 3. 权限判断：操作人不能编辑同级/更高权限的角色
        String targetRole = editDTO.getRole() != null ? editDTO.getRole() : targetUser.getRole();
        RoleLevelEnum targetRoleEnum = RoleLevelEnum.getByRole(targetRole);

        if (!operatorRoleEnum.canEdit(targetRoleEnum)) {
            throw new BizException(ResultCode.ROLE_PERMISSION_DENIED);
        }

        // 4. 组装更新参数（只更新非空参数）
        UserEntity updateUser = new UserEntity();
        updateUser.setId(editDTO.getId()); // 主键

        //更新修改时间
        updateUser.setUpdatedAt(LocalDateTime.now());

        if (StringUtils.hasText(editDTO.getIdCard())) {
            updateUser.setIdCard(editDTO.getIdCard());
        }
        if (StringUtils.hasText(editDTO.getTel())) {
            updateUser.setTel(editDTO.getTel());
        }
        if (StringUtils.hasText(editDTO.getNickname())) {
            updateUser.setNickname(editDTO.getNickname());
        }
        if (StringUtils.hasText(editDTO.getRole())) {
            updateUser.setRole(editDTO.getRole());
        }
        if (StringUtils.hasText(editDTO.getEmail())) {
            updateUser.setEmail(editDTO.getEmail());
        }
        if (editDTO.getStatus() != null) {
            updateUser.setStatus(Integer.parseInt(editDTO.getStatus()));
        }

        // 5. 执行更新
        int updateCount = userMapper.updateById(updateUser);
        if (updateCount == 0) {
            throw new BizException(ResultCode.SERVER_ERROR);
        }
    }
}