package com.anime.shop.mapper;

import com.anime.shop.entity.OrderTicketRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderTicketRelationMapper extends BaseMapper<OrderTicketRelationEntity> {
    // 按订单ID查询票务关联信息
    @Select("SELECT * FROM order_ticket_relation WHERE order_id = #{orderId} AND is_delete = 0")
    List<OrderTicketRelationEntity> selectByOrderId(@Param("orderId") Long orderId);

    // 按核销码查询票务信息
    @Select("SELECT * FROM order_ticket_relation WHERE verify_code = #{verifyCode} AND is_delete = 0")
    OrderTicketRelationEntity selectByVerifyCode(@Param("verifyCode") String verifyCode);
}
