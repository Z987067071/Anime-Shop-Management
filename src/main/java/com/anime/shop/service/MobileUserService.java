package com.anime.shop.service;

import com.anime.shop.common.Result;

/**
 * 移动端用户服务接口
 */
public interface MobileUserService {

    /**
     * 移动端用户修改自身用户名
     * @param userId 当前登录用户ID
     * @param newUsername 新用户名
     * @return 操作结果
     */
    Result<Void> editUsername(Long userId, String newUsername);
}
