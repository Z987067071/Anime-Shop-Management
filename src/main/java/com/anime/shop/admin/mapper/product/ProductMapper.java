package com.anime.shop.admin.mapper.product;

import com.anime.shop.admin.controller.dto.product.ProductQueryDTO;
import com.anime.shop.admin.controller.dto.product.ProductVO;
import com.anime.shop.entity.ProductEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductMapper extends BaseMapper<ProductEntity> {
    // 商品详情查询
    @Select("SELECT " +
            "p.id, p.product_name, p.category_id, p.cover_img, p.price, p.original_price, p.remain_stock,p.total_stock, p.status, p.sort, p.tag,p.sales, " +
            "p.product_type, " +"p.product_type, p.is_ticket, p.comic_con_id, " +
            "c.category_name, pc.category_name AS first_category_name, pd.detail_content " +
            "FROM p_product p " +
            "LEFT JOIN p_category c ON p.category_id = c.id " +
            "LEFT JOIN p_category pc ON c.parent_id = pc.id " +
            "LEFT JOIN p_product_detail pd ON p.id = pd.product_id " +
            "WHERE p.id = #{id}")
    ProductVO selectProductDetailById(@Param("id") Long id);
    // 分页查询
    @Select("<script>" +
            "SELECT " +
            "p.id, p.product_name, p.category_id, c.category_name, " +
            "p.cover_img, p.price, p.original_price, p.remain_stock,p.total_stock, p.status, p.create_time, p.product_type, p.is_ticket,p.sales " +
            "FROM p_product p " +
            "LEFT JOIN p_category c ON p.category_id = c.id " +
            "WHERE 1=1 " +
            "<if test='dto.productName != null and dto.productName != \"\"'>" +
            "AND p.product_name LIKE CONCAT('%', #{dto.productName}, '%') " +
            "</if>" +
            "<if test='dto.categoryId != null and dto.categoryId != \"\"'>" +
            "AND p.category_id = #{dto.categoryId} " +
            "</if>" +
            "<if test='dto.status != null'>" +
            "AND p.status = #{dto.status} " +
            "</if>" +
            "<if test='dto.productType != null'>" +
            "AND p.product_type = #{dto.productType} " +
            "</if>" +
            "<if test='dto.isTicket != null'>" +
            "AND p.is_ticket = #{dto.isTicket} " +
            "</if>" +
            "ORDER BY p.sort DESC, p.create_time DESC" +
            "</script>")
    IPage<ProductVO> selectProductPage(Page<ProductVO> page, @Param("dto") ProductQueryDTO dto);
}
