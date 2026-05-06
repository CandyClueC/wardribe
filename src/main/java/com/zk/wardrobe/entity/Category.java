package com.zk.wardrobe.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 衣服分类实体类
 */
@Data
@TableName("category")
public class Category {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 所属用户ID (0表示系统预设)
     */
    private Long userId;

    /**
     * 父级分类ID
     */
    private Long parentId;

    /**
     * 分类层级 (例如: 1-一级分类/娃娃, 2-二级分类/部位)
     */
    private Integer level;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类图片/图标URL (保存图片文件名或相对路径)
     */
    private String imageUrl;

    /**
     * 排序权重 (数字越小越靠前)
     */
    private Integer sort;

    /**
     * 是否系统预设(0否 1是)
     */
    private Boolean isDefault;

    @TableField(fill = FieldFill.INSERT)
    private Long createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;
}