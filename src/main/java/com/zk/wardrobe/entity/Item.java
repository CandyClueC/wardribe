package com.zk.wardrobe.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 衣服单品实体类
 */
@Data
@TableName("item")
public class Item {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 所属用户ID
     */
    private Long userId;

    /**
     * 衣服名称
     */
    private String name;

    /**
     * 所属分类ID
     */
    private Long categoryId;

    /**
     * 衣服原图地址
     */
    private String imageUrl;

    /**
     * 购买价格
     */
    private BigDecimal price;

    /**
     * 购买时间
     */
    private Long purchaseDate;

    /**
     * 季节标签(如:春,夏,秋,冬)
     */
    private String season;

    /**
     * 状态(0:在库, 1:待洗, 2:外借, 3:断舍离)
     */
    private Integer status;

    /**
     * 发货状态 (0:未发货, 1:已发货)
     */
    private Integer isShipped;

    /**
     * 尾款状态 (0:未补, 1:已补)
     */
    private Integer isFinalPaid;

    @TableField(fill = FieldFill.INSERT)
    private Long createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;
}