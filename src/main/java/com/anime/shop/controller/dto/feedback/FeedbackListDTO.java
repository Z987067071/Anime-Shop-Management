package com.anime.shop.controller.dto.feedback;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FeedbackListDTO {
    /**
     * 用户ID（必填，仅能查询自己的工单）
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 工单状态筛选（可选）
     * 0=待审核，1=审核中，2=已解决，3=已驳回，4=已关闭
     */
    private Integer status;

    /**
     * 页码（默认1）
     */
    private Integer page = 1;

    /**
     * 每页条数（默认10）
     */
    private Integer size = 10;
}
