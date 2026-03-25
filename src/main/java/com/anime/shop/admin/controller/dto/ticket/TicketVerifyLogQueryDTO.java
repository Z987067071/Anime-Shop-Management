package com.anime.shop.admin.controller.dto.ticket;

import lombok.Data;

@Data
public class TicketVerifyLogQueryDTO {
    private Integer pageNum = 1;         // 页码
    private Integer pageSize = 10;       // 页大小

    private String verifyCode;           // 按核销码筛选
    private String verifyStaff;          // 按核销员筛选
    private Integer verifyResult;        // 按核销结果筛选（0=失败 1=成功）
    private String startTime;            // 开始时间（yyyy-MM-dd HH:mm:ss）
    private String endTime;              // 结束时间（yyyy-MM-dd HH:mm:ss）
    private Long orderId;                // 按订单ID筛选
}
