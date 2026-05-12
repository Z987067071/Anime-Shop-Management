package com.anime.shop.controller;

import com.anime.shop.common.Result;
import com.anime.shop.controller.dto.order.UserComicConTicketVO;
import com.anime.shop.service.OrderService;
import com.anime.shop.util.JwtUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mobile/user/comic-con")
public class MobileUserComicConController {

    @Resource
    private OrderService orderService;
    @Resource
    private JwtUtil jwtUtil;

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

        // 4. 查询用户的漫展票务订单（ticketVerifyList已在Service层构建好）
        List<UserComicConTicketVO> ticketList = orderService.getUserComicConTickets(userId);

        return Result.success(ticketList);
    }

}