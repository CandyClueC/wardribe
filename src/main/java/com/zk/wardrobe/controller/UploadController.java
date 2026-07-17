package com.zk.wardrobe.controller;

import com.zk.wardrobe.service.WxContentSecurityService;
import com.zk.wardrobe.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
public class UploadController {

    @Autowired
    private WxContentSecurityService wxContentSecurityService;

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        try {
            // 1. 获取原文件名及后缀 (例如: my_shirt.png -> .png)
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            
            // 限制一下上传类型，防止黑客上传 .sh 或 .exe 脚本
            if (!suffix.equalsIgnoreCase(".jpg") && !suffix.equalsIgnoreCase(".jpeg") && !suffix.equalsIgnoreCase(".png")) {
                return Result.error("仅支持 JPG/PNG 格式的图片");
            }

            // 2. 内容安全审核
            try {
                wxContentSecurityService.checkImage(file.getBytes());
            } catch (RuntimeException e) {
                return Result.error(e.getMessage());
            }

            // 3. 生成全局唯一的全新文件名 (例如: f8a9e2b1-1234.png)
            String newFileName = UUID.randomUUID().toString().replace("-", "") + suffix;

            // 3. 构建物理存储路径，如果文件夹不存在则自动创建
            String path = System.getProperty("user.dir") + "/uploads";
            File directory = new File(path);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 4. 将文件保存到硬盘
            File destFile = new File(directory, newFileName);
            file.transferTo(destFile);

            // 返回最终的图片文件名
            return Result.success(newFileName);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
}