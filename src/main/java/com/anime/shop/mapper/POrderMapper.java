package com.anime.shop.mapper;

import com.anime.shop.controller.dto.order.POrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface POrderMapper extends BaseMapper<POrder> {
    @Select({
            "<script>",
            "SELECT o.*, p.product_type ",
            "FROM p_order o ",
            "LEFT JOIN p_order_item oi ON o.id = oi.order_id ",
            "LEFT JOIN p_product p ON oi.product_id = p.id ",
            "WHERE o.user_id = #{userId} ",
            "AND o.is_delete = 0 ",
            "<if test='status != null and validStatus.contains(status)'>",
            "AND o.order_status = #{status} ",
            "</if>",
            "ORDER BY o.create_time DESC",
            "</script>"
    })
    List<POrder> selectOrderWithProductType(
            @Param("userId") Long userId,
            @Param("status") Integer status,
            @Param("validStatus") List<Integer> validStatus
    );
}
