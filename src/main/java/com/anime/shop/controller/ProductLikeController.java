package com.anime.shop.controller;

import com.anime.shop.common.Result;
import com.anime.shop.controller.dto.order.LikeResultVO;
import com.anime.shop.service.ProductLikeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mobile/product")
public class ProductLikeController {

    @Resource
    private ProductLikeService productLikeService;

    /**
     * 商品点赞/取消点赞
     * @param productId 商品ID
     * @param isLike true=点赞，false=取消点赞
     * @return 操作结果
     */
    @PostMapping("/like/{productId}")
    public Result<LikeResultVO> likeProduct(
            @PathVariable Long productId,
            @RequestParam Boolean isLike,
            // 实际项目中从登录态获取用户ID（这里简化为参数）
            @RequestParam Long userId
    ) {
        try {
            LikeResultVO result = productLikeService.likeProduct(productId, userId, isLike);
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("点赞操作失败");
        }
    }

    /**
     * 查询商品点赞信息（是否点赞+点赞数）
     * @param productId 商品ID
     * @param userId 用户ID
     * @return 点赞信息
     */
//    @GetMapping("/like/info/{productId}")
//    public Result<LikeResultVO> getLikeInfo(
//            @PathVariable Long productId,
//            @RequestParam Long userId
//    ) {
//        LikeResultVO result = new LikeResultVO();
//        result.setHasLiked(productLikeService.isUserLiked(productId, userId));
//        result.setLikeCount(productLikeService.getProductLikeCount(productId));
//        return Result.success(result);
//    }
}