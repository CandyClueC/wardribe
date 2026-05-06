package com.zk.wardrobe.controller;

import com.zk.wardrobe.dto.ItemDTO;
import com.zk.wardrobe.entity.Item;
import com.zk.wardrobe.service.ItemService;
import com.zk.wardrobe.utils.Result;
import com.zk.wardrobe.vo.CategoryPriceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    // ================= 1. 录入与修改 (POST) =================

    @PostMapping("/add")
    public Result<Void> add(@RequestBody ItemDTO itemDTO) {
        itemService.addItem(itemDTO);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody ItemDTO itemDTO) {
        itemService.updateItem(itemDTO);
        return Result.success();
    }

    @PostMapping("/delete")
    public Result<Void> delete(@RequestParam("id") Long id) {
        itemService.deleteItem(id);
        return Result.success();
    }

    // ================= 2. 查询 (GET + @RequestParam) =================

    /**
     * 获取衣服详情
     * 请求示例: GET /item/detail?id=12345
     */
    @GetMapping("/detail")
    public Result<Item> getDetail(@RequestParam("id") Long id) {
        return Result.success(itemService.getItemDetail(id));
    }

    /**
     * 获取单品列表（带多条件分类筛选）
     * 请求示例: GET /item/list?categoryId=1&season=春&status=0
     * 注：参数都是非必传(required = false)，不传就查该用户所有的衣服
     */
    @GetMapping("/list")
    public Result<List<Item>> getList(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "season", required = false) String season,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(name = "parentId", required = false) Long parentId,
            @RequestParam(name = "isShipped", required = false) Integer isShipped,
            @RequestParam(name = "isFinalPaid", required = false) Integer isFinalPaid) {

        List<Item> list = itemService.getItemList(categoryId, season, status, parentId, isShipped, isFinalPaid);
        return Result.success(list);
    }

    // ================= 3. 统计 (GET + @RequestParam) =================

    /**
     * 获取衣服总价值
     * 请求示例: GET /item/totalPrice?categoryId=123 (传categoryId则查该分类总价，不传则查衣橱总价)
     */
    @GetMapping("/totalPrice")
    public Result<BigDecimal> getTotalPrice(@RequestParam(value = "categoryId", required = false) Long categoryId) {
        return Result.success(itemService.getTotalPrice(categoryId));
    }

    /**
     * 获取衣橱总件数
     */
    @GetMapping("/totalCount")
    public Result<Integer> getTotalCount(@RequestParam(value = "categoryId", required = false) Long categoryId) {
        return Result.success(itemService.getTotalCount(categoryId));
    }

    /**
     * 获取衣橱资产统计看板（按分类聚合计算）
     * 请求示例: GET /item/categoryPriceStats
     */
    @GetMapping("/categoryPriceStats")
    public Result<List<CategoryPriceVO>> getCategoryPriceStats() {
        return Result.success(itemService.getCategoryPriceStats());
    }
}