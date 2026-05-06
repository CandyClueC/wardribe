package com.zk.wardrobe.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zk.wardrobe.dto.WearingLogDTO;
import com.zk.wardrobe.entity.WearingLog;
import com.zk.wardrobe.vo.WearingLogVO;

import java.util.List;

public interface WearingLogService extends IService<WearingLog> {
    void addLog(WearingLogDTO logDTO);
    void updateLog(WearingLogDTO logDTO);
    void deleteLog(Long id);
    List<WearingLogVO> getLogsByMonth(Integer year, Integer month);
    WearingLogVO getLogDetail(Long id);
}