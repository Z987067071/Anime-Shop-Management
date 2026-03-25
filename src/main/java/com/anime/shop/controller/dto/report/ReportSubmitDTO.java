package com.anime.shop.controller.dto.report;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReportSubmitDTO {
    /** 举报目标类型：1=商品，2=商品评论，3=社区帖子，4=社区帖子评论 */
    @NotNull(message = "举报目标类型不能为空")
    @Min(value = 1, message = "举报目标类型只能是1-4")
    @Max(value = 4, message = "举报目标类型只能是1-4")
    private Integer targetType;

    /** 举报目标ID */
    @NotNull(message = "举报目标ID不能为空")
    private Long targetId;

    /** 举报理由：1=违反法律规定...99=其他 */
    @NotNull(message = "举报理由不能为空")
    private Integer reportReason;

    /** 自定义理由（仅reportReason=99时必填，长度≤150） */
    @Size(max = 150, message = "自定义理由长度不能超过150字")
    private String customReason;
}