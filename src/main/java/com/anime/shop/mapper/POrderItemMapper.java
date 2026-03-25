package com.anime.shop.mapper;

import com.anime.shop.controller.dto.order.POrderItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface POrderItemMapper extends BaseMapper<POrderItem> {
    @Insert({
            "<script>",
            "INSERT INTO p_order_item (order_id, order_no, product_id, product_name, product_img, product_price, quantity, total_price, is_delete)",
            "VALUES",
            "<foreach collection='list' item='item' separator=','>",
            "(#{item.orderId}, #{item.orderNo}, #{item.productId}, #{item.productName}, #{item.productImg}, #{item.productPrice}, #{item.quantity}, #{item.totalPrice}, 0)",
            "</foreach>",
            "</script>"
    })
    void insertBatchSomeColumn(@Param("list") List<POrderItem> orderItems);
}
