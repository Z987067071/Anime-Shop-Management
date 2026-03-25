package com.anime.shop.service;

import com.anime.shop.controller.dto.buyer.BuyerAddDTO;
import com.anime.shop.controller.dto.buyer.BuyerAuditDTO;
import com.anime.shop.controller.dto.buyer.BuyerEditDTO;
import com.anime.shop.controller.dto.buyer.BuyerVO;
import com.anime.shop.entity.BuyerInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface BuyerInfoService extends IService<BuyerInfo> {
    // 新增购票人
    void addBuyer(Long userId, BuyerAddDTO dto);

    // 获取用户的购票人列表（脱敏）
    List<BuyerVO> getBuyerListByUserId(Long userId);

    // 后台审核购票人
    void auditBuyer(BuyerAuditDTO dto, Long adminId);

    // 根据ID+用户ID查询购票人详情
    BuyerVO getBuyerDetailByIdAndUserId(Long id, Long userId);

    // 编辑购票人
    boolean editBuyer(Long id, Long userId, BuyerEditDTO dto);

    // 删除购票人
    boolean deleteBuyer(Long id, Long userId);

    List<BuyerVO> getBuyerListByStatus(Integer auditStatus);

    List<BuyerVO> getAllBuyerList();
}
