package com.anime.shop.mapper.impl;

import com.anime.shop.admin.mapper.product.ProductImageMapper;
import com.anime.shop.admin.mapper.product.ProductMapper;
import com.anime.shop.controller.dto.cart.CartDTO;
import com.anime.shop.controller.dto.cart.CartVO;
import com.anime.shop.entity.CartEntity;
import com.anime.shop.entity.ProductEntity;
import com.anime.shop.entity.ProductImageEntity;
import com.anime.shop.mapper.CartMapper;
import com.anime.shop.service.CartService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Resource
    private CartMapper cartMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private ProductImageMapper productImageMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCart(CartDTO dto) {
        // 1. 基础参数校验
        if (dto.getUserId() == null || dto.getGoodsId() == null || dto.getNum() == null || dto.getNum() <= 0) {
            throw new RuntimeException("参数无效");
        }

        // 2. 校验商品是否已存在，存在则数量累加
        CartEntity existCart = cartMapper.selectByUserAndGoods(dto.getUserId(), dto.getGoodsId());
        if (existCart != null) {
            existCart.setNum(existCart.getNum() + dto.getNum());
            cartMapper.updateById(existCart);
            return;
        }

        // 3. 商品基础信息+状态+库存校验
        ProductEntity product = productMapper.selectById(dto.getGoodsId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (product.getStatus() != 1) {
            throw new RuntimeException("商品已下架，无法加入购物车");
        }
        if (product.getRemainStock() < dto.getNum()) {
            throw new RuntimeException("商品库存不足");
        }

        // 4. 核心修复：从p_product_image取图，优先图片表，降级用coverImg
        String productImageUrl = null;
        List<ProductImageEntity> productImages = productImageMapper.selectByProductId(dto.getGoodsId());
        if (productImages != null && !productImages.isEmpty()) {
            productImageUrl = productImages.stream()
                    .filter(img -> StringUtils.hasText(img.getImageUrl()))
                    .findFirst()
                    .map(ProductImageEntity::getImageUrl)
                    .orElse(null);
        }
        // 降级策略：图片表无有效图片则用商品主表coverImg
        if (!StringUtils.hasText(productImageUrl)) {
            productImageUrl = product.getCoverImg();
        }

        // 5. 构建购物车实体 - 关键：赋值查询到的有效图片地址（修复原错误用product.getCoverImg()）
        CartEntity cart = new CartEntity();
        cart.setUserId(dto.getUserId());
        cart.setGoodsId(dto.getGoodsId());
        cart.setGoodsName(product.getProductName());
        cart.setGoodsImage(productImageUrl); // 修复核心行
        cart.setPrice(product.getPrice());
        cart.setNum(dto.getNum());

        // 6. 插入购物车表
        cartMapper.insert(cart);
    }

    @Override
    public List<CartVO> getCartList(Long userId) {
        List<CartEntity> cartList = cartMapper.selectByUserId(userId);
        return cartList.stream().map(cart -> {
            CartVO cartVO = new CartVO();
            BeanUtils.copyProperties(cart, cartVO);
            // 保障：无图片设为空字符串，避免null，前端统一兜底
            if (!StringUtils.hasText(cartVO.getGoodsImage())) {
                cartVO.setGoodsImage("");
            }
            return cartVO;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNum(Long id, Integer num) {
        if (num <= 0) {
            throw new RuntimeException("购买数量不能小于1");
        }
        cartMapper.updateNumOnly(id, num);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCart(Long id) {
        cartMapper.deleteById(id);
    }
}