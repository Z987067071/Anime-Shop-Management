package com.anime.shop.admin.service;

import com.anime.shop.admin.controller.dto.product.ProductAddDTO;
import com.anime.shop.admin.controller.dto.product.ProductEditDTO;
import com.anime.shop.admin.controller.dto.product.ProductQueryDTO;
import com.anime.shop.admin.controller.dto.product.ProductVO;
import com.anime.shop.entity.ProductSkuEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface AdminProductService {
    /** 分页查询商品 */
    IPage<ProductVO> pageProduct(ProductQueryDTO dto);

    /** 新增商品（含详情+图片） */
    void addProduct(ProductAddDTO dto);

    /** 根据ID查询商品详情 */
    ProductVO getProductDetail(Long id);

    /** 编辑商品 */
    void editProduct(ProductEditDTO dto);

    /** 修改商品上下架状态 */
    void updateProductStatus(Long id, Integer status);

    /** 删除商品（物理删除，含详情+图片） */
    void deleteProduct(Long id);

    /** 根据商品ID查询票种SKU列表 */
    List<ProductSkuEntity> listProductSku(Long productId);
}
