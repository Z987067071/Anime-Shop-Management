package com.anime.shop.mapper;

import com.anime.shop.entity.FeedbackReplyEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface FeedbackReplyMapper extends BaseMapper<FeedbackReplyEntity> {

    /**
     * 根据工单ID查询回复列表（按时间升序）
     */
    @Select("SELECT * FROM f_feedback_reply WHERE feedback_id = #{feedbackId} AND is_delete = 0 ORDER BY create_time ASC")
    List<FeedbackReplyEntity> selectByFeedbackId(@Param("feedbackId") Long feedbackId);
}
