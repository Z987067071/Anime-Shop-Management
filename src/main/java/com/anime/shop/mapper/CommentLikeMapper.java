package com.anime.shop.mapper;

import com.anime.shop.entity.CommentLike;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface CommentLikeMapper extends BaseMapper<CommentLike> {

    /**
     * 查询用户是否给评论点过赞
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 1=已点赞，0=未点赞
     */
    @Select("SELECT COUNT(1) FROM p_comment_like WHERE comment_id = #{commentId} AND user_id = #{userId}")
    int countByCommentIdAndUserId(@Param("commentId") Long commentId, @Param("userId") Long userId);

}
