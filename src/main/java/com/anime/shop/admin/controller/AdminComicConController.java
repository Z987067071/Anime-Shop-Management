package com.anime.shop.admin.controller;

import com.anime.shop.admin.controller.dto.comic.ComicConVO;
import com.anime.shop.admin.mapper.product.ProductMapper;
import com.anime.shop.admin.service.AdminComicConService;
import com.anime.shop.common.Result;
import com.anime.shop.entity.ComicCon;
import com.anime.shop.entity.ProductEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/comic-con")
public class AdminComicConController {

    @Autowired
    private AdminComicConService adminComicConService; // 注入调整后的Service

    @Autowired
    private ProductMapper productMapper;

    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> addComicCon(@RequestBody ComicCon comicCon) {
        try {
            if (comicCon.getProductId() == null || comicCon.getProductId() <= 0) {
                return Result.fail("新增漫展失败：必须关联商品ID（product_id）");
            }

            boolean save = adminComicConService.save(comicCon);
            if (!save) {
                return Result.fail("新增漫展失败");
            }

            boolean bindSuccess = adminComicConService.bindComicConIdToProduct(comicCon.getId(), comicCon.getProductId());
            if (!bindSuccess) {
                return Result.fail("新增漫展成功，但绑定商品漫展ID失败（商品可能不存在）");
            }

            return Result.success("新增漫展成功，并已绑定商品漫展ID");
        } catch (Exception e) {
            return Result.fail("新增漫展异常：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> updateComicCon(@PathVariable Long id, @RequestBody ComicCon comicCon) {
        try {
            ComicCon oldComicCon = adminComicConService.getById(id);
            if (oldComicCon == null) {
                return Result.fail("漫展不存在");
            }

            comicCon.setId(id);
            boolean update = adminComicConService.updateById(comicCon);
            if (!update) {
                return Result.fail("修改漫展失败");
            }

            if (comicCon.getProductId() != null && !comicCon.getProductId().equals(oldComicCon.getProductId())) {
                if (oldComicCon.getProductId() != null) {
                    ProductEntity oldProduct = new ProductEntity();
                    oldProduct.setId(oldComicCon.getProductId());
                    oldProduct.setComicConId(null);
                    productMapper.updateById(oldProduct);
                }
                adminComicConService.bindComicConIdToProduct(id, comicCon.getProductId());
            }

            return Result.success("修改漫展成功");
        } catch (Exception e) {
            return Result.fail("修改漫展异常：" + e.getMessage());
        }
    }

    // 删除漫展
    @DeleteMapping("/{id}")
    public String deleteComicCon(@PathVariable Long id) {
        boolean remove = adminComicConService.removeById(id);
        return remove ? "删除成功" : "删除失败";
    }

    // 查询单个漫展
    @GetMapping("/{id}")
    public ComicConVO getComicConById(@PathVariable Long id) {
        return adminComicConService.getComicConVOById(id);
    }

    // 分页查询漫展列表（支持名称模糊查询）
    @GetMapping("/list")
    public IPage<ComicConVO> getComicConList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long productId) {
        // 直接调用带商品名称的查询方法，返回VO
        return adminComicConService.getComicConListWithProductName(pageNum, pageSize, name, productId);
    }

    // 修改漫展状态
    @PutMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        ComicCon comicCon = new ComicCon();
        comicCon.setId(id);
        comicCon.setStatus(status);
        boolean update = adminComicConService.updateById(comicCon);
        return update ? "状态修改成功" : "状态修改失败";
    }
}
