package com.anime.shop.util;

import com.anime.shop.common.BizException;
import com.anime.shop.common.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            throw new BizException(ResultCode.NO_TOKEN);
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!jwtUtil.validateToken(token)) {
            throw new BizException(ResultCode.TOKEN_INVALID);
        }

        if (jwtUtil.isTokenExpired(token)) {
            throw new BizException(ResultCode.TOKEN_EXPIRED);
        }

        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/admin")) {
            String role = jwtUtil.getRoleFromToken(token);
            if (role == null || (!"admin".equals(role) && !"manager".equals(role))) {
                throw new BizException(ResultCode.NO_PERMISSION);
            }
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        request.setAttribute("userId", userId);
        request.setAttribute("role", jwtUtil.getRoleFromToken(token));

        return true;
    }
}