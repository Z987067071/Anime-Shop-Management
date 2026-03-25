package com.anime.shop.admin.service;

import com.anime.shop.admin.controller.dto.UserAddDTO;

public interface AdminUserAddService {
    /**
     * 新增账户
     * @param dto 新增账户入参
     */
    void addUser(UserAddDTO dto);

    /**
     * 校验用户名唯一性
     * @param username 登录账号
     */
    void checkUsernameUnique(String username);
}