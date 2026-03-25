package com.anime.shop.admin.service;

import com.anime.shop.admin.controller.dto.ticket.TicketVerifyDTO;
import com.anime.shop.admin.controller.dto.ticket.TicketVerifyLogQueryDTO;
import com.anime.shop.entity.TicketVerifyLogEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface AdminTicketVerifyService {
    // 执行票务核销
    boolean verifyTicket(TicketVerifyDTO dto);

    // 分页查询核销日志
    IPage<TicketVerifyLogEntity> getVerifyLogPage(TicketVerifyLogQueryDTO query);
}
