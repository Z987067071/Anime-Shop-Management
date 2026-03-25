package com.anime.shop.admin.controller;

import com.anime.shop.common.Result;
import com.anime.shop.controller.dto.report.ReportHandleDTO;
import com.anime.shop.entity.ProductReportEntity;
import com.anime.shop.service.PReportService;
import com.anime.shop.util.JwtUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 举报管理控制器（管理端）
 * 统一处理所有类型举报的管理操作（商品评论/社区帖子/社区评论等）
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/report")
@RequiredArgsConstructor
public class ReportController {

    private final PReportService pReportService;
    private final JwtUtil jwtUtil;

    /**
     * 管理员处理举报（兼容所有类型）
     */
    @PostMapping("/handle/{reportId}")
    public Result<String> handleReport(
            @PathVariable Long reportId,
            @Validated @RequestBody ReportHandleDTO dto,
            @RequestHeader("token") String token) {
        try {
            // 1. Token校验
            if (!jwtUtil.validateToken(token)) {
                log.warn("处理举报失败：Token无效");
                return Result.error(401, "登录状态失效，请重新登录！");
            }

            // 2. 管理员权限校验
            String role = jwtUtil.getRoleFromToken(token);
            if (role == null || !"ADMIN".equals(role.toUpperCase())) {
                log.warn("非管理员尝试处理举报，角色：{}", role);
                return Result.error(403, "无管理员权限！");
            }

            Long handlerId = jwtUtil.getUserIdFromToken(token);
            Result<String> result = pReportService.handleReport(reportId, dto, handlerId);
            log.info("管理员{}处理举报{}，状态：{}", handlerId, reportId, dto.getStatus());
            return result;
        } catch (Exception e) {
            log.error("处理举报异常：", e);
            return Result.error(500, "处理失败，请稍后重试！");
        }
    }

    /**
     * 管理员查询举报列表（支持按类型筛选：商品评论/社区等）
     */
    @GetMapping("/list")
    public Result<Page<ProductReportEntity>> getReportList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) Integer targetType, // 1=商品,2=商品评论,3=社区帖子,4=社区评论
            @RequestParam(required = false) Integer status,     // 处理状态
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestHeader("token") String token) {
        try {
            // 1. 权限校验
            if (!jwtUtil.validateToken(token)) {
                return Result.error(401, "登录状态失效，请重新登录！");
            }
            String role = jwtUtil.getRoleFromToken(token);
            if (role == null || !"ADMIN".equals(role.toUpperCase())) {
                return Result.error(403, "无管理员权限！");
            }

            // 2. 查询列表（直接返回结果，无isSuccess()）
            Result<Page<ProductReportEntity>> result = pReportService.getReportList(
                    pageNum, pageSize,id, targetType, status, startTime, endTime);
            return result;
        } catch (Exception e) {
            log.error("查询举报列表异常：", e);
            return Result.error(500, "查询失败，请稍后重试！");
        }
    }
}