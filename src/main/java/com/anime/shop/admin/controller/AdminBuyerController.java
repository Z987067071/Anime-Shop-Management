package com.anime.shop.admin.controller;

import com.anime.shop.common.Result;
import com.anime.shop.controller.dto.buyer.BuyerAuditDTO;
import com.anime.shop.controller.dto.buyer.BuyerVO;
import com.anime.shop.service.BuyerInfoService;
import com.anime.shop.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/buyer")
public class AdminBuyerController {
    @Autowired
    private BuyerInfoService buyerInfoService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/list")
    public Result<List<BuyerVO>> getBuyerList(
            @RequestParam(required = false) Integer auditStatus // 非必填，不传返回全部
    ) {
        List<BuyerVO> list;
        if (auditStatus == null) {
            // 不传状态 → 返回全部
            list = buyerInfoService.getAllBuyerList();
        } else {
            // 传状态 → 返回对应状态数据
            list = buyerInfoService.getBuyerListByStatus(auditStatus);
        }
        return Result.success(list);
    }

    // 审核购票人
    @PostMapping("/audit")
    public Result<?> auditBuyer(@Valid @RequestBody BuyerAuditDTO dto, HttpServletRequest request) {
        Long adminId = jwtUtil.getUserIdFromToken(request.getHeader("token"));
        buyerInfoService.auditBuyer(dto, adminId);
        String msg = dto.getAuditStatus() == 1 ? "审核通过" : "审核驳回";
        return Result.success(msg);
    }
}
