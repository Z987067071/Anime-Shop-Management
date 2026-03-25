package com.anime.shop.controller;

import com.anime.shop.common.Result;
import com.anime.shop.controller.dto.order.POrderItem;
import com.anime.shop.controller.dto.order.TicketVerifyVO;
import com.anime.shop.controller.dto.order.UserComicConTicketVO;
import com.anime.shop.mapper.POrderItemMapper;
import com.anime.shop.service.OrderService;
import com.anime.shop.util.JwtUtil;
import com.anime.shop.util.QrCodeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private JwtUtil jwtUtil;
    @Resource
    private POrderItemMapper pOrderItemMapper;

    /**
     * 查询当前用户的漫展票务订单（适配拆分后的订单项，显示多张票的核销码）
     */
    @GetMapping("/ticket")
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

        // 4. 查询用户的漫展票务订单
        List<UserComicConTicketVO> ticketList = orderService.getUserComicConTickets(userId);
        if (ticketList.isEmpty()) {
            return Result.success(ticketList);
        }

        // 5. 查询订单对应的所有订单项（拆分后的，一张票一条记录）
        List<Long> orderIds = ticketList.stream().map(UserComicConTicketVO::getOrderId).collect(Collectors.toList());
        LambdaQueryWrapper<POrderItem> query = new LambdaQueryWrapper<>();
        query.in(POrderItem::getOrderId, orderIds)
                .eq(POrderItem::getIsDelete, 0)
                // 补充查询购票人信息（可选，票夹可显示购票人）
                .select(POrderItem::getOrderId, POrderItem::getProductImg, POrderItem::getQuantity,
                        POrderItem::getTicketType, POrderItem::getBuyerName, POrderItem::getBuyerIdCard);

        List<POrderItem> orderItems = pOrderItemMapper.selectList(query);

        // 6. 重构映射：订单ID -> 该订单下的所有订单项列表（关键修复）
        Map<Long, List<POrderItem>> orderItemListMap = orderItems.stream()
                .collect(Collectors.groupingBy(POrderItem::getOrderId));

        // 7. 遍历订单，生成每张票的核销码（适配拆分后的订单项）
        for (UserComicConTicketVO ticket : ticketList) {
            List<POrderItem> itemList = orderItemListMap.get(ticket.getOrderId());
            if (itemList == null || itemList.isEmpty()) continue;

            // 7.1 取第一个订单项的基础信息（商品图片、票种）
            POrderItem firstItem = itemList.get(0);
            ticket.setComicConBanner(firstItem.getProductImg() != null ? firstItem.getProductImg() : "https://www.helloimg.com/i/2026/01/27/69783066c0b56.png");
            ticket.setTicketCount(itemList.size()); // 总票数=订单项数量（关键！）
            ticket.setTicketType(firstItem.getTicketType() != null ? firstItem.getTicketType() : "普通票");

            // 7.2 按订单项数量生成核销码（每张票对应一个独立核销码）
            List<TicketVerifyVO> verifyList = new ArrayList<>();
            String baseVerifyCode = ticket.getVerifyCode(); // 原核销码
            for (int i = 0; i < itemList.size(); i++) {
                POrderItem item = itemList.get(i);
                TicketVerifyVO vo = new TicketVerifyVO();

                // 生成独立核销码（可选：拼接购票人ID/订单项ID，更唯一）
                String singleVerifyCode = baseVerifyCode + "-" + String.format("%02d", i + 1);
                vo.setVerifyCode(singleVerifyCode);
                // 生成二维码
                vo.setVerifyQrCodeUrl(QrCodeUtils.generateVerifyQrCode(singleVerifyCode));
                vo.setIsVerified(false); // 默认未核销

                // 可选：显示购票人信息（提升体验）
                vo.setBuyerName(item.getBuyerName());
                vo.setBuyerIdCard(item.getBuyerIdCard() != null ? maskIdCard(item.getBuyerIdCard()) : "");

                verifyList.add(vo);
            }
            ticket.setTicketVerifyList(verifyList);
        }

        return Result.success(ticketList);
    }

    /**
     * 身份证脱敏（可选，保护隐私）
     */
    private String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() != 18) {
            return idCard;
        }
        return idCard.substring(0, 6) + "********" + idCard.substring(14);
    }
}