package com.zk.wardrobe.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 穿搭单品关联实体类
 */
@Data
@TableName("look_item_rel")
public class LookItemRel {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 穿搭ID
     */
    private Long lookId;

    /**
     * 单品ID
     */
    private Long itemId;

    @TableField(fill = FieldFill.INSERT)
    private Long createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;
}