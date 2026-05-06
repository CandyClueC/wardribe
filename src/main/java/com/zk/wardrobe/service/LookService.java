package com.zk.wardrobe.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zk.wardrobe.dto.LookDTO;
import com.zk.wardrobe.entity.Look;
import com.zk.wardrobe.vo.LookDetailVO;

import java.util.List;

public interface LookService extends IService<Look> {
    void addLook(LookDTO lookDTO);
    void updateLook(LookDTO lookDTO);
    void deleteLook(Long id);
    List<Look> getLookList();
    LookDetailVO getLookDetail(Long id);
}