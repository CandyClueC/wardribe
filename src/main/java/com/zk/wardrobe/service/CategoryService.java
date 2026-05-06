package com.zk.wardrobe.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zk.wardrobe.dto.CategoryDTO;
import com.zk.wardrobe.dto.CategorySortDTO;
import com.zk.wardrobe.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    void addCategory(CategoryDTO categoryDTO);
    void updateCategory(CategoryDTO categoryDTO);
    void deleteCategory(Long id);
    List<Category> getCategoryList(Integer level, Long parentId);
    Category getCategoryDetail(Long id);

    String updateSort(List<CategorySortDTO> sortList);
}