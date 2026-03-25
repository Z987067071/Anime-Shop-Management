package com.anime.shop.mapper.impl;

import com.anime.shop.common.Result;
import com.anime.shop.entity.UserEntity;
import com.anime.shop.mapper.UserMapper;
import com.anime.shop.service.MobileUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MobileUserServiceImpl implements MobileUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> editUsername(Long userId, String newNickname) {
        // 1. 校验用户是否存在
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }

        // 2. 校验新昵称是否与原昵称一致（可选，提升体验）
        if (newNickname.equals(user.getNickname())) {
            return Result.fail("新昵称不能与原昵称一致");
        }

        // 3. 无需校验唯一性！直接更新nickname字段（核心修改）
        UserEntity updateUser = new UserEntity();
        updateUser.setId(userId);
        updateUser.setNickname(newNickname); // 修改nickname而非username
        updateUser.setUpdatedAt(LocalDateTime.now());
        int updateCount = userMapper.updateById(updateUser);

        if (updateCount == 0) {
            return Result.fail("昵称修改失败");
        }

        return Result.ok(null);
    }
}
