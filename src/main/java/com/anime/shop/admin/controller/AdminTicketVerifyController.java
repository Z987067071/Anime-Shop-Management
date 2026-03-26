package com.anime.shop.admin.controller;

import com.anime.shop.admin.controller.dto.ticket.TicketVerifyDTO;
import com.anime.shop.admin.controller.dto.ticket.TicketVerifyLogQueryDTO;
import com.anime.shop.admin.service.AdminTicketVerifyService;
import com.anime.shop.common.Result;
import com.anime.shop.entity.TicketVerifyLogEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/ticket")
@Validated
public class AdminTicketVerifyController {

    @Resource
    private AdminTicketVerifyService adminTicketVerifyService;

    /**
     * 票务核销接口（核心）
     * POST /api/admin/ticket/verify
     */
    @PostMapping("/verify")
    public Result<Boolean> verifyTicket(@Valid @RequestBody TicketVerifyDTO dto) {
        boolean success = adminTicketVerifyService.verifyTicket(dto);
        if (success) {
            return Result.success(true);
        } else {
            return Result.fail("核销失败，核销码无效或已核销");
        }
    }

    /**
     * 分页查询核销日志
     * GET /api/admin/ticket/verifyLog/page
     */
    @GetMapping("/verifyLog/page")
    public Result<IPage<TicketVerifyLogEntity>> getVerifyLogPage(TicketVerifyLogQueryDTO query) {
        IPage<TicketVerifyLogEntity> page = adminTicketVerifyService.getVerifyLogPage(query);
        return Result.success(page);
    }
}
