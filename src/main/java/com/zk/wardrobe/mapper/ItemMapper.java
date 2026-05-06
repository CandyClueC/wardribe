package com.zk.wardrobe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zk.wardrobe.entity.Item;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ItemMapper extends BaseMapper<Item> {
}
