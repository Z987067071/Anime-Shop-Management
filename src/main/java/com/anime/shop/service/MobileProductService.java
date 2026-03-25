package com.anime.shop.service;

import com.anime.shop.admin.controller.dto.product.ProductVO;
import com.anime.shop.entity.ProductSkuEntity;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface MobileProductService {

    /**
     * 按二级分类ID查询上架商品
     * @param categoryId 二级分类ID（如景品手办ID=28）
     * @return 商品列表
     */
    Page<ProductVO> listByCategory(Long categoryId, Integer pageNum, Integer pageSize);

    /**
     * 按IP标签查询上架商品（如“海贼王”“明日方舟”）
     * @param tag IP标签（模糊匹配）
     * @return 商品列表
     */
    Page<ProductVO> listByTag(String tag, Integer pageNum, Integer pageSize);

    /**
     * 首页查询全部上架商品（带分页）
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 分页商品列表
     */
    Page<ProductVO> listAll(Integer pageNum, Integer pageSize);

    /**
     * 按一级分类查询上架商品（手办/周边等）
     * @param firstCategoryId 一级分类ID
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 分页商品列表
     */
    Page<ProductVO> listByFirstCategory(Long firstCategoryId, Integer pageNum, Integer pageSize);

    /**
     * 首页推荐商品（按sort排序，取前N条）
     * @param limit 数量限制
     * @return 推荐商品列表
     */
    List<ProductVO> listRecommend(Integer limit);

    /**
     * 查询移动端商品详情（含基础信息+详情+图片）
     * @param id 商品ID
     * @return 商品详情VO
     */
    ProductVO getDetail(Long id);

    Page<ProductVO> searchProduct(String keyword, Integer isTicket, String sort, String order, Integer pageNum, Integer pageSize);

    List<ProductSkuEntity> listProductSku(Long productId);

    boolean checkProductStatus(Long productId, Long skuId);

    boolean collectProduct(Long userId, Long productId, Integer isCollect);

    Page<ProductVO> getCollectList(Long userId, Integer pageNum, Integer pageSize);

    boolean existsCollection(Long userId, Long id);
}
