package com.anime.shop.admin.mapper.order;

import com.anime.shop.admin.controller.dto.order.OrderQueryDTO;
import org.apache.ibatis.jdbc.SQL;

public class OrderSqlProvider {

    /**
     * 构建分页查询的动态 SQL
     */
    public String selectOrderPageSql(OrderQueryDTO query) {
        // 基础 SQL
        SQL sql = new SQL()
                .SELECT("*")
                .FROM("p_order")
                .WHERE("is_delete = 0"); // 固定条件

        // 动态拼接条件：订单编号
        if (query.getOrderNo() != null && !query.getOrderNo().isEmpty()) {
            sql.AND().WHERE("order_no LIKE CONCAT('%', #{query.orderNo}, '%')");
        }
        // 动态拼接条件：收货人/购票人（普通订单搜consignee，票务订单搜order_ticket_relation.real_name）
        if (query.getConsignee() != null && !query.getConsignee().isEmpty()) {
            sql.AND().WHERE("(consignee LIKE CONCAT('%', #{query.consignee}, '%') " +
                    "OR EXISTS (SELECT 1 FROM order_ticket_relation otr2 " +
                    "WHERE otr2.order_id = p_order.id " +
                    "AND otr2.real_name LIKE CONCAT('%', #{query.consignee}, '%') " +
                    "AND otr2.is_delete = 0))");
        }
        // 动态拼接条件：收货人手机号
        if (query.getConsigneePhone() != null && !query.getConsigneePhone().isEmpty()) {
            sql.AND().WHERE("consignee_phone LIKE CONCAT('%', #{query.consigneePhone}, '%')");
        }
        // 动态拼接条件：订单状态
        if (query.getOrderStatus() != null) {
            sql.AND().WHERE("order_status = #{query.orderStatus}");
        }
        // 动态拼接条件：支付状态
        if (query.getPayStatus() != null) {
            sql.AND().WHERE("pay_status = #{query.payStatus}");
        }
        if (query.getOrderType() != null) {
            sql.AND().WHERE("order_type = #{query.orderType}");
        }
        if (query.getOrderType() != null && query.getOrderType() == 1 && query.getVerifyStatus() != null) {
            sql.AND().WHERE("EXISTS (SELECT 1 FROM order_ticket_relation tor WHERE tor.order_id = p_order.id AND tor.verify_status = #{query.verifyStatus} AND tor.is_delete = 0)");
        }
        if (query.getOrderType() != null && query.getOrderType() == 1 && query.getComicConId() != null) {
            sql.AND().WHERE("EXISTS (SELECT 1 FROM order_ticket_relation tor INNER JOIN comic_con_ticket cct ON tor.comic_con_ticket_id = cct.id WHERE tor.order_id = p_order.id AND cct.comic_con_id = #{query.comicConId})");
        }

        // 排序
        sql.ORDER_BY("create_time DESC");

        // 返回拼接后的 SQL 字符串
        return sql.toString();
    }
}
