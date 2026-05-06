package com.zk.wardrobe.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class CategoryDTO {
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
     * 分类名称
     */
    private String name;

    /**
     * 是否系统预设(0否 1是)
     */
    private Boolean isDefault;

    /**
     * 分类图片/图标URL (保存图片文件名或相对路径)
     */
    private String imageUrl;

    @TableField(fill = FieldFill.INSERT)
    private Long createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;
}