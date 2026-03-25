package com.anime.shop.mapper;

import com.anime.shop.entity.CommunityCommentLike;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CommunityCommentLikeMapper extends BaseMapper<CommunityCommentLike> {

    /**
     * 统计用户是否点赞该评论
     */
    @Select("SELECT COUNT(1) FROM p_community_comment_like WHERE community_comment_id = #{communityCommentId} AND user_id = #{userId}")
    int countByCommentIdAndUserId(@Param("communityCommentId") Long communityCommentId, @Param("userId") Long userId);
}
