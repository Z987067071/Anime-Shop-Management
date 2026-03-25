package com.anime.shop.admin.mapper.product;

import com.anime.shop.entity.ProductDetailEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductDetailMapper extends BaseMapper<ProductDetailEntity> {
    @Delete("DELETE FROM p_product_detail WHERE product_id = #{productId}")
    void deleteByProductId(@Param("productId") Long productId);

    @Select("SELECT id, product_id, detail_content FROM p_product_detail WHERE product_id = #{productId}")
    List<ProductDetailEntity> selectByProductId(@Param("productId") Long productId);
}

