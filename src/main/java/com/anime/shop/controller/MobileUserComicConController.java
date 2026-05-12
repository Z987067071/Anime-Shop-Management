package com.anime.shop.controller;

import com.anime.shop.common.Result;
import com.anime.shop.controller.dto.order.UserComicConTicketVO;
import com.anime.shop.service.OrderService;
<<<<<<< HEAD
import com.anime.shop.util.QrCodeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
=======
import com.anime.shop.util.JwtUtil;
>>>>>>> master
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mobile/user/comic-con")
public class MobileUserComicConController {

    @Resource
    private OrderService orderService;
    @Resource
<<<<<<< HEAD
    private POrderItemMapper pOrderItemMapper;
=======
    private JwtUtil jwtUtil;
>>>>>>> master

    /**
     * 查询当前用户的漫展票务订单（userId 由拦截器注入）
     */
    @GetMapping("/ticket")
<<<<<<< HEAD
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
=======
    public Result<List<UserComicConTicketVO>> getUserComicConTickets(HttpServletRequest request) {
        // 1. 从请求头获取Token
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
        }

        // 2. 校验Token
        if (token == null || token.isEmpty()) {
            return Result.fail("请先登录");
        }
        if (!jwtUtil.validateToken(token)) {
            return Result.fail("Token已过期或无效，请重新登录");
        }

        // 3. 解析用户ID
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.fail("Token解析失败，请重新登录");
        }

        // 4. 查询用户的漫展票务订单（ticketVerifyList已在Service层构建好）
        List<UserComicConTicketVO> ticketList = orderService.getUserComicConTickets(userId);
>>>>>>> master

        return Result.success(ticketList);
    }

<<<<<<< HEAD
    private String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() != 18) return idCard;
        return idCard.substring(0, 6) + "********" + idCard.substring(14);
    }
}
=======
}
>>>>>>> master
