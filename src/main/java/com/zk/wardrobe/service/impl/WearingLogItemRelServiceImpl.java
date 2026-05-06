package com.zk.wardrobe.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.wardrobe.entity.WearingLogItemRel;
import com.zk.wardrobe.mapper.WearingLogItemRelMapper;
import com.zk.wardrobe.service.WearingLogItemRelService;
import org.springframework.stereotype.Service;

@Service
public class WearingLogItemRelServiceImpl extends ServiceImpl<WearingLogItemRelMapper, WearingLogItemRel> implements WearingLogItemRelService {}