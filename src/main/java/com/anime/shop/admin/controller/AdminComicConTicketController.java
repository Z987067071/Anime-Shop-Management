package com.anime.shop.admin.controller;

import com.anime.shop.admin.mapper.product.ProductMapper;
import com.anime.shop.admin.service.AdminComicConService;
import com.anime.shop.admin.service.AdminComicConTicketService;
import com.anime.shop.admin.service.AdminProductSkuService;
import com.anime.shop.admin.service.ProductSpecService;
import com.anime.shop.common.Result;
import com.anime.shop.common.ResultCode;
import com.anime.shop.entity.ComicConTicket;
import com.anime.shop.entity.ProductEntity;
import com.anime.shop.entity.ProductSkuEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/comic-con-ticket") // 票种管理接口路径
public class AdminComicConTicketController {

    @Autowired
    private AdminComicConTicketService adminComicConTicketService;

    @Resource
    private AdminProductSkuService adminProductSkuService;

    @Resource
    private ProductSpecService productSpecService;

    @Resource
    private AdminComicConService adminComicConService;

    @Resource
    private ProductMapper productMapper;

    private int getUsedStock(Long productId) {
        List<ComicConTicket> tickets = adminComicConTicketService.lambdaQuery()
                .eq(ComicConTicket::getProductId, productId)
                .eq(ComicConTicket::getStatus, 1)
                .list();
        return tickets.stream()
                .mapToInt(ComicConTicket::getTotalStock)
                .sum();
    }

    // 新增票种
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> addTicket(@RequestBody ComicConTicket ticket) {
        try {
            // 1. 基础校验
            if (ticket.getTotalStock() != null && ticket.getStock() != null) {
                if (ticket.getStock() > ticket.getTotalStock()) {
                    return Result.fail("剩余库存不能大于总库存");
                }
            }
            if (ticket.getComicConId() == null) {
                return Result.fail("漫展ID不能为空");
            }

            // 2. 获取商品ID & 商品信息
            Long productId = adminComicConService.getProductIdByConId(ticket.getComicConId());
            if (productId == null || productId <= 0) {
                return Result.fail("该漫展未配置商品ID，无法创建票种");
            }

            ProductEntity product = productMapper.selectById(productId);
            if (product == null) {
                return Result.fail("关联商品不存在");
            }

            // 3. 核心：票种总库存不能超过商品剩余库存
            int usedStock = getUsedStock(productId);
            int addTotalStock = ticket.getTotalStock();
            int remainStock = product.getRemainStock();

            if (usedStock + addTotalStock > remainStock) {
                return Result.fail("票种总库存超限！\n商品剩余：" + remainStock +
                        "\n已分配：" + usedStock +
                        "\n可新增：" + (remainStock - usedStock));
            }

            // 4. 名称/默认值
            if (ticket.getTicketName() == null || ticket.getTicketName().trim().isEmpty()) {
                return Result.fail("票种名称不能为空");
            }
            if (ticket.getNeedRealName() == null) ticket.setNeedRealName(true);
            if (ticket.getMaxBuy() == null) ticket.setMaxBuy(5);
            if (ticket.getStatus() == null) ticket.setStatus(1);

            // 5. 绑定商品ID
            ticket.setProductId(productId);

            // 6. 保存票种
            boolean saveTicket = adminComicConTicketService.save(ticket);
            if (!saveTicket) {
                return Result.fail("新增票种失败");
            }

            // 7. 创建规格 + SKU
            Long specId = productSpecService.getOrCreateSpecId(ticket.getTicketName());
            if (specId == null) {
                return Result.fail("创建规格失败");
            }

            ProductSkuEntity sku = new ProductSkuEntity();
            sku.setProductId(productId);
            sku.setSpecId(specId);
            sku.setSpecValue(ticket.getTicketName());
            sku.setPrice(ticket.getPrice());
            sku.setStock(ticket.getStock());
            adminProductSkuService.getBaseMapper().insert(sku);

            // 8. 回填 SKU ID
            ComicConTicket updateTicket = new ComicConTicket();
            updateTicket.setId(ticket.getId());
            updateTicket.setSkuId(sku.getId());
            adminComicConTicketService.updateById(updateTicket);

            // 返回
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("ticketId", ticket.getId());
            resultData.put("specId", specId);
            resultData.put("skuId", sku.getId());
            resultData.put("productId", productId);
            resultData.put("ticketName", ticket.getTicketName());
            resultData.put("price", ticket.getPrice());
            resultData.put("stock", ticket.getStock());
            return Result.success(resultData);

        } catch (Exception e) {
            return Result.error(ResultCode.ERROR.getCode(), "新增票种异常：" + e.getMessage());
        }
    }

