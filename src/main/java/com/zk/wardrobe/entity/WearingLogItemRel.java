package com.zk.wardrobe.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 日历单品记录实体类
 */
@Data
@TableName("wearing_log_item_rel")
public class WearingLogItemRel {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 关联日历记录ID
     */
    private Long logId;

    /**
     * 单品ID
     */
    private Long itemId;

    @TableField(fill = FieldFill.INSERT)
    private Long createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;
}