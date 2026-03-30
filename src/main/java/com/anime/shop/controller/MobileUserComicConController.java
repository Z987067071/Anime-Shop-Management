package com.anime.shop.controller;

import com.anime.shop.common.Result;
import com.anime.shop.controller.dto.order.POrderItem;
import com.anime.shop.controller.dto.order.TicketVerifyVO;
import com.anime.shop.controller.dto.order.UserComicConTicketVO;
import com.anime.shop.mapper.POrderItemMapper;
import com.anime.shop.service.OrderService;
import com.anime.shop.util.QrCodeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mobile/user/comic-con")
public class MobileUserComicConController {

    @Resource
    private OrderService orderService;
    @Resource
    private POrderItemMapper pOrderItemMapper;

    /**
     * 查询当前用户的漫展票务订单（userId 由拦截器注入）
     */
    @GetMapping("/ticket")
    public Result<List<UserComicConTicketVO>> getUserComicConTickets(@RequestAttribute Long userId) {
        List<UserComicConTicketVO> ticketList = orderService.getUserComicConTickets(userId);
        if (ticketList.isEmpty()) {
            return Result.success(ticketList);
        }

        List<Long> orderIds = ticketList.stream().map(UserComicConTicketVO::getOrderId).collect(Collectors.toList());
        LambdaQueryWrapper<POrderItem> query = new LambdaQueryWrapper<POrderItem>()
                .in(POrderItem::getOrderId, orderIds)
                .eq(POrderItem::getIsDelete, 0)
                .select(POrderItem::getOrderId, POrderItem::getProductImg, POrderItem::getQuantity,
                        POrderItem::getTicketType, POrderItem::getBuyerName, POrderItem::getBuyerIdCard);

        Map<Long, List<POrderItem>> orderItemMap = pOrderItemMapper.selectList(query).stream()
                .collect(Collectors.groupingBy(POrderItem::getOrderId));

        for (UserComicConTicketVO ticket : ticketList) {
            List<POrderItem> items = orderItemMap.get(ticket.getOrderId());
            if (items == null || items.isEmpty()) continue;

            POrderItem first = items.get(0);
            ticket.setComicConBanner(first.getProductImg() != null ? first.getProductImg()
                    : "https://www.helloimg.com/i/2026/01/27/69783066c0b56.png");
            ticket.setTicketCount(items.size());
            ticket.setTicketType(first.getTicketType() != null ? first.getTicketType() : "普通票");

            List<TicketVerifyVO> verifyList = new ArrayList<>();
            String baseCode = ticket.getVerifyCode();
            for (int i = 0; i < items.size(); i++) {
                POrderItem item = items.get(i);
                TicketVerifyVO vo = new TicketVerifyVO();
                String code = baseCode + "-" + String.format("%02d", i + 1);
                vo.setVerifyCode(code);
                vo.setVerifyQrCodeUrl(QrCodeUtils.generateVerifyQrCode(code));
                vo.setIsVerified(false);
                vo.setBuyerName(item.getBuyerName());
                vo.setBuyerIdCard(item.getBuyerIdCard() != null ? maskIdCard(item.getBuyerIdCard()) : "");
                verifyList.add(vo);
            }
            ticket.setTicketVerifyList(verifyList);
        }

        return Result.success(ticketList);
    }

    private String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() != 18) return idCard;
        return idCard.substring(0, 6) + "********" + idCard.substring(14);
    }
}
