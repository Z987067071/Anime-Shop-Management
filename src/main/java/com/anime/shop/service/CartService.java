package com.anime.shop.service;

import com.anime.shop.controller.dto.cart.CartDTO;
import com.anime.shop.controller.dto.cart.CartVO;

import java.util.List;

public interface CartService {
    void addCart(CartDTO dto);
    List<CartVO> getCartList(Long userId);
    void updateNum(Long id, Integer num);
    void deleteCart(Long id);
}
