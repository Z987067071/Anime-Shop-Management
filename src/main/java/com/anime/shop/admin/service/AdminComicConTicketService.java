package com.anime.shop.admin.service;

import com.anime.shop.entity.ComicConTicket;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface AdminComicConTicketService extends IService<ComicConTicket> {
    // 分页查询票种（支持漫展ID/票种名称筛选）
    IPage<ComicConTicket> pageTicket(Integer pageNum, Integer pageSize, Long comicConId, String ticketName);

    // 根据漫展ID查询所有票种
    List<ComicConTicket> listByComicConId(Long comicConId);

    // 修改票种状态（兼容1=正常/0=下架/2=售罄）
    boolean updateTicketStatus(Long id, Integer status);
}
