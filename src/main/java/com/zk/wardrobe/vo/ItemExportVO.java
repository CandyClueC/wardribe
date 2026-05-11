package com.zk.wardrobe.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ItemExportVO {

    @ExcelProperty("单品名称")
    @ColumnWidth(20)
    private String name;

    @ExcelProperty("所属分类")
    @ColumnWidth(15)
    private String categoryName;

    @ExcelProperty("购入价格(元)")
    @ColumnWidth(15)
    private BigDecimal price;

    @ExcelProperty("购买日期")
    @ColumnWidth(15)
    private String purchaseDate;

    @ExcelProperty("发货状态")
    @ColumnWidth(15)
    private String isShipped;

    @ExcelProperty("尾款状态")
    @ColumnWidth(15)
    private String isFinalPaid;
}