package com.anime.shop.mapper;

import com.anime.shop.entity.FeedbackImageEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface FeedbackImageMapper extends BaseMapper<FeedbackImageEntity> {
    /**
     * 根据工单ID查询图片列表
     */
    @Select("SELECT * FROM f_feedback_image WHERE feedback_id = #{feedbackId} ORDER BY sort ASC")
    List<FeedbackImageEntity> selectByFeedbackId(@Param("feedbackId") Long feedbackId);

    @Insert({
            "<script>",
            "INSERT INTO f_feedback_image (feedback_id, image_url, sort, create_time)",
            "VALUES",
            "<foreach collection='list' item='item' separator=','>",
            "(#{item.feedbackId}, #{item.imageUrl}, #{item.sort}, NOW())",
            "</foreach>",
            "</script>"
    })
    int insertBatchSomeColumn(@Param("list") List<FeedbackImageEntity> list);
}
