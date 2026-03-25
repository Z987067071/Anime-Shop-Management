package com.anime.shop.controller;

import com.anime.shop.common.Result;
import com.anime.shop.controller.dto.BannerEntity;
import com.anime.shop.service.MobileBannerService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/mobile/banner")
public class MobileBannerController {

    @Resource
    private MobileBannerService mobileBannerService;

    /**
     * 查询首页轮播图（已启用）
     * @return 轮播图列表（包含图片地址、跳转链接、排序）
     */
    @GetMapping("/list")
    public Result<List<BannerEntity>> listHomeBanner() {
        List<BannerEntity> bannerList = mobileBannerService.listHomeBanner();
        return Result.success(bannerList);
    }
}