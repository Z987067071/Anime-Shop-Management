package com.anime.shop.mapper;

import com.anime.shop.entity.CartEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CartMapper extends BaseMapper<CartEntity> {

    @Select("""
        SELECT * FROM u_cart
        WHERE user_id = #{userId}
        ORDER BY create_time DESC
    """)
    List<CartEntity> selectByUserId(@Param("userId") Long userId);

    @Select("""
        SELECT * FROM u_cart
        WHERE user_id = #{userId} AND goods_id = #{goodsId}
        LIMIT 1
    """)
    CartEntity selectByUserAndGoods(
            @Param("userId") Long userId,
            @Param("goodsId") Long goodsId
    );
    void updateNumOnly(@Param("id") Long id, @Param("num") Integer num);
}
