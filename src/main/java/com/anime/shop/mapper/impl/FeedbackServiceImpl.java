package com.anime.shop.mapper.impl;

import com.anime.shop.admin.controller.dto.feedback.FeedbackAuditDTO;
import com.anime.shop.admin.controller.dto.feedback.FeedbackPageDTO;
import com.anime.shop.common.BizException;
import com.anime.shop.common.ResultCode;
import com.anime.shop.controller.dto.feedback.*;
import com.anime.shop.entity.FeedbackEntity;
import com.anime.shop.entity.FeedbackImageEntity;
import com.anime.shop.entity.FeedbackReplyEntity;
import com.anime.shop.mapper.FeedbackImageMapper;
import com.anime.shop.mapper.FeedbackMapper;
import com.anime.shop.mapper.FeedbackReplyMapper;
import com.anime.shop.service.FeedbackService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, FeedbackEntity> implements FeedbackService {

    @Resource
    private FeedbackImageMapper feedbackImageMapper;

    @Resource
    private FeedbackReplyMapper feedbackReplyMapper;

    /**
     * 添加工单回复（复用该方法作为Admin回复工单的核心逻辑）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFeedbackReply(FeedbackReplyDTO dto) {
        // 1. 校验参数
        if (dto.getFeedbackId() == null || dto.getUserId() == null || dto.getIsAdmin() == null) {
            throw new BizException(ResultCode.PARAM_ERROR, "工单ID、回复人ID、回复类型不能为空");
        }

        // 2. 校验工单是否存在
        FeedbackEntity feedback = baseMapper.selectById(dto.getFeedbackId());
        if (feedback == null || feedback.getIsDelete() == 1) {
            throw new BizException(ResultCode.NOT_FOUND, "工单不存在");
        }

        // 3. 校验用户权限（普通用户只能回复自己的工单）
        if (dto.getIsAdmin() == 0 && !feedback.getUserId().equals(dto.getUserId())) {
            throw new BizException(ResultCode.NO_PERMISSION, "无权限回复该工单");
        }

        // 4. 已解决/驳回/关闭的工单不允许回复（Admin端新增校验）
        if (List.of(2, 3, 4).contains(feedback.getStatus())) {
            throw new BizException(ResultCode.PARAM_ERROR, "该工单已完成审核，不允许回复");
        }

        // 5. 保存回复
        FeedbackReplyEntity reply = new FeedbackReplyEntity();
        reply.setFeedbackId(dto.getFeedbackId());
        reply.setUserId(dto.getUserId());
        reply.setContent(dto.getContent().trim());
        reply.setIsAdmin(dto.getIsAdmin());
        reply.setReplyer(dto.getReplyer());
        reply.setCreateTime(LocalDateTime.now());
        reply.setIsDelete(0);
        feedbackReplyMapper.insert(reply);

        // 6. 管理员回复时，更新工单状态为「审核中」
        if (dto.getIsAdmin() == 1) {
            feedback.setStatus(1); // 1=审核中
            feedback.setUpdateTime(LocalDateTime.now());
            baseMapper.updateById(feedback);
        }
    }

    /**
     * 根据工单ID查询回复列表
     */
    @Override
    public List<FeedbackReplyVO> getFeedbackReplyList(Long feedbackId) {
        if (feedbackId == null) {
            throw new BizException(ResultCode.PARAM_ERROR, "工单ID不能为空");
        }

        // 查询回复列表
        List<FeedbackReplyEntity> replyList = feedbackReplyMapper.selectByFeedbackId(feedbackId);
        if (CollectionUtils.isEmpty(replyList)) {
            return List.of(); // 返回空列表
        }

        // 转换为VO
        return replyList.stream().map(reply -> {
            FeedbackReplyVO vo = new FeedbackReplyVO();
            vo.setId(reply.getId());
            vo.setFeedbackId(reply.getFeedbackId());
            vo.setContent(reply.getContent());
            vo.setIsAdmin(reply.getIsAdmin());
            vo.setReplyer(reply.getReplyer());
            vo.setCreateTime(reply.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 移动端提交反馈
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFeedback(FeedbackAddDTO dto) {
        // 1. 校验参数
        if (dto.getUserId() == null || dto.getCreator() == null || dto.getFeedbackContent() == null) {
            throw new BizException(ResultCode.PARAM_ERROR, "反馈内容、用户ID、创建人不能为空");
        }

        // 2. 保存工单主表
        FeedbackEntity feedback = new FeedbackEntity();
        feedback.setUserId(dto.getUserId());
        feedback.setFeedbackContent(dto.getFeedbackContent());
        feedback.setCreator(dto.getCreator());
        feedback.setStatus(0); // 初始状态：待审核
        feedback.setIsDelete(0);
        baseMapper.insert(feedback);

        // 3. 保存图片（如有）
        if (!CollectionUtils.isEmpty(dto.getImageUrls())) {
            List<FeedbackImageEntity> imageList = IntStream.range(0, dto.getImageUrls().size())
                    .mapToObj(index -> {
                        String url = dto.getImageUrls().get(index);
                        FeedbackImageEntity image = new FeedbackImageEntity();
                        image.setFeedbackId(feedback.getId());
                        image.setImageUrl(url);
                        image.setSort(index + 1); // 排序从1开始
                        return image;
                    }).collect(Collectors.toList());
            feedbackImageMapper.insertBatchSomeColumn(imageList);
        }
    }

    /**
     * 用户查询工单列表
     */
    @Override
    public IPage<FeedbackListVO> getUserFeedbackList(Long userId, Integer status, Integer page, Integer size) {
        if (userId == null) {
            throw new BizException(ResultCode.PARAM_ERROR, "用户ID不能为空");
        }
        // 2. 分页参数兜底（避免page/size为null）
        if (page == null || page < 1) page = 1;
        if (size == null || size < 1 || size > 50) size = 10;

        Page<FeedbackEntity> pageParam = new Page<>(page, size);
        IPage<FeedbackEntity> feedbackPage = baseMapper.selectUserPage(pageParam, userId, status);

        // 3. 转换VO时判空（避免feedback为null）
        return feedbackPage.convert(feedback -> {
            if (feedback == null) return null; // 兜底
            FeedbackListVO vo = new FeedbackListVO();
            vo.setId(feedback.getId());
            vo.setFeedbackContent(feedback.getFeedbackContent());
            vo.setStatus(feedback.getStatus());
            vo.setStatusDesc(getStatusDesc(feedback.getStatus()));
            vo.setCreateTime(feedback.getCreateTime());

            // 查询图片列表（补充判空）
            List<FeedbackImageEntity> imageList = feedbackImageMapper.selectByFeedbackId(feedback.getId());
            if (!CollectionUtils.isEmpty(imageList)) {
                vo.setImageUrls(imageList.stream()
                        .map(FeedbackImageEntity::getImageUrl)
                        .collect(Collectors.toList()));
            }
            return vo;
        });
    }

    /**
     * 用户查询工单详情
     */
    @Override
    public FeedbackDetailVO getFeedbackDetail(Long id, Long userId) {
        // 1. 校验参数
        if (id == null || userId == null) {
            throw new BizException(ResultCode.PARAM_ERROR, "工单ID和用户ID不能为空");
        }

        // 2. 查询工单主信息
        FeedbackEntity feedback = baseMapper.selectById(id);
        if (feedback == null || feedback.getIsDelete() == 1) {
            throw new BizException(ResultCode.NOT_FOUND, "工单不存在");
        }

        // 3. 校验权限（只能查自己的工单）
        if (!feedback.getUserId().equals(userId)) {
            throw new BizException(ResultCode.NO_PERMISSION, "无权限查看该工单");
        }

        // 4. 转换为VO
        return buildFeedbackDetailVO(feedback);
    }

    /**
     * 新增：Admin端查询工单详情（无需用户ID校验，可查看所有工单）
     */
    @Override
    public FeedbackDetailVO getAdminFeedbackDetail(Long id) {
        // 1. 校验参数
        if (id == null) {
            throw new BizException(ResultCode.PARAM_ERROR, "工单ID不能为空");
        }

        // 2. 查询工单主信息
        FeedbackEntity feedback = baseMapper.selectById(id);
        if (feedback == null || feedback.getIsDelete() == 1) {
            throw new BizException(ResultCode.NOT_FOUND, "工单不存在");
        }

        // 3. 构建VO（复用移动端的VO结构，包含图片、状态描述等）
        FeedbackDetailVO vo = new FeedbackDetailVO();
        vo.setId(feedback.getId());
        vo.setFeedbackContent(feedback.getFeedbackContent());
        vo.setStatus(feedback.getStatus());
        vo.setStatusDesc(getStatusDesc(feedback.getStatus()));
        vo.setReplyContent(feedback.getReplyContent() == null ? "" : feedback.getReplyContent());
        vo.setCreateTime(feedback.getCreateTime());
        vo.setAuditTime(feedback.getAuditTime());

        // 4. 查询图片（从FeedbackImageEntity表获取）
        List<FeedbackImageEntity> imageList = feedbackImageMapper.selectByFeedbackId(feedback.getId());
        if (!CollectionUtils.isEmpty(imageList)) {
            vo.setImageUrls(imageList.stream()
                    .map(FeedbackImageEntity::getImageUrl)
                    .collect(Collectors.toList()));
        }
        return vo;
    }

    /**
     * 后台分页查询工单（原有逻辑保留，补充时间筛选适配）
     */
    @Override
    public IPage<FeedbackEntity> getAdminFeedbackPage(FeedbackPageDTO dto) {
        // 校验分页参数
        if (dto.getPage() == null || dto.getPage() < 1) {
            dto.setPage(1);
        }
        if (dto.getSize() == null || dto.getSize() < 1 || dto.getSize() > 50) {
            dto.setSize(10);
        }

        Page<FeedbackEntity> pageParam = new Page<>(dto.getPage(), dto.getSize());
        // 适配时间筛选：如果Mapper的selectAdminPage支持时间参数则传递，否则忽略（根据实际Mapper调整）
        return baseMapper.selectAdminPage(
                pageParam,
                dto.getStatus(),
                dto.getUserId(),
                dto.getKeyword(),
                dto.getStartTime(),
                dto.getEndTime()
        );
    }

    /**
     * 后台审核工单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditFeedback(FeedbackAuditDTO dto) {
        // 1. 校验参数
        if (dto.getId() == null || dto.getStatus() == null || dto.getAuditor() == null) {
            throw new BizException(ResultCode.PARAM_ERROR, "工单ID、审核状态、审核人不能为空");
        }

        // 2. 查询工单是否存在
        FeedbackEntity feedback = baseMapper.selectById(dto.getId());
        if (feedback == null || feedback.getIsDelete() == 1) {
            throw new BizException(ResultCode.NOT_FOUND, "工单不存在");
        }

        // 3. 已完成审核的工单不允许重复操作（新增校验）
        if (List.of(2, 3, 4).contains(feedback.getStatus())) {
            throw new BizException(ResultCode.PARAM_ERROR, "该工单已完成审核，不允许重复操作");
        }

        // 4. 更新工单状态和回复
        UpdateWrapper<FeedbackEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", dto.getId())
                .set("status", dto.getStatus())
                .set("reply_content", dto.getReplyContent() == null ? "" : dto.getReplyContent().trim())
                .set("auditor", dto.getAuditor())
                .set("audit_time", LocalDateTime.now())
                .set("update_time", LocalDateTime.now());
        int updateCount = baseMapper.update(null, updateWrapper);
        if (updateCount == 0) {
            throw new BizException(ResultCode.SERVER_ERROR, "工单审核更新失败");
        }
    }

    /**
     * 辅助方法：构建工单详情VO（复用用户端和Admin端）
     */
    private FeedbackDetailVO buildFeedbackDetailVO(FeedbackEntity feedback) {
        FeedbackDetailVO vo = new FeedbackDetailVO();
        vo.setId(feedback.getId());
        vo.setFeedbackContent(feedback.getFeedbackContent());
        vo.setStatus(feedback.getStatus());
        vo.setStatusDesc(getStatusDesc(feedback.getStatus()));
        vo.setReplyContent(feedback.getReplyContent() == null ? "" : feedback.getReplyContent());
        vo.setCreateTime(feedback.getCreateTime());
        vo.setAuditTime(feedback.getAuditTime());

        // 查询图片（从FeedbackImageEntity表获取）
        List<FeedbackImageEntity> imageList = feedbackImageMapper.selectByFeedbackId(feedback.getId());
        if (!CollectionUtils.isEmpty(imageList)) {
            vo.setImageUrls(imageList.stream()
                    .map(FeedbackImageEntity::getImageUrl)
                    .collect(Collectors.toList()));
        }
        return vo;
    }

    /**
     * 状态码转描述
     */
    private String getStatusDesc(Integer status) {
        if (status == null) {
            return "未知状态";
        }
        return switch (status) {
            case 0 -> "待审核";
            case 1 -> "审核中";
            case 2 -> "已解决";
            case 3 -> "已驳回";
            case 4 -> "已关闭";
            default -> "未知状态";
        };
    }
}