package com.anime.shop.controller;

import com.anime.shop.admin.controller.dto.product.ProductVO;
import com.anime.shop.common.Result;
import com.anime.shop.controller.dto.order.LikeResultVO;
import com.anime.shop.service.MobileProductService;
import com.anime.shop.service.ProductCommentService;
import com.anime.shop.service.ProductLikeService;
import com.anime.shop.util.JwtUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/mobile/product")
public class MobileProductController {

    @Resource
    private MobileProductService mobileProductService;

    @Resource
    private JwtUtil jwtUtil;
    @Resource
    private ProductCommentService productCommentService;
    @Resource
    private ProductLikeService productLikeService;

    /** 全部商品 */
    @GetMapping("/list")
    public Result<Page<ProductVO>> listAll(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ProductVO> productPage = mobileProductService.listAll(pageNum, pageSize);
        return Result.success(productPage);
    }

    /** 一级分类查询全部商品 */
    @GetMapping("/first-category/{firstCategoryId}")
    public Result<Page<ProductVO>> listByFirstCategory(
            @PathVariable Long firstCategoryId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ProductVO> productPage = mobileProductService.listByFirstCategory(firstCategoryId, pageNum, pageSize);
        return Result.success(productPage);
    }

    /** 按二级分类查询上架商品 */
    @GetMapping("/category/{categoryId}")
    public Result<Page<ProductVO>> listByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ProductVO> productPage = mobileProductService.listByCategory(categoryId, pageNum, pageSize);
        return Result.success(productPage);
    }

    /** 按IP（tag）查询上架商品 */
    @GetMapping("/tag/{tag}")
    public Result<Page<ProductVO>> listByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<ProductVO> productPage = mobileProductService.listByTag(tag, pageNum, pageSize);
        return Result.success(productPage);
    }

    /** 查询商品详情（移动端） */
    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> getDetail(@PathVariable Long id, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        // 1. 获取登录用户ID（从Token解析）
        Long userId = null;
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
        }
        // 仅Token有效时解析userId（未登录用户不返回点赞状态）
        if (token != null && !token.isEmpty() && jwtUtil.validateToken(token)) {
            userId = jwtUtil.getUserIdFromToken(token);
        }

        // 2. 查询商品基础详情（原有逻辑）
        ProductVO productVO = mobileProductService.getDetail(id);
        result.put("product", productVO);

        // 3. 查询评论数量（合并评论接口）
        Integer commentCount = productCommentService.getCommentCount(id);
        result.put("commentCount", commentCount == null ? 0 : commentCount);

        // 4. 查询点赞信息（合并点赞接口）
        LikeResultVO likeResult = new LikeResultVO();
        likeResult.setLikeCount(productLikeService.getProductLikeCount(id)); // 总点赞数
        if (userId != null) {
            likeResult.setHasLiked(productLikeService.isUserLiked(id, userId)); // 当前用户是否点赞
        } else {
            likeResult.setHasLiked(false); // 未登录用户默认未点赞
        }
        result.put("likeInfo", likeResult);

        boolean isCollected = false;
        if (userId != null) {
            isCollected = mobileProductService.existsCollection(userId, id);
        }
        result.put("isCollected", isCollected);

        return Result.success(result);
    }

    /**
     * 查询首页推荐商品（按sort排序/销量优先）
     * @param limit 数量（默认8）
     * @return 推荐商品列表
     */
    @GetMapping("/recommend")
    public Result<List<ProductVO>> listRecommend(@RequestParam(defaultValue = "8") Integer limit) {
        return Result.success(mobileProductService.listRecommend(limit));
    }

    @GetMapping("/search")
    public Result<Page<ProductVO>> searchProduct(
            @RequestParam String keyword,
            @RequestParam(required = false) Integer isTicket,
            // 👇 新增下面三个参数
            @RequestParam(defaultValue = "default") String sort,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {

        Page<ProductVO> productPage = mobileProductService.searchProduct(
                keyword, isTicket, sort, order, pageNum, pageSize
        );
        return Result.success(productPage);
    }

    /**
     * 校验商品/票种状态（解决404的核心接口）
     * @param productId 商品ID
     * @param skuId 票种SKU ID（可选）
     * @return 校验结果
     */
    @GetMapping("/check-status")
    public Result<?> checkProductStatus(
            @RequestParam("productId") Long productId,
            @RequestParam(value = "skuId", required = false) Long skuId) {
        try {
            // 调用Service层校验逻辑
            boolean isValid = mobileProductService.checkProductStatus(productId, skuId);
            if (isValid) {
                return Result.ok("商品/票种状态正常");
            } else {
                return Result.fail("商品/票种已失效（下架/库存不足）");
            }
        } catch (Exception e) {
            return Result.fail("校验商品状态失败：" + e.getMessage());
        }
    }

    // 新增收藏相关接口（直接加在原有接口后面）
    /**
     * 收藏/取消收藏商品
     */
    @PostMapping("/collect")
    public Result<?> collectProduct(
            @RequestParam Long productId,
            @RequestParam Integer isCollect,
            HttpServletRequest request) {
        // 复用你已有的Token解析逻辑
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
        }
        if (!jwtUtil.validateToken(token)) {
            return Result.fail("请先登录");
        }
        Long userId = jwtUtil.getUserIdFromToken(token);

        boolean result = mobileProductService.collectProduct(userId, productId, isCollect);
        return result ?
                Result.success(isCollect == 1 ? "收藏成功" : "取消收藏成功") :
                Result.fail(isCollect == 1 ? "收藏失败" : "取消收藏失败");
    }

    /**
     * 查询用户收藏的商品列表
     */
    @GetMapping("/collect/list")
    public Result<Page<ProductVO>> getCollectList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        // 同样复用Token解析逻辑
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
        }
        if (!jwtUtil.validateToken(token)) {
            return Result.fail("请先登录");
        }
        Long userId = jwtUtil.getUserIdFromToken(token);

        Page<ProductVO> collectPage = mobileProductService.getCollectList(userId, pageNum, pageSize);
        return Result.success(collectPage);
    }
}
