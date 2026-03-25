package com.anime.shop.mapper;

import com.anime.shop.controller.dto.buyer.BuyerVO;
import com.anime.shop.entity.BuyerInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BuyerInfoMapper extends BaseMapper<BuyerInfo> {
    // 自定义查询：获取用户的购票人列表（脱敏）
    @Select("""
            SELECT 
                id, 
                name, 
                id_card as idCard, 
                audit_status as auditStatus, 
                audit_remark as auditRemark 
            FROM buyer_info 
            WHERE user_id = #{userId}
            ORDER BY create_time DESC
            """)
    List<BuyerVO> selectBuyerVOByUserId(@Param("userId") Long userId);


    @Select("SELECT * FROM buyer_info WHERE audit_status = #{auditStatus} ORDER BY create_time DESC")
    List<BuyerInfo> selectByAuditStatus(Integer auditStatus);

    // 查询全部
    @Select("SELECT * FROM buyer_info ORDER BY create_time DESC")
    List<BuyerInfo> selectAll();
}