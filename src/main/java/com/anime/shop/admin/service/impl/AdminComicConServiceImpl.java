package com.anime.shop.admin.service.impl;

import com.anime.shop.admin.controller.dto.comic.ComicConVO;
import com.anime.shop.admin.mapper.comic.AdminComicConMapper;
import com.anime.shop.admin.mapper.product.ProductMapper;
import com.anime.shop.admin.service.AdminComicConService;
import com.anime.shop.entity.ComicCon;
import com.anime.shop.entity.ProductEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminComicConServiceImpl extends ServiceImpl<AdminComicConMapper, ComicCon> implements AdminComicConService {

    @Autowired
    private ProductMapper productMapper;

    @Resource
    private AdminComicConMapper adminComicConMapper;

    @Override
    public boolean bindComicConIdToProduct(Long comicConId, Long productId) {
        // 1. 查询关联的商品（漫展虚拟商品：product_type=1）
        LambdaQueryWrapper<ProductEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductEntity::getId, productId)
                .eq(ProductEntity::getProductType, 1); // 只更新漫展虚拟商品

        ProductEntity product = productMapper.selectOne(wrapper);
        if (product == null) {
            return false; // 商品不存在或不是漫展虚拟商品
        }

        // 2. 更新商品的comic_con_id字段
        ProductEntity updateProduct = new ProductEntity();
        updateProduct.setId(productId);
        updateProduct.setComicConId(comicConId); // 回填漫展ID

        int affectedRows = productMapper.updateById(updateProduct);
        return affectedRows > 0;
    }

    @Override
    public Long getProductIdByConId(Long comicConId) {
        return adminComicConMapper.selectProductIdById(comicConId);
    }

    // 新增：带商品名称的分页查询方法
    public IPage<ComicConVO> getComicConListWithProductName(Integer pageNum, Integer pageSize, String name, Long productId) {
        // 1. 分页查询漫展基础数据
        Page<ComicCon> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ComicCon> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(ComicCon::getName, name);
        }
        // 新增：支持按商品ID筛选
        if (productId != null) {
            wrapper.eq(ComicCon::getProductId, productId);
        }
        wrapper.orderByDesc(ComicCon::getUpdateTime);
        IPage<ComicCon> comicConPage = this.page(page, wrapper);

        // 2. 转换为VO并关联查询商品名称
        List<ComicConVO> voList = comicConPage.getRecords().stream().map(comicCon -> {
            ComicConVO vo = new ComicConVO();
            BeanUtils.copyProperties(comicCon, vo); // 复制原有字段

            // 3. 根据productId查询商品名称（适配你的ProductEntity）
            if (comicCon.getProductId() != null) {
                ProductEntity product = productMapper.selectById(comicCon.getProductId());
                if (product != null) {
                    vo.setProductName(product.getProductName()); // 取商品名称
                } else {
                    vo.setProductName("商品已删除"); // 友好提示
                }
            } else {
                vo.setProductName("未关联商品"); // 友好提示
            }
            return vo;
        }).collect(Collectors.toList());

        // 4. 封装VO分页结果
        Page<ComicConVO> resultPage = new Page<>(pageNum, pageSize);
        resultPage.setRecords(voList);
        resultPage.setTotal(comicConPage.getTotal());
        resultPage.setSize(comicConPage.getSize());
        resultPage.setCurrent(comicConPage.getCurrent());
        resultPage.setPages(comicConPage.getPages());

        return resultPage;
    }
    @Override
    public ComicConVO getComicConVOById(Long id) {
        // 1. 查询基础数据
        ComicCon comicCon = this.getById(id);
        if (comicCon == null) {
            return null;
        }
        // 2. 转换为VO并补充商品名称
        ComicConVO vo = new ComicConVO();
        BeanUtils.copyProperties(comicCon, vo);
        if (comicCon.getProductId() != null) {
            ProductEntity product = productMapper.selectById(comicCon.getProductId());
            if (product != null) {
                vo.setProductName(product.getProductName());
            } else {
                vo.setProductName("商品已删除");
            }
        } else {
            vo.setProductName("未关联商品");
        }
        return vo;
    }
}
