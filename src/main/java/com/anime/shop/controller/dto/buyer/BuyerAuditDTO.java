package com.anime.shop.controller.dto.buyer;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BuyerAuditDTO {
    @NotNull(message = "购票人ID不能为空")
    private Long id;

    @NotNull(message = "审核状态不能为空（1-通过 2-驳回）")
    @Min(value = 1, message = "审核状态只能是1或2")
    @Max(value = 2, message = "审核状态只能是1或2")
    private Integer auditStatus;

    private String auditRemark; // 驳回时必填
}
