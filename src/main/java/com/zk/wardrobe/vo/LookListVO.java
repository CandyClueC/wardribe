package com.zk.wardrobe.vo;

import lombok.Data;
import java.util.List;

@Data
public class LookListVO {
    private Long id;
    private String name;
    private String imageUrl;     // 穿搭封面图
    private Long createTime;     // 创建时间戳
    
    // 【补充核心】：前端需要的标签数组（比如：["糯米子", "连衣裙"]）
    private List<String> tags;   
}