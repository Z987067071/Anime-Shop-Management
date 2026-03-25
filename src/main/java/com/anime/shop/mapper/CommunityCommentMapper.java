package com.anime.shop.mapper;

import com.anime.shop.entity.CommunityComment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper // 新增@Mapper注解，确保MyBatis扫描到
public interface CommunityCommentMapper extends BaseMapper<CommunityComment> {

    /**
     * 查询根评论的回复列表（添加@Select注解）
     */
    @Select("SELECT * FROM p_community_comment " +
            "WHERE parent_id = #{rootCommentId} " +
            "AND is_delete = 0 " +
            "ORDER BY create_time ASC")
    List<CommunityComment> selectReplyComments(@Param("rootCommentId") Long rootCommentId);

    /**
     * 增加点赞数（已有@Update注解，保留）
     */
    @Update("UPDATE p_community_comment SET like_count = like_count + 1 WHERE id = #{commentId}")
    void incrLikeCount(@Param("commentId") Long commentId);

    /**
     * 减少点赞数（已有@Update注解，保留）
     */
    @Update("UPDATE p_community_comment SET like_count = like_count - 1 WHERE id = #{commentId} AND like_count > 0")
    void decrLikeCount(@Param("commentId") Long commentId);

    /**
     * 查询帖子评论总数（添加@Select注解）
     */
    @Select("SELECT COUNT(1) FROM p_community_comment WHERE post_id = #{postId} AND is_delete = 0")
    Integer selectCommentCountByPostId(@Param("postId") Long postId);

    /**
     * 统计根评论下的所有回复数（包括根评论自己）
     */
    @Select("SELECT COUNT(1) FROM p_community_comment " +
            "WHERE post_id = #{postId} " +
            "AND (id = #{rootCommentId} OR parent_id = #{rootCommentId}) " +
            "AND is_delete = 0")
    Integer countRootCommentAndReplies(
            @Param("postId") Long postId,
            @Param("rootCommentId") Long rootCommentId);
}
