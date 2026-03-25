package com.anime.shop.service;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class CaptchaService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final int EXPIRE_MINUTES = 5;
    private static final int COOLDOWN_SECONDS = 60;


    public String generateCaptcha(String username) {

        String code = String.valueOf((int) ((Math.random() * 9000) + 1000));

        String key = "captcha:" + username;
        stringRedisTemplate.opsForValue().set(key, code, Duration.ofMinutes(5));

        return code;
    }

    public boolean validate(String username, String code) {
        String key = "captcha:" + username;
        String cached = stringRedisTemplate.opsForValue().get(key);
        return code != null && code.equals(cached);
    }
}