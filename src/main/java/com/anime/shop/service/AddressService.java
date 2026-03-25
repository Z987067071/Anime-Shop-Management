package com.anime.shop.service;

import com.anime.shop.controller.dto.mine.AddressDTO;
import com.anime.shop.controller.dto.mine.AddressVO;

import java.util.List;

public interface AddressService {
    void addAddress(AddressDTO dto);
    void editAddress(AddressDTO dto);
    void deleteAddress(Long id, Long userId);
    List<AddressVO> listAddress(Long userId);
    AddressVO getDefaultAddress(Long userId);
    AddressVO getAddressById(Long id, Long userId);
}
