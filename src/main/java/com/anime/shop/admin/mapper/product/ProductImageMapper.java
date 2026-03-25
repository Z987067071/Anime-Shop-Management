package com.anime.shop.admin.mapper.product;

import com.anime.shop.entity.ProductImageEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductImageMapper extends BatchBaseMapper<ProductImageEntity> {
    @Delete("DELETE FROM p_product_image WHERE product_id = #{productId}")
    int deleteByProductId(@Param("productId") Long productId);

    @Select("SELECT id, product_id, image_url, type, sort FROM p_product_image WHERE product_id = #{productId} ORDER BY sort DESC")
    List<ProductImageEntity> selectByProductId(@Param("productId") Long productId);

    void insertBatchSomeColumn(List<ProductImageEntity> imageList);
}
