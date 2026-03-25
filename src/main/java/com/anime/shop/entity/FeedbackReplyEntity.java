package com.anime.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("f_feedback_reply")
public class FeedbackReplyEntity {
    /** 回复ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联工单ID */
    private Long feedbackId;

    /** 回复人ID（用户ID/管理员ID） */
    private Long userId;

    /** 回复内容 */
    private String content;

    /** 是否管理员回复：0-用户 1-管理员 */
    private Integer isAdmin;

    /** 回复人昵称 */
    private String replyer;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 逻辑删除：0-未删 1-已删 */
    private Integer isDelete;
}
