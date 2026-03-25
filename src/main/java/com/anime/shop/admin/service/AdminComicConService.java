package com.anime.shop.admin.service;

import com.anime.shop.admin.controller.dto.comic.ComicConVO;
import com.anime.shop.entity.ComicCon;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface AdminComicConService extends IService<ComicCon> {
    IPage<ComicConVO> getComicConListWithProductName(Integer pageNum, Integer pageSize, String name, Long productId);
    ComicConVO getComicConVOById(Long id);
    Long getProductIdByConId(Long comicConId);
    boolean bindComicConIdToProduct(Long comicConId, Long productId);
}
