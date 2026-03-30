package com.anime.shop.util;

import com.anime.shop.common.BizException;
import com.anime.shop.common.ResultCode;
import com.anime.shop.entity.UserEntity;
import com.anime.shop.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserMapper userMapper;

    /**
     * 可选认证路径：有 token 就解析 userId，没有 token 也放行（userId 为 null）
     */
    private static final String[] OPTIONAL_AUTH_PATHS = {
            "/api/mobile/product/detail/",
            "/api/mobile/product/list",
            "/api/mobile/product/first-category/",
            "/api/mobile/product/category/",
            "/api/mobile/product/tag/",
            "/api/mobile/product/recommend",
            "/api/mobile/product/search",
            "/api/mobile/product/check-status",
            "/api/mobile/community/post/list",
            "/api/mobile/community/post/detail/",
            "/api/mobile/community/comment/list",
            "/api/mobile/product/comment/list",
            "/api/mobile/product/comment/count/",
            "/api/mobile/banner/list",
            "/api/mobile/category/group/list"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String token = extractToken(request);

        // 可选认证路径：有 token 就解析，没有也放行
        for (String path : OPTIONAL_AUTH_PATHS) {
            if (uri.startsWith(path) || uri.equals(path.stripTrailing())) {
                if (token != null && jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
                    request.setAttribute("userId", jwtUtil.getUserIdFromToken(token));
                    request.setAttribute("role", jwtUtil.getRoleFromToken(token));
                }
                return true;
            }
        }

        // 强制认证路径
        if (token == null) {
            throw new BizException(ResultCode.NO_TOKEN);
        }
        if (!jwtUtil.validateToken(token)) {
            throw new BizException(ResultCode.TOKEN_INVALID);
        }
        if (jwtUtil.isTokenExpired(token)) {
            throw new BizException(ResultCode.TOKEN_EXPIRED);
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);

        // 校验账户是否被禁用
        UserEntity user = userMapper.selectById(userId);
        if (user == null || (user.getStatus() != null && user.getStatus() == 0)) {
            throw new BizException(ResultCode.USER_DISABLED);
        }

        if (uri.startsWith("/api/admin")) {
            if (role == null || (!"admin".equals(role) && !"manager".equals(role)
                    && !"leader".equals(role) && !"member".equals(role))) {
                throw new BizException(ResultCode.NO_PERMISSION);
            }
        }

        request.setAttribute("userId", userId);
        request.setAttribute("role", role);
        return true;
    }

    /**
     * 兼容两种 token 传递方式：
     * 1. header: token: xxx
     * 2. header: Authorization: Bearer xxx
     */
    public static String extractToken(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (token != null && !token.isEmpty()) {
            return token;
        }
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
