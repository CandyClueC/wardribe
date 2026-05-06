package com.zk.wardrobe.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.wardrobe.entity.LookItemRel;
import com.zk.wardrobe.mapper.LookItemRelMapper;
import com.zk.wardrobe.service.LookItemRelService;
import org.springframework.stereotype.Service;

/**
 * 穿搭-单品关联 Service 实现类
 */
@Service
public class LookItemRelServiceImpl extends ServiceImpl<LookItemRelMapper, LookItemRel> implements LookItemRelService {
    // 保持为空，父类 ServiceImpl 已经把增删改查的代码全写好了
}