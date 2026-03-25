package com.anime.shop.controller;

import com.anime.shop.common.Result;
import com.anime.shop.common.ResultCode;
import com.anime.shop.controller.dto.cart.CartDTO;
import com.anime.shop.controller.dto.cart.CartVO;
import com.anime.shop.service.CartService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 移动端购物车控制器 - 适配Result/ResultCode，统一返回格式，全局异常捕获
 */
@Slf4j
@RestController
@RequestMapping("/api/mobile/cart")
public class MobileCartController {
    @Resource
    private CartService cartService;

    /**
     * 加入购物车
     */
    @PostMapping("/add")
    public Result<Void> addCart(@RequestBody CartDTO dto) {
        try {
            cartService.addCart(dto);
            // 成功：code=0，msg=success（匹配ResultCode.SUCCESS）
            return Result.success();
        } catch (RuntimeException e) {
            log.error("加入购物车失败：{}", e.getMessage());
            // 业务异常：返回自定义错误信息
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("加入购物车系统异常", e);
            // 系统异常：复用ResultCode.SERVER_ERROR
            return Result.error(ResultCode.SERVER_ERROR);
        }
    }

    /**
     * 查询购物车列表 - 强制msg=success，匹配前端期望格式
     */
    @GetMapping("/list")
    public Result<List<CartVO>> list(@RequestParam Long userId) {
        try {
            List<CartVO> cartList = cartService.getCartList(userId);
            Result<List<CartVO>> result = Result.success(cartList);
            // 强制设置msg为success，与你提供的返回示例完全一致
            result.setMsg("success");
            return result;
        } catch (Exception e) {
            log.error("查询购物车失败，用户ID：{}", userId, e);
            return Result.error(ResultCode.SERVER_ERROR);
        }
    }

    /**
     * 更新购物车商品数量
     */
    @PostMapping("/update/num")
    public Result<Void> updateNum(@RequestParam Long id,
                                  @RequestParam Integer num) {
        try {
            cartService.updateNum(id, num);
            return Result.success();
        } catch (RuntimeException e) {
            log.error("更新购物车数量失败：{}", e.getMessage());
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            log.error("更新购物车数量系统异常，购物车ID：{}", id, e);
            return Result.error(ResultCode.SERVER_ERROR);
        }
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestParam Long id) {
        try {
            cartService.deleteCart(id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除购物车商品失败，购物车ID：{}", id, e);
            return Result.error(ResultCode.SERVER_ERROR);
        }
    }
}