package com.anime.shop.service;

import com.anime.shop.controller.dto.BannerEntity;

import java.util.List;

public interface MobileBannerService {

    /**
     * 查询首页展示的轮播图（状态=1，按sort升序）
     * @return 轮播图列表（仅展示状态，按排序值从小到大）
     */
    List<BannerEntity> listHomeBanner();
}
