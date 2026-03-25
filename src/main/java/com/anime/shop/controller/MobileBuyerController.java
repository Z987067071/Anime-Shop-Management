package com.anime.shop.controller;

import com.anime.shop.common.Result;
import com.anime.shop.controller.dto.buyer.BuyerAddDTO;
import com.anime.shop.controller.dto.buyer.BuyerEditDTO;
import com.anime.shop.controller.dto.buyer.BuyerVO;
import com.anime.shop.service.BuyerInfoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mobile/buyer")
public class MobileBuyerController {
    @Autowired
    private BuyerInfoService buyerInfoService;


    // 新增购票人
    @PostMapping("/add")
    public Result<?> addBuyer(@Valid @RequestBody BuyerAddDTO dto) {
        if (dto.getUserId() == null) {
            return Result.fail("用户ID不能为空");
        }
        buyerInfoService.addBuyer(dto.getUserId(), dto);
        return Result.success("新增购票人成功，等待后台审核");
    }

    // 获取当前用户的购票人列表
    @GetMapping("/list")
    public Result<List<BuyerVO>> getBuyerList(@RequestParam Long userId) {
        List<BuyerVO> list = buyerInfoService.getBuyerListByUserId(userId);
        return Result.success(list);
    }

    @GetMapping("/detail/{id}")
    public Result<BuyerVO> getBuyerDetail(@PathVariable Long id, @RequestParam Long userId) {

        BuyerVO buyerVO = buyerInfoService.getBuyerDetailByIdAndUserId(id, userId);
        if (buyerVO == null) {
            return Result.fail("购票人不存在或无查看权限");
        }
        return Result.success(buyerVO);
    }

    @PutMapping("/edit/{id}")
    public Result<?> editBuyer(@PathVariable Long id, @Valid @RequestBody BuyerEditDTO dto) {

        boolean success = buyerInfoService.editBuyer(id, dto.getUserId(), dto);
        if (!success) {
            return Result.fail("购票人不存在/无编辑权限（仅未审核/驳回的可编辑）");
        }
        return Result.success("编辑购票人成功，需重新等待审核");
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> deleteBuyer(@PathVariable Long id, @RequestParam Long userId) {
        boolean success = buyerInfoService.deleteBuyer(id, userId);
        if (!success) {
            return Result.fail("购票人不存在/无删除权限（仅未审核/驳回的可删除）");
        }
        return Result.success("删除购票人成功");
    }
}