    // 修改票种
    @PutMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> updateTicket(@PathVariable Long id, @RequestBody ComicConTicket ticket) {
        try {
            // 1. 基础校验
            if (ticket.getTotalStock() != null && ticket.getStock() != null) {
                if (ticket.getStock() > ticket.getTotalStock()) {
                    return Result.fail("剩余库存不能大于总库存");
                }
            }

            // 2. 查询旧票种
            ComicConTicket oldTicket = adminComicConTicketService.getById(id);
            if (oldTicket == null) {
                return Result.fail("票种不存在");
            }

            Long productId = oldTicket.getProductId();
            ProductEntity product = productMapper.selectById(productId);
            if (product == null) {
                return Result.fail("关联商品不存在");
            }

            // 3. 核心：修改后总库存校验
            int oldTotal = oldTicket.getTotalStock();
            int newTotal = ticket.getTotalStock();
            int used = getUsedStock(productId) - oldTotal + newTotal;

            if (used > product.getRemainStock()) {
                return Result.fail("修改后总库存超过商品上限！\n商品剩余：" + product.getRemainStock() +
                        "\n修改后将占用：" + used);
            }

            // 4. 更新票种
            ticket.setId(id);
            boolean updateSuccess = adminComicConTicketService.updateById(ticket);
            if (!updateSuccess) {
                return Result.fail("修改票种失败");
            }

            // 5. 同步 SKU
            if (oldTicket.getSkuId() != null) {
                ProductSkuEntity sku = new ProductSkuEntity();
                sku.setId(oldTicket.getSkuId());
                sku.setPrice(ticket.getPrice() != null ? ticket.getPrice() : oldTicket.getPrice());
                sku.setStock(ticket.getStock() != null ? ticket.getStock() : oldTicket.getStock());

                // 名称变了 → 同步规格
                if (ticket.getTicketName() != null && !ticket.getTicketName().equals(oldTicket.getTicketName())) {
                    Long newSpecId = productSpecService.getOrCreateSpecId(ticket.getTicketName());
                    sku.setSpecId(newSpecId);
                    sku.setSpecValue(ticket.getTicketName());
                }

                adminProductSkuService.getBaseMapper().updateById(sku);
            }

            Map<String, Object> resultData = new HashMap<>();
            resultData.put("ticketId", id);
            resultData.put("updateSuccess", true);
            return Result.success(resultData);

        } catch (Exception e) {
            return Result.error(ResultCode.ERROR.getCode(), "修改票种异常：" + e.getMessage());
        }
    }

    // 删除票种
    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteTicket(@PathVariable Long id) {
        try {
            ComicConTicket ticket = adminComicConTicketService.getById(id);
            if (ticket == null) {
                return Result.fail("票种不存在");
            }

            // 删除票种
            adminComicConTicketService.removeById(id);

            // 删除SKU
            if (ticket.getSkuId() != null) {
                adminProductSkuService.getBaseMapper().deleteById(ticket.getSkuId());
            }

            return Result.success(true);
        } catch (Exception e) {
            return Result.error(ResultCode.ERROR.getCode(), "删除异常：" + e.getMessage());
        }
    }

    // 查询单个票种详情
    @GetMapping("/{id}")
    public Result<ComicConTicket> getTicketById(@PathVariable Long id) {
        try {
            ComicConTicket ticket = adminComicConTicketService.getById(id);
            if (ticket == null) {
                return Result.fail("票种不存在");
            }
            return Result.success(ticket);
        } catch (Exception e) {
            return Result.error(ResultCode.ERROR.getCode(), "查询票种异常：" + e.getMessage());
        }
    }

    // 分页查询票种列表
    @GetMapping("/list")
    public IPage<ComicConTicket> getTicketList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long comicConId, // 按漫展ID筛选
            @RequestParam(required = false) String ticketName) { // 票种名称模糊查
        return adminComicConTicketService.pageTicket(pageNum, pageSize, comicConId, ticketName);
    }

    // 根据漫展ID查询所有票种（无分页）
    @GetMapping("/list-by-con/{comicConId}")
    public List<ComicConTicket> listTicketByConId(@PathVariable Long comicConId) {
        return adminComicConTicketService.listByComicConId(comicConId);
    }

    // 修改票种状态（下架/上架/标记售罄）
    @PutMapping("/{id}/status")
    public Result<?> updateTicketStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        try {
            if (status == null || (status != 0 && status != 1 && status != 2)) {
                return Result.fail("状态参数错误：只能是0(下架)/1(正常)/2(售罄)");
            }

            ComicConTicket oldTicket = adminComicConTicketService.getById(id);
            if (oldTicket == null) {
                return Result.fail("票种不存在");
            }

            // 改为上架时校验库存
            if (status == 1) {
                ProductEntity product = productMapper.selectById(oldTicket.getProductId());
                if (product == null) return Result.fail("商品不存在");

                int used = getUsedStock(oldTicket.getProductId());
                int remain = product.getRemainStock();
                int ticketStock = oldTicket.getTotalStock();

                if (used + ticketStock > remain) {
                    return Result.fail("启用失败：商品库存不足\n剩余：" + remain + " 已占用：" + used);
                }
            }

            ComicConTicket update = new ComicConTicket();
            update.setId(id);
            update.setStatus(status);
            boolean ok = adminComicConTicketService.updateById(update);

            return ok ? Result.success("状态修改成功") : Result.fail("修改失败");
        } catch (Exception e) {
            return Result.fail("修改状态异常：" + e.getMessage());
        }
    }
}
