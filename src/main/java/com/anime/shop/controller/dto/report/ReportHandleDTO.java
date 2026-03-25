package com.anime.shop.controller.dto.report;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReportHandleDTO {
    /** 处理状态：1=已受理,2=已驳回,3=已处理 */
    @NotNull(message = "处理状态不能为空")
    @Min(value = 1, message = "处理状态只能是1-3")
    @Max(value = 3, message = "处理状态只能是1-3")
    private Integer status;

    /** 处理备注（长度≤500） */
    @Size(max = 500, message = "处理备注长度不能超过500字")
    private String handleNote;

    public interface HandleGroup {}
}