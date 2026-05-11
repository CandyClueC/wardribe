package com.zk.wardrobe.controller;

import com.zk.wardrobe.dto.LookDTO;
import com.zk.wardrobe.entity.Look;
import com.zk.wardrobe.service.LookService;
import com.zk.wardrobe.utils.Result;
import com.zk.wardrobe.vo.LookDetailVO;
import com.zk.wardrobe.vo.LookListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/look")
public class LookController {

    @Autowired
    private LookService lookService;

    /**
     * 保存穿搭组合
     */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody LookDTO lookDTO) {
        lookService.addLook(lookDTO);
        return Result.success();
    }

    /**
     * 修改穿搭组合 (名称、图片或关联的单品)
     */
    @PostMapping("/update")
    public Result<Void> update(@RequestBody LookDTO lookDTO) {
        lookService.updateLook(lookDTO);
        return Result.success();
    }

    /**
     * 删除穿搭组合
     */
    @PostMapping("/delete")
    public Result<Void> delete(@RequestParam("id") Long id) {
        lookService.deleteLook(id);
        return Result.success();
    }

    /**
     * 获取我的穿搭列表 (包含标签)
     */
    @GetMapping("/list")
    public Result<List<LookListVO>> getList() {
        return Result.success(lookService.getLookList());
    }

    /**
     * 获取穿搭详情 (包含衣服单品列表)
     */
    @GetMapping("/detail")
    public Result<LookDetailVO> getDetail(@RequestParam("id") Long id) {
        return Result.success(lookService.getLookDetail(id));
    }
}