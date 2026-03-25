package com.anime.shop.mapper;

import com.anime.shop.entity.FeedbackEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface FeedbackMapper extends BaseMapper<FeedbackEntity> {
    /**
     * 后台分页查询工单
     */
    @Select({
            "<script>",
            "SELECT * FROM f_feedback",
            "WHERE is_delete = 0",
            "<if test='status != null'> AND status = #{status}</if>",
            "<if test='userId != null'> AND user_id = #{userId}</if>",
            "<if test='keyword != null and keyword != \"\"'> AND feedback_content LIKE CONCAT('%', #{keyword}, '%')</if>",
            "<if test='startTime != null and startTime != \"\"'> AND create_time &gt;= STR_TO_DATE(#{startTime}, '%Y-%m-%d %H:%i:%s')</if>",
            "<if test='endTime != null and endTime != \"\"'> AND create_time &lt;= STR_TO_DATE(#{endTime}, '%Y-%m-%d %H:%i:%s')</if>",
            "ORDER BY create_time DESC",
            "</script>"
    })
    IPage<FeedbackEntity> selectAdminPage(
            Page<FeedbackEntity> page,
            @Param("status") Integer status,
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime
    );

    /**
     * 用户查询自己的工单列表
     */
    @Select({
            "<script>",
            "SELECT * FROM f_feedback",
            "WHERE is_delete = 0 AND user_id = #{userId}",
            "<if test='status != null'> AND status = #{status}</if>",
            "ORDER BY create_time DESC",
            "</script>"
    })
    IPage<FeedbackEntity> selectUserPage(
            Page<FeedbackEntity> page,
            @Param("userId") Long userId,
            @Param("status") Integer status
    );
}
