package com.zk.wardrobe.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ItemDTO {
    private String name;        // 衣服名称
    private Long id;            // 修改时必传
    private Long categoryId;    // 分类ID
    private String imageUrl;    // 衣服图片
    private BigDecimal price;   // 价格
    private String season;      // 季节（如：春,夏）
    private Integer status;     // 状态
    private Integer isShipped;  // 发货状态
    private Integer isFinalPaid;    // 尾款状态
    private Long purchaseDate;  // 购买时间
}