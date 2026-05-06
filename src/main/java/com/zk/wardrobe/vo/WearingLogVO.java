package com.zk.wardrobe.vo;

import com.zk.wardrobe.entity.Item;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class WearingLogVO {
    private Long id;
    private LocalDate wearDate;
    
    // 如果有关联预设穿搭，把穿搭的基础信息返回
    private Long lookId;
    private String lookName;
    private String lookImageUrl;
    
    // 这天实实在在穿在身上的所有衣服详情（快照数据）
    private List<Item> wornItems;
}