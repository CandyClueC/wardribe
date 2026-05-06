package com.zk.wardrobe.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class WearingLogDTO {
    private Long id;
    private LocalDate wearDate; // 穿着日期 (必传)
    private Long lookId;        // 关联的穿搭ID (选传)
    private List<Long> itemIds; // 额外添加或散装选择的单品ID集合 (选传)
}