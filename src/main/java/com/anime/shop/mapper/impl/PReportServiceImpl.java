package com.anime.shop.mapper.impl;

import com.anime.shop.common.Result;
import com.anime.shop.common.ResultCode;
import com.anime.shop.controller.dto.report.ReportHandleDTO;
import com.anime.shop.controller.dto.report.ReportSubmitDTO;
import com.anime.shop.entity.ProductReportEntity;
import com.anime.shop.enums.ReportEnums;
import com.anime.shop.mapper.PReportMapper;
import com.anime.shop.service.PReportService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 举报服务实现
 */
@Slf4j
@Service
public class PReportServiceImpl extends ServiceImpl<PReportMapper, ProductReportEntity> implements PReportService {

    // 时间格式化器
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Result<String> submitReport(ReportSubmitDTO dto, Long userId) {
        try {
            // 1. 校验重复举报
            if (checkRepeatReport(userId, dto.getTargetType(), dto.getTargetId())) {
                return Result.error(ResultCode.PARAM_ERROR.getCode(), "您已举报过该内容，1小时内请勿重复举报！");
            }

            // 2. 校验自定义理由（仅reason=99时必填）
            if (dto.getReportReason().equals(99) && (dto.getCustomReason() == null || dto.getCustomReason().trim().isEmpty())) {
                return Result.error(ResultCode.PARAM_ERROR.getCode(), "选择“其他”理由时，必须填写自定义举报理由！");
            }

            // 3. 构建举报实体
            ProductReportEntity report = new ProductReportEntity();
            report.setTargetType(dto.getTargetType());
            report.setTargetId(dto.getTargetId());
            report.setUserId(userId);
            report.setReportReason(dto.getReportReason());
            report.setCustomReason(dto.getCustomReason() == null ? "" : dto.getCustomReason().trim());
            report.setStatus(0); // 初始状态：待处理
            report.setHandlerId(0L); // 初始处理人ID为0

            // 4. 保存举报记录
            boolean saveSuccess = this.save(report);
            if (!saveSuccess) {
                return Result.error(ResultCode.SERVER_ERROR.getCode(), "举报提交失败，请重试！");
            }

            return Result.ok("举报提交成功，我们会尽快处理！");
        } catch (Exception e) {
            log.error("提交举报失败：", e);
            return Result.error(ResultCode.SERVER_ERROR.getCode(), "举报提交失败：" + e.getMessage());
        }
    }

    @Override
    public Result<String> handleReport(Long reportId, ReportHandleDTO dto, Long handlerId) {
        try {
            // 1. 校验举报记录是否存在
            ProductReportEntity report = this.getById(reportId);
            if (report == null) {
                return Result.error(ResultCode.PARAM_ERROR.getCode(), "举报记录不存在！");
            }

            // 2. 校验处理状态是否合法
            ReportEnums.ReportStatus statusEnum = ReportEnums.ReportStatus.getByCode(dto.getStatus());
            if (statusEnum == null) {
                return Result.error(ResultCode.PARAM_ERROR.getCode(), "处理状态只能是1（已受理）、2（已驳回）、3（已处理）！");
            }

            // 3. 更新举报处理信息
            report.setStatus(dto.getStatus());
            report.setHandlerId(handlerId);
            report.setHandleTime(LocalDateTime.now());
            report.setHandleNote(dto.getHandleNote() == null ? "" : dto.getHandleNote().trim());

            // 4. 保存更新
            boolean updateSuccess = this.updateById(report);
            if (!updateSuccess) {
                return Result.error(ResultCode.SERVER_ERROR.getCode(), "举报处理失败，请重试！");
            }

            return Result.ok("举报处理成功！");
        } catch (Exception e) {
            log.error("处理举报失败：", e);
            return Result.error(ResultCode.SERVER_ERROR.getCode(), "举报处理失败：" + e.getMessage());
        }
    }

    @Override
    public Result<Page<ProductReportEntity>> getReportList(Integer pageNum, Integer pageSize,Integer id, Integer targetType, Integer status, String startTime, String endTime) {
        try {
            // 1. 构建分页对象
            Page<ProductReportEntity> page = new Page<>(pageNum, pageSize);

            // 2. 构建查询条件
            LambdaQueryWrapper<ProductReportEntity> wrapper = new LambdaQueryWrapper<>();

            if (id != null && id > 0) {
                wrapper.eq(ProductReportEntity::getId, id);
            }

            // 按目标类型筛选
            if (targetType != null && targetType >= 1 && targetType <= 4) {
                wrapper.eq(ProductReportEntity::getTargetType, targetType);
            }

            // 按处理状态筛选
            if (status != null && status >= 0 && status <= 3) {
                wrapper.eq(ProductReportEntity::getStatus, status);
            }

            // 按时间范围筛选
            if (startTime != null && !startTime.trim().isEmpty()) {
                wrapper.ge(ProductReportEntity::getCreateTime, LocalDateTime.parse(startTime, DATE_FORMATTER));
            }
            if (endTime != null && !endTime.trim().isEmpty()) {
                wrapper.le(ProductReportEntity::getCreateTime, LocalDateTime.parse(endTime, DATE_FORMATTER));
            }

            // 按举报时间倒序
            wrapper.orderByDesc(ProductReportEntity::getCreateTime);

            // 3. 分页查询
            Page<ProductReportEntity> reportPage = this.page(page, wrapper);

            return Result.ok(reportPage);
        } catch (Exception e) {
            log.error("查询举报列表失败：", e);
            return Result.error(ResultCode.SERVER_ERROR.getCode(), "查询举报列表失败：" + e.getMessage());
        }
    }

    @Override
    public boolean checkRepeatReport(Long userId, Integer targetType, Long targetId) {
        // 同一用户对同一目标1小时内只能举报一次
        LambdaQueryWrapper<ProductReportEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductReportEntity::getUserId, userId)
                .eq(ProductReportEntity::getTargetType, targetType)
                .eq(ProductReportEntity::getTargetId, targetId)
                .ge(ProductReportEntity::getCreateTime, LocalDateTime.now().minusHours(1));

        return this.count(wrapper) > 0;
    }
}