package com.anime.shop.admin.mapper.product;

import com.anime.shop.entity.ProductSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductSkuMapper extends BaseMapper<ProductSkuEntity> {
    /**
     * 根据商品ID查询所有SKU（票种）
     */
    @Select("SELECT ps.id, ps.product_id, ps.spec_id, ps.spec_value, ps.price, ps.stock FROM product_sku ps LEFT JOIN product_spec psp ON ps.spec_id = psp.id WHERE ps.product_id = #{productId} AND ps.stock != 0 ORDER BY ps.id ASC")
    List<ProductSkuEntity> selectByProductId(@Param("productId") Long productId);

}
