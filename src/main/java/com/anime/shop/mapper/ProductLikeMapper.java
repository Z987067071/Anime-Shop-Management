package com.anime.shop.mapper;


import com.anime.shop.entity.ProductLike;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

public interface ProductLikeMapper extends BaseMapper<ProductLike> {

    /**
     * 查询用户是否已点赞该商品
     * @param productId 商品ID
     * @param userId 用户ID
     * @return 是否点赞（1=已点赞，0=未点赞）
     */
    @Select("SELECT COUNT(1) FROM p_product_like WHERE product_id = #{productId} AND user_id = #{userId}")
    int checkUserLike(@Param("productId") Long productId, @Param("userId") Long userId);

    /**
     * 查询商品点赞总数
     * @param productId 商品ID
     * @return 点赞数
     */
    @Select("SELECT COUNT(1) FROM p_product_like WHERE product_id = #{productId}")
    int countProductLike(@Param("productId") Long productId);
}
