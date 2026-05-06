package com.zk.wardrobe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zk.wardrobe.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
