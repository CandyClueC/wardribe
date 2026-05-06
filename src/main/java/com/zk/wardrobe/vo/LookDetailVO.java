package com.zk.wardrobe.vo;

import com.zk.wardrobe.entity.Item;
import lombok.Data;
import java.util.List;

/**
 * 返回给前端的穿搭详情
 */
@Data
public class LookDetailVO {
    private Long id;
    private String name;
    private String imageUrl;
    private Long createTime;
    
    // 重点：详情里需要把这套穿搭用到的具体衣服信息也返回给前端
    private List<Item> itemList;
}