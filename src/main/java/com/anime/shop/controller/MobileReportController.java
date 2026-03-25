package com.anime.shop.controller;

import com.anime.shop.common.Result;
import com.anime.shop.controller.dto.report.ReportSubmitDTO;
import com.anime.shop.service.PReportService;
import com.anime.shop.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 移动端举报控制器
 * 仅处理用户举报提交（商品评论/社区帖子/社区评论）
 */
@Slf4j
@RestController
@RequestMapping("/api/mobile/report")
@RequiredArgsConstructor
public class MobileReportController {

    private final PReportService pReportService;
    private final JwtUtil jwtUtil;

    /**
     * 移动端提交举报（通用：商品评论/社区等）
     */
    @PostMapping("/submit")
    public Result<String> submitReport(
            @Validated @RequestBody ReportSubmitDTO dto,
            @RequestHeader("token") String token) {
        try {
            // 1. Token校验
            if (!jwtUtil.validateToken(token)) {
                return Result.error(401, "登录失效，请重新登录！");
            }

            // 2. 获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return Result.error(401, "无法获取用户信息！");
            }

            // 3. 提交举报（移除isSuccess()，直接返回结果）
            Result<String> result = pReportService.submitReport(dto, userId);
            log.info("用户{}提交举报，类型：{}，目标ID：{}", userId, dto.getTargetType(), dto.getTargetId());
            return result;
        } catch (Exception e) {
            log.error("移动端提交举报异常：", e);
            return Result.error(500, "举报提交失败，请稍后重试！");
        }
    }
}