package com.anime.shop.mapper;

import com.anime.shop.controller.community.PostDTO;
import com.anime.shop.entity.CommunityPost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.*;

/**
 * 最终版 Mapper（完全适配 Controller/Service，无 XML）
 */
@Mapper
public interface CommunityPostMapper extends BaseMapper<CommunityPost> {
    @Select({
            "SELECT ",
            "id, user_id, user_avatar, user_name, ",
            "title, content, image_urls,",
            "like_count, comment_count, view_count, ",
            "status, create_time, update_time ",
            "FROM p_community_post ",
            "WHERE status = #{status} ",
            "ORDER BY create_time DESC"
    })
    @Results({
            // 字段名1:1匹配，无任何别名转换（确保和数据库一致）
            @Result(column = "id", property = "id"),
            @Result(column = "user_id", property = "userId"),
            @Result(column = "user_avatar", property = "userAvatar"),
            @Result(column = "user_name", property = "userName"),
            @Result(column = "title", property = "title"),
            @Result(column = "content", property = "content"),
            @Result(column = "image_urls", property = "imageUrls"),
            @Result(column = "like_count", property = "likeCount"),
            @Result(column = "comment_count", property = "commentCount"),
            @Result(column = "view_count", property = "viewCount"),
            @Result(column = "status", property = "status"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "update_time", property = "updateTime")
    })
    IPage<PostDTO> selectPostList(
            IPage<PostDTO> page,
            @Param("status") Integer status
    );

    @Select({
            "SELECT ",
            "id, user_id, user_avatar, user_name, ",
            "title, content, image_urls,",
            "like_count, comment_count, view_count, ",
            "status, create_time, update_time ",
            "FROM p_community_post ",
            "WHERE id = #{id} AND status = 1" // 只查正常状态的帖子
    })
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "user_id", property = "userId"),
            @Result(column = "user_avatar", property = "userAvatar"),
            @Result(column = "user_name", property = "userName"),
            @Result(column = "title", property = "title"),
            @Result(column = "content", property = "content"),
            @Result(column = "image_urls", property = "imageUrls"), // 先查逗号分隔的字符串
            @Result(column = "like_count", property = "likeCount"),
            @Result(column = "comment_count", property = "commentCount"),
            @Result(column = "view_count", property = "viewCount"),
            @Result(column = "status", property = "status"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "update_time", property = "updateTime")
    })
    PostDTO selectPostById(@Param("id") Long id);

    @Update("UPDATE p_community_post SET comment_count = comment_count + 1 WHERE id = #{postId}")
    void incrCommentCount(@Param("postId") Long postId);

    /**
     * 按指定数量递减评论数（防止负数）
     */
    @Update("UPDATE p_community_post " +
            "SET comment_count = IF(comment_count >= #{num}, comment_count - #{num}, 0) " +
            "WHERE id = #{postId}")
    void decrCommentCountByNum(
            @Param("postId") Long postId,
            @Param("num") Integer num);
}