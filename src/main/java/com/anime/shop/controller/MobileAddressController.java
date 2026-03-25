package com.anime.shop.controller;

import com.anime.shop.common.Result;
import com.anime.shop.controller.dto.mine.AddressDTO;
import com.anime.shop.controller.dto.mine.AddressVO;
import com.anime.shop.service.AddressService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址控制器（简化版）
 */
@RestController
@RequestMapping("/api/mobile/address")
public class MobileAddressController {

    @Resource
    private AddressService addressService;

    @PostMapping("/add")
    public Result<Void> addAddress(@Valid @RequestBody AddressDTO dto) {
        addressService.addAddress(dto);
        return Result.success();
    }

    @PostMapping("/edit")
    public Result<Void> editAddress(@Valid @RequestBody AddressDTO dto) {
        addressService.editAddress(dto);
        return Result.success();
    }

    /**
     * 删除地址（核心：路径参数id + 请求参数userId）
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteAddress(@PathVariable Long id, @RequestParam Long userId) {
        addressService.deleteAddress(id, userId);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<AddressVO>> listAddress(@RequestParam Long userId) {
        return Result.success(addressService.listAddress(userId));
    }

    @GetMapping("/default")
    public Result<AddressVO> getDefaultAddress(@RequestParam Long userId) {
        return Result.success(addressService.getDefaultAddress(userId));
    }

    @GetMapping("/detail/{id}")
    public Result<AddressVO> getAddressDetail(@PathVariable Long id, @RequestParam Long userId) {
        return Result.success(addressService.getAddressById(id, userId));
    }
}