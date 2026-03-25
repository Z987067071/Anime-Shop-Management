package com.anime.shop.mapper.impl;

import com.anime.shop.controller.dto.buyer.BuyerAddDTO;
import com.anime.shop.controller.dto.buyer.BuyerAuditDTO;
import com.anime.shop.controller.dto.buyer.BuyerEditDTO;
import com.anime.shop.controller.dto.buyer.BuyerVO;
import com.anime.shop.entity.BuyerInfo;
import com.anime.shop.mapper.BuyerInfoMapper;
import com.anime.shop.service.BuyerInfoService;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BuyerInfoServiceImpl extends ServiceImpl<BuyerInfoMapper, BuyerInfo> implements BuyerInfoService {
    @Autowired
    private BuyerInfoMapper buyerInfoMapper;

    @Override
    public void addBuyer(Long userId, BuyerAddDTO dto) {
        // 校验身份证是否已存在
        BuyerInfo exist = lambdaQuery()
                .eq(BuyerInfo::getIdCard, dto.getIdCard())
                .one();
        if (exist != null) {
            throw new RuntimeException("该身份证号已绑定");
        }

        // 新增购票人（默认待审核）
        BuyerInfo buyer = new BuyerInfo();
        buyer.setUserId(userId);
        buyer.setName(dto.getName());
        buyer.setIdCard(dto.getIdCard());
        buyer.setAuditStatus(0); // 0-待审核
        save(buyer);
    }

    @Override
    public List<BuyerVO> getBuyerListByUserId(Long userId) {
        List<BuyerVO> list = baseMapper.selectBuyerVOByUserId(userId);
        // 身份证脱敏：110101********1234
        list.forEach(vo -> {
            if (vo.getIdCard() != null && vo.getIdCard().length() == 18) {
                vo.setIdCard(vo.getIdCard().substring(0, 6) + "********" + vo.getIdCard().substring(14));
            }
        });
        return list;
    }

    @Override
    public BuyerVO getBuyerDetailByIdAndUserId(Long id, Long userId) {
        // 1. 查询购票人（校验归属权）
        BuyerInfo buyerInfo = lambdaQuery()
                .eq(BuyerInfo::getId, id)
                .eq(BuyerInfo::getUserId, userId)
                .one();
        if (buyerInfo == null) {
            return null;
        }
        // 2. 转换为VO（脱敏）
        BuyerVO vo = new BuyerVO();
        BeanUtils.copyProperties(buyerInfo, vo);
        // 身份证脱敏
        vo.setIdCard(desensitizeIdCard(buyerInfo.getIdCard()));
        return vo;
    }

    @Override
    public void auditBuyer(BuyerAuditDTO dto, Long adminId) {
        BuyerInfo buyer = getById(dto.getId());
        if (buyer == null) {
            throw new RuntimeException("购票人不存在");
        }
        if (buyer.getAuditStatus() != 0) {
            throw new RuntimeException("该购票人已审核，不可重复操作");
        }

        // 驳回时校验理由
        if (dto.getAuditStatus() == 2 && StringUtils.isBlank(dto.getAuditRemark())) {
            throw new RuntimeException("驳回时必须填写理由");
        }

        // 更新审核状态
        buyer.setAuditStatus(dto.getAuditStatus());
        buyer.setAuditRemark(dto.getAuditRemark());
        buyer.setAuditTime(LocalDateTime.now());
        updateById(buyer);
    }

    @Override
    public boolean editBuyer(Long id, Long userId, BuyerEditDTO dto) {
        // 1. 查询购票人（校验归属权+状态：仅未审核/驳回的可编辑）
        BuyerInfo buyerInfo = lambdaQuery()
                .eq(BuyerInfo::getId, id)
                .eq(BuyerInfo::getUserId, userId)
                .in(BuyerInfo::getAuditStatus, 0, 2) // 0-待审核 2-驳回
                .one();
        if (buyerInfo == null) {
            return false;
        }
        // 2. 校验身份证是否重复（排除自己）
        long count = lambdaQuery()
                .eq(BuyerInfo::getIdCard, dto.getIdCard())
                .ne(BuyerInfo::getId, id)
                .count();
        if (count > 0) {
            throw new RuntimeException("该身份证号已绑定其他购票人");
        }
        // 3. 更新信息（重置为待审核）
        buyerInfo.setName(dto.getName());
        buyerInfo.setIdCard(dto.getIdCard());
        buyerInfo.setAuditStatus(0); // 编辑后重新待审核
        buyerInfo.setAuditRemark(null);
        buyerInfo.setAuditTime(null);
        updateById(buyerInfo);
        return true;
    }

    @Override
    public boolean deleteBuyer(Long id, Long userId) {
        // 1. 查询购票人（校验归属权+状态：仅未审核/驳回的可删除）
        BuyerInfo buyerInfo = lambdaQuery()
                .eq(BuyerInfo::getId, id)
                .eq(BuyerInfo::getUserId, userId)
                .in(BuyerInfo::getAuditStatus, 0, 2)
                .one();
        if (buyerInfo == null) {
            return false;
        }
        // 2. 执行删除（物理删除/逻辑删除根据你的业务调整）
        removeById(id);
        return true;
    }

    // 身份证脱敏工具方法
    private String desensitizeIdCard(String idCard) {
        if (idCard == null || idCard.length() != 18) {
            return idCard;
        }
        return idCard.substring(0, 6) + "********" + idCard.substring(14);
    }

    private List<BuyerVO> convertToVOList(List<BuyerInfo> buyerList) {
        return buyerList.stream().map(buyer -> {
            BuyerVO vo = new BuyerVO();
            BeanUtils.copyProperties(buyer, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<BuyerVO> getBuyerListByStatus(Integer auditStatus) {
        List<BuyerInfo> buyerList = buyerInfoMapper.selectByAuditStatus(auditStatus);
        return convertToVOList(buyerList);
    }

    // 新增：获取全部
    @Override
    public List<BuyerVO> getAllBuyerList() {
        List<BuyerInfo> buyerList = buyerInfoMapper.selectAll();
        return convertToVOList(buyerList);
    }
}
