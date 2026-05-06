package com.zk.wardrobe.dto;

import lombok.Data;
import java.util.List;

/**
 * 接收前端创建/修改穿搭的入参
 */
@Data
public class LookDTO {
    private Long id;              // 修改时必传
    private String name;          // 穿搭名称 (如："周一通勤")
    private String imageUrl;      // 前端合成好的穿搭图片地址
    private List<Long> itemIds;   // 该穿搭包含的衣服单品 ID 列表
}