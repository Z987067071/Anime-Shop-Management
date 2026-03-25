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
        // 动态拼接条件：收货人
        if (query.getConsignee() != null && !query.getConsignee().isEmpty()) {
            sql.AND().WHERE("consignee LIKE CONCAT('%', #{query.consignee}, '%')");
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
        if (query.getOrderType() != null && query.getOrderType() == 1) {
            // 关联票务订单表
            sql.JOIN("order_ticket_relation tor ON p_order.id = tor.order_id");
            if (query.getVerifyStatus() != null) {
                sql.AND().WHERE("tor.verify_status = #{query.verifyStatus}");
            }
            if (query.getComicConId() != null) {
                sql.JOIN("comic_con_ticket cct ON tor.comic_con_ticket_id = cct.id");
                sql.AND().WHERE("cct.comic_con_id = #{query.comicConId}");
            }
        }

        // 排序
        sql.ORDER_BY("create_time DESC");

        // 返回拼接后的 SQL 字符串
        return sql.toString();
    }
}
