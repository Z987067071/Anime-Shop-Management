package com.anime.shop.controller;

import com.anime.shop.common.BizException;
import com.anime.shop.common.Result;
import com.anime.shop.common.ResultCode;
import com.anime.shop.controller.dto.LoginDTO;
import com.anime.shop.controller.dto.RegisterDTO;
import com.anime.shop.entity.UserEntity;
import com.anime.shop.mapper.UserMapper;
import com.anime.shop.service.LoginService;
import com.anime.shop.util.JwtUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     * @param dto 登录信息
     * @return 登录结果
     */

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody LoginDTO dto) {
        UserEntity user = userMapper.selectOne(
                Wrappers.<UserEntity>lambdaQuery().eq(UserEntity::getUsername, dto.getUsername()));
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BizException(ResultCode.USERNAME_OR_PWD_ERROR);
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BizException(ResultCode.USER_DISABLED);
        }
        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("role", user.getRole());
        map.put("nickname", user.getNickname());
        map.put("id", String.valueOf(user.getId()));
        map.put("avatar", user.getAvatar());
        return Result.ok(map);
    }

    /**
     * 用户注册
     * @param registerDTO 注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterDTO registerDTO) {
        loginService.register(registerDTO.getUsername(), registerDTO.getPassword(), registerDTO.getCaptcha());
        return Result.ok(null);
    }
}