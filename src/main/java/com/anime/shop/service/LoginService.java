package com.anime.shop.service;

import com.anime.shop.common.BizException;
import com.anime.shop.common.ResultCode;
import com.anime.shop.entity.UserEntity;
import com.anime.shop.mapper.UserMapper;
import com.anime.shop.util.JwtUtil;
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
    private JwtUtil jwtUtil;

    @Resource
    private CaptchaService captchaService;

    public String login(String username, String password) {
        System.out.println(">>> 输入用户名=" + username);
        System.out.println(">>> 输入密码=" + password);

        UserEntity user = userMapper.selectOne(
                new QueryWrapper<UserEntity>().eq("username", username));

        System.out.println(">>> 库密码=" + (user == null ? "null" : user.getPassword()));

        if (user == null) {
            System.out.println(">>> 用户不存在");
            throw new BizException(ResultCode.USERNAME_OR_PWD_ERROR);
        }
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        System.out.println(">>> 校验结果=" + matches);

        if (!matches) {
            System.out.println(">>> 密码不匹配");
            throw new BizException(ResultCode.USERNAME_OR_PWD_ERROR);
        }

        return jwtUtil.generateToken(user.getId());
    }

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
        userMapper.insert(user);
    }
}
