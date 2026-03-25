package com.anime.shop.mapper;

import com.anime.shop.entity.ProductComment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ProductCommentMapper extends BaseMapper<ProductComment> {

    /**
     * 查询商品根评论列表（parent_id=0）
     * @param productId 商品ID
     * @return 根评论列表
     */
    @Select("SELECT * FROM p_product_comment WHERE product_id = #{productId} AND parent_id = 0 AND is_delete = 0 ORDER BY create_time DESC")
    List<ProductComment> selectRootComments(@Param("productId") Long productId);

    /**
     * 查询某条评论的回复列表
     * @param parentId 父评论ID
     * @return 回复列表
     */
    @Select("SELECT * FROM p_product_comment WHERE parent_id = #{parentId} AND is_delete = 0 ORDER BY create_time ASC")
    List<ProductComment> selectReplyComments(@Param("parentId") Long parentId);

    /**
     * 点赞数+1
     */
    @Update("UPDATE p_product_comment SET like_count = like_count + 1 WHERE id = #{commentId}")
    int incrLikeCount(@Param("commentId") Long commentId);

    /**
     * 点赞数-1
     */
    @Update("UPDATE p_product_comment SET like_count = like_count - 1 WHERE id = #{commentId} AND like_count > 0")
    int decrLikeCount(@Param("commentId") Long commentId);

    @Select("SELECT COUNT(1) FROM p_product_comment WHERE product_id = #{productId} AND is_delete = 0")
    Integer selectCommentCountByProductId(@Param("productId") Long productId);
}

