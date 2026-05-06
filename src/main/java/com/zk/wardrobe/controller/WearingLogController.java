package com.zk.wardrobe.controller;

import com.zk.wardrobe.dto.WearingLogDTO;
import com.zk.wardrobe.service.WearingLogService;
import com.zk.wardrobe.utils.Result;
import com.zk.wardrobe.vo.WearingLogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/wearingLog")
public class WearingLogController {

    @Autowired
    private WearingLogService wearingLogService;

    @PostMapping("/add")
    public Result<Void> add(@RequestBody WearingLogDTO logDTO) {
        wearingLogService.addLog(logDTO);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody WearingLogDTO logDTO) {
        wearingLogService.updateLog(logDTO);
        return Result.success();
    }

    @PostMapping("/delete")
    public Result<Void> delete(@RequestParam("id") Long id) {
        wearingLogService.deleteLog(id);
        return Result.success();
    }

    /**
     * 按月份获取日历记录 (前端画日历看板用)
     * 请求示例: GET /wearingLog/listByMonth?year=2023&month=10
     */
    @GetMapping("/listByMonth")
    public Result<List<WearingLogVO>> getListByMonth(
            @RequestParam("year") Integer year, 
            @RequestParam("month") Integer month) {
        return Result.success(wearingLogService.getLogsByMonth(year, month));
    }

    @GetMapping("/detail")
    public Result<WearingLogVO> getDetail(@RequestParam("id") Long id) {
        return Result.success(wearingLogService.getLogDetail(id));
    }
}