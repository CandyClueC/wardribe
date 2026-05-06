package com.zk.wardrobe.controller;

import com.zk.wardrobe.dto.CategoryDTO;
import com.zk.wardrobe.dto.CategorySortDTO;
import com.zk.wardrobe.entity.Category;
import com.zk.wardrobe.service.CategoryService;
import com.zk.wardrobe.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody CategoryDTO categoryDTO) {
        categoryService.addCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 修改分类
     */
    @PostMapping("/update")
    public Result<Void> update(@RequestBody CategoryDTO categoryDTO) {
        categoryService.updateCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 删除分类
     */
    @PostMapping("/delete")
    public Result<Void> delete(@RequestParam("id") Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }

    /**
     * 获取当前用户的所有分类
     */
    @GetMapping("/list")
    public Result<List<Category>> getList(@RequestParam(required = false) Integer level,
                                          @RequestParam(required = false) Long parentId) {
        return Result.success(categoryService.getCategoryList(level, parentId));
    }

    /**
     * 获取分类详情
     */
    @GetMapping("/detail")
    public Result<Category> getDetail(@RequestParam("id") Long id) {
        return Result.success(categoryService.getCategoryDetail(id));
    }

    @PostMapping("/updateSort")
    public Result<String> updateSort(@RequestBody List<CategorySortDTO> sortList) {
        if (sortList == null || sortList.isEmpty()) {
            return Result.success("无变动");
        }

        return Result.success(categoryService.updateSort(sortList));
    }
}