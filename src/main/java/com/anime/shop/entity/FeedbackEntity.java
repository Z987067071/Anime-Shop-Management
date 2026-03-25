package com.anime.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("f_feedback")
public class FeedbackEntity {
    /** 工单ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID（关联u_user.id） */
    private Long userId;

    /** 反馈内容 */
    private String feedbackContent;

    /** 工单状态：0=待审核，1=审核中，2=已解决，3=已驳回，4=已关闭 */
    private Integer status;

    /** 后台回复内容 */
    private String replyContent;

    /** 创建人（用户昵称） */
    private String creator;

    /** 审核人（管理员昵称） */
    private String auditor;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 审核时间 */
    private LocalDateTime auditTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0=未删，1=已删 */
    private Integer isDelete;
}
