package com.anime.shop.admin.mapper.order;

import com.anime.shop.admin.controller.dto.order.OrderQueryDTO;
import com.anime.shop.entity.OrderEntity;
import com.anime.shop.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemEntityMapper extends BaseMapper<OrderItemEntity> {
    @Select("SELECT * FROM p_order_item WHERE order_id = #{orderId} AND is_delete = 0")
    List<OrderItemEntity> selectByOrderId(@Param("orderId") Long orderId);
    IPage<OrderEntity> selectOrderPage(IPage<OrderEntity> page, @Param("query") OrderQueryDTO query);
}
