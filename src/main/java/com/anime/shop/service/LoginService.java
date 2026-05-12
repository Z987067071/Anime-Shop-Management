package com.anime.shop.service;

import com.anime.shop.common.BizException;
import com.anime.shop.common.ResultCode;
import com.anime.shop.entity.UserEntity;
import com.anime.shop.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private CaptchaService captchaService;

    @Transactional
    public void register(String username, String password, String captcha) {
        if (userMapper.selectCount(new QueryWrapper<UserEntity>().eq("username", username)) > 0) {
            throw new BizException(ResultCode.USER_EXIST);
        }
        if (!captchaService.validate(username, captcha)) {
            throw new BizException(ResultCode.CAPTCHA_ERROR);
        }
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("consumer"); // 移动端注册用户默认为普通用户
        user.setNickname(username); // 默认昵称设为用户名，用户后续可修改
        userMapper.insert(user);
    }
}
