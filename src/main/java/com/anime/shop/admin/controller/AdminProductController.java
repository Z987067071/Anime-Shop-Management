package com.anime.shop.admin.controller;


import com.anime.shop.admin.controller.dto.product.ProductAddDTO;
import com.anime.shop.admin.controller.dto.product.ProductEditDTO;
import com.anime.shop.admin.controller.dto.product.ProductQueryDTO;
import com.anime.shop.admin.controller.dto.product.ProductVO;
import com.anime.shop.admin.service.AdminProductService;
import com.anime.shop.common.Result;
import com.anime.shop.entity.ProductSkuEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/product")
public class AdminProductController {
    @Resource
    private AdminProductService productService;

    /** 分页查询商品 */
    @PostMapping("/page")
    public Result<IPage<ProductVO>> pageProduct(@RequestBody ProductQueryDTO dto) {
        return Result.success(productService.pageProduct(dto));
    }

    /** 新增商品 */
    @PostMapping("/add")
    public Result<Void> addProduct(@Validated @RequestBody ProductAddDTO dto) {
        productService.addProduct(dto);
        return Result.success();
    }

    /** 查询商品详情 */
    @GetMapping("/detail/{id}")
    public Result<ProductVO> getProductDetail(@PathVariable Long id) {
        return Result.success(productService.getProductDetail(id));
    }

    /** 编辑商品 */
    @PostMapping("/edit")
    public Result<Void> editProduct(@Validated @RequestBody ProductEditDTO dto) {
        productService.editProduct(dto);
        return Result.success();
    }

    /** 修改商品上下架状态 */
    @PostMapping("/updateStatus")
    public Result<Void> updateProductStatus(@RequestParam Long id, @RequestParam Integer status) {
        productService.updateProductStatus(id, status);
        return Result.success();
    }

    /** 删除商品 */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }

    /** 查询商品SKU（票种)*/
    @GetMapping("/sku/{productId}")
    public Result<List<ProductSkuEntity>> listProductSku(@PathVariable Long productId) {
        return Result.success(productService.listProductSku(productId));
    }
}
