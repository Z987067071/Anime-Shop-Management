package com.anime.shop.admin.mapper.order;

import com.anime.shop.admin.controller.dto.order.OrderQueryDTO;
import com.anime.shop.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderEntityMapper extends BaseMapper<OrderEntity> {

    /**
     * 分页查询订单列表（动态条件）
     * 用 @SelectProvider 替代 @Select，支持动态 SQL
     */
    @SelectProvider(
            type = OrderSqlProvider.class,
            method = "selectOrderPageSql"
    )
    IPage<OrderEntity> selectOrderPage(Page<OrderEntity> page, @Param("query") OrderQueryDTO query);

}
