package com.zk.wardrobe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zk.wardrobe.entity.LookItemRel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 穿搭-单品关联 Mapper 接口
 */
@Mapper
public interface LookItemRelMapper extends BaseMapper<LookItemRel> {
    // 空着就行，BaseMapper 里已经有 insert, delete, select 等方法了
}