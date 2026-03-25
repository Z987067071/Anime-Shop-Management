package com.anime.shop.controller;

import com.anime.shop.common.BizException;
import com.anime.shop.common.Result;
import com.anime.shop.common.ResultCode;
import com.anime.shop.service.CaptchaService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;


@RestController
@RequestMapping("/api")
public class CaptchaController {

    @Resource
    private CaptchaService captchaService;

    @GetMapping("/captcha/img")
    public Result<String> captchaImg(@RequestParam String username) {
        // 1. 生成 4 位数字
        String code = captchaService.generateCaptcha(username);

        // 2. 生成图片 Base64
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            // 宽 120 高 40，黑底白字
            BufferedImage img = new BufferedImage(80, 40, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 120, 40);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Serif", Font.BOLD, 24));
            g.drawString(code, 20, 28);
            g.dispose();

            ImageIO.write(img, "png", os);
            return Result.ok("data:image/png;base64," + Base64.getEncoder().encodeToString(os.toByteArray()));
        } catch (Exception e) {
            throw new BizException(ResultCode.SERVER_ERROR);
        }
    }
}