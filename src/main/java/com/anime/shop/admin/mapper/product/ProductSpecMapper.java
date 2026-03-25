package com.anime.shop.admin.mapper.product;

import com.anime.shop.entity.ProductSpecEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductSpecMapper extends BaseMapper<ProductSpecEntity> {
    /**
     * 根据规格值查询票种规格ID（比如“周六VIP票”→对应spec_id）
     */
    @Select("SELECT id FROM product_spec WHERE spec_name = '票种' AND spec_value = #{specValue}")
    Long selectSpecIdByValue(@Param("specValue") String specValue);
}