package com.anime.shop.service;

import com.anime.shop.common.Result;
import com.anime.shop.controller.dto.report.ReportHandleDTO;
import com.anime.shop.controller.dto.report.ReportSubmitDTO;
import com.anime.shop.entity.ProductReportEntity;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface PReportService extends IService<ProductReportEntity> {
    /**
     * 提交举报（通用，支持商品/评论/社区等）
     */
    Result<String> submitReport(ReportSubmitDTO dto, Long userId);

    /**
     * 管理员处理举报
     */
    Result<String> handleReport(Long reportId, ReportHandleDTO dto, Long handlerId);

    /**
     * 分页查询举报列表（支持按类型/状态/时间筛选）
     */
    Result<Page<ProductReportEntity>> getReportList(
            Integer pageNum,
            Integer pageSize,
            Integer id,
            Integer targetType,  // 可选：目标类型
            Integer status,      // 可选：处理状态
            String startTime,    // 可选：开始时间
            String endTime       // 可选：结束时间
    );

    /**
     * 校验用户是否重复举报（同一用户对同一目标1小时内只能举报一次）
     */
    boolean checkRepeatReport(Long userId, Integer targetType, Long targetId);
}
