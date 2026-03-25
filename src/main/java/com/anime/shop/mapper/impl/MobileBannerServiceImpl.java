package com.anime.shop.mapper.impl;

import com.anime.shop.controller.dto.BannerEntity;
import com.anime.shop.mapper.BannerMapper;
import com.anime.shop.service.MobileBannerService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MobileBannerServiceImpl implements MobileBannerService {

    @Resource
    private BannerMapper bannerMapper;

    @Override
    public List<BannerEntity> listHomeBanner() {
        // 构建查询条件：仅显示状态为1的轮播图 + 按sort升序（数值小的靠前）
        LambdaQueryWrapper<BannerEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BannerEntity::getStatus, 1) // 仅展示启用的轮播图
                .orderByAsc(BannerEntity::getSort); // 排序值小的排在前面

        // 查询并返回结果
        return bannerMapper.selectList(wrapper);
    }
}
