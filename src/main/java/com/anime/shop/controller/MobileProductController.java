package com.anime.shop.controller;

import com.anime.shop.admin.controller.dto.product.ProductVO;
import com.anime.shop.common.Result;
import com.anime.shop.controller.dto.order.LikeResultVO;
import com.anime.shop.service.MobileProductService;
import com.anime.shop.service.ProductCommentService;
import com.anime.shop.service.ProductLikeService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/mobile/product")
@RequiredArgsConstructor
public class MobileProductController {

    private final MobileProductService mobileProductService;
    private final ProductCommentService productCommentService;
    private final ProductLikeService productLikeService;

    @GetMapping("/list")
    public Result<Page<ProductVO>> listAll(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(mobileProductService.listAll(pageNum, pageSize));
    }

    @GetMapping("/first-category/{firstCategoryId}")
    public Result<Page<ProductVO>> listByFirstCategory(
            @PathVariable Long firstCategoryId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(mobileProductService.listByFirstCategory(firstCategoryId, pageNum, pageSize));
    }

    @GetMapping("/category/{categoryId}")
    public Result<Page<ProductVO>> listByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(mobileProductService.listByCategory(categoryId, pageNum, pageSize));
    }

    @GetMapping("/tag/{tag}")
    public Result<Page<ProductVO>> listByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(mobileProductService.listByTag(tag, pageNum, pageSize));
    }

    /**
     * 商品详情（可选登录）：合并了点赞、评论数、收藏状态
     * userId 由拦截器注入，未登录时为 null
     */
    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> getDetail(
            @PathVariable Long id,
            @RequestAttribute(required = false) Long userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("product", mobileProductService.getDetail(id));

        Integer commentCount = productCommentService.getCommentCount(id);
        result.put("commentCount", commentCount == null ? 0 : commentCount);

        LikeResultVO likeResult = new LikeResultVO();
        likeResult.setLikeCount(productLikeService.getProductLikeCount(id));
        likeResult.setHasLiked(userId != null && productLikeService.isUserLiked(id, userId));
        result.put("likeInfo", likeResult);

        result.put("isCollected", userId != null && mobileProductService.existsCollection(userId, id));
        return Result.success(result);
    }

    @GetMapping("/recommend")
    public Result<List<ProductVO>> listRecommend(@RequestParam(defaultValue = "8") Integer limit) {
        return Result.success(mobileProductService.listRecommend(limit));
    }

    @GetMapping("/search")
    public Result<Page<ProductVO>> searchProduct(
            @RequestParam String keyword,
            @RequestParam(required = false) Integer isTicket,
            @RequestParam(defaultValue = "default") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(mobileProductService.searchProduct(keyword, isTicket, sort, order, pageNum, pageSize));
    }

    @GetMapping("/check-status")
    public Result<?> checkProductStatus(
            @RequestParam Long productId,
            @RequestParam(required = false) Long skuId) {
        boolean isValid = mobileProductService.checkProductStatus(productId, skuId);
        return isValid ? Result.success("商品/票种状态正常") : Result.error(400, "商品/票种已失效（下架/库存不足）");
    }

    /** 收藏/取消收藏（需登录） */
    @PostMapping("/collect")
    public Result<?> collectProduct(
            @RequestParam Long productId,
            @RequestParam Integer isCollect,
            @RequestAttribute Long userId) {
        boolean result = mobileProductService.collectProduct(userId, productId, isCollect);
        return result
                ? Result.success(isCollect == 1 ? "收藏成功" : "取消收藏成功")
                : Result.error(400, isCollect == 1 ? "收藏失败" : "取消收藏失败");
    }

    /** 查询收藏列表（需登录） */
    @GetMapping("/collect/list")
    public Result<Page<ProductVO>> getCollectList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestAttribute Long userId) {
        return Result.success(mobileProductService.getCollectList(userId, pageNum, pageSize));
    }
}
