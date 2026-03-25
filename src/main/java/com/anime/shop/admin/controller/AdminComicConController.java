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
@RequestMapping("/api/admin/comic-con") // 路径也加admin，和类名对应，更规范
public class AdminComicConController {

    @Autowired
    private AdminComicConService adminComicConService; // 注入调整后的Service

    @Autowired
    private ProductMapper productMapper;

    // 新增漫展
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class) // 事务保证：新增漫展+更新商品原子操作
    public Result<String> addComicCon(@RequestBody ComicCon comicCon) {
        try {
            // 1. 校验：漫展必须关联商品ID（product_id不能为空）
            if (comicCon.getProductId() == null || comicCon.getProductId() <= 0) {
                return Result.fail("新增漫展失败：必须关联商品ID（product_id）");
            }

            // 2. 新增漫展
            boolean save = adminComicConService.save(comicCon);
            if (!save) {
                return Result.fail("新增漫展失败");
            }

            // 3. 核心：自动将漫展ID回填到商品的comic_con_id字段
            boolean bindSuccess = adminComicConService.bindComicConIdToProduct(comicCon.getId(), comicCon.getProductId());
            if (!bindSuccess) {
                return Result.fail("新增漫展成功，但绑定商品漫展ID失败（商品可能不存在）");
            }

            return Result.success("新增漫展成功，并已绑定商品漫展ID");
        } catch (Exception e) {
            return Result.fail("新增漫展异常：" + e.getMessage());
        }
    }

    // 修改漫展信息
    @PutMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> updateComicCon(@PathVariable Long id, @RequestBody ComicCon comicCon) {
        try {
            // 1. 查询原漫展信息
            ComicCon oldComicCon = adminComicConService.getById(id);
            if (oldComicCon == null) {
                return Result.fail("漫展不存在");
            }

            // 2. 更新漫展
            comicCon.setId(id);
            boolean update = adminComicConService.updateById(comicCon);
            if (!update) {
                return Result.fail("修改漫展失败");
            }

            // 3. 如果product_id变更，重新绑定商品的comic_con_id
            if (comicCon.getProductId() != null && !comicCon.getProductId().equals(oldComicCon.getProductId())) {
                // 先清空旧商品的comic_con_id
                if (oldComicCon.getProductId() != null) {
                    ProductEntity oldProduct = new ProductEntity();
                    oldProduct.setId(oldComicCon.getProductId());
                    oldProduct.setComicConId(null);
                    productMapper.updateById(oldProduct);
                }
                // 绑定新商品的comic_con_id
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
