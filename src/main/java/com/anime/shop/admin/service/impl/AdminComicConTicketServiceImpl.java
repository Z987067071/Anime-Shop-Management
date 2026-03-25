package com.anime.shop.admin.service.impl;

import com.anime.shop.admin.mapper.comic.ComicConTicketMapper;
import com.anime.shop.admin.service.AdminComicConTicketService;
import com.anime.shop.entity.ComicConTicket;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class AdminComicConTicketServiceImpl extends ServiceImpl<ComicConTicketMapper, ComicConTicket> implements AdminComicConTicketService {

    @Override
    public IPage<ComicConTicket> pageTicket(Integer pageNum, Integer pageSize, Long comicConId, String ticketName) {
        Page<ComicConTicket> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ComicConTicket> wrapper = new LambdaQueryWrapper<>();

        // 按漫展ID筛选（核心）
        if (comicConId != null) {
            wrapper.eq(ComicConTicket::getComicConId, comicConId);
        }
        // 票种名称模糊查询
        if (StringUtils.hasText(ticketName)) {
            wrapper.like(ComicConTicket::getTicketName, ticketName);
        }
        // 按更新时间倒序
        wrapper.orderByDesc(ComicConTicket::getUpdateTime);

        return this.page(page, wrapper);
    }

    @Override
    public List<ComicConTicket> listByComicConId(Long comicConId) {
        LambdaQueryWrapper<ComicConTicket> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ComicConTicket::getComicConId, comicConId);
        wrapper.orderByDesc(ComicConTicket::getUpdateTime);
        return this.list(wrapper);
    }

    @Override
    public boolean updateTicketStatus(Long id, Integer status) {
        // 校验状态有效值（1=正常/0=下架/2=售罄）
        if (id == null || status == null || (status != 0 && status != 1 && status != 2)) {
            return false;
        }
        ComicConTicket ticket = new ComicConTicket();
        ticket.setId(id);
        ticket.setStatus(status);
        return this.updateById(ticket);
    }
}
