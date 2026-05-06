package com.zk.wardrobe.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CategoryPriceVO {
    /**
     * 分类ID
     */
    private Long categoryId;
    /**
     * 分类名称
     */
    private String categoryName;
    /**
     * 该分类下的衣服总数量
     */
    private Integer itemCount;
    /**
     * 该分类的总价值
     */
    private BigDecimal totalPrice;
}