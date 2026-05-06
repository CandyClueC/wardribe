package com.zk.wardrobe.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zk.wardrobe.dto.ItemDTO;
import com.zk.wardrobe.entity.Item;
import com.zk.wardrobe.vo.CategoryPriceVO;

import java.math.BigDecimal;
import java.util.List;

public interface ItemService extends IService<Item> {
    void addItem(ItemDTO itemDTO);
    void updateItem(ItemDTO itemDTO);
    void deleteItem(Long id);
    Item getItemDetail(Long id);
    List<Item> getItemList(Long categoryId, String season, Integer status, Long parentId, Integer isShipped, Integer isFinalPaid);
    BigDecimal getTotalPrice(Long categoryId);
    List<CategoryPriceVO> getCategoryPriceStats();

    Integer getTotalCount(Long categoryId);
}