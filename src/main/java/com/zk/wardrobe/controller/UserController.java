package com.zk.wardrobe.controller;

import com.zk.wardrobe.dto.UserUpdateDTO;
import com.zk.wardrobe.dto.WxLoginRequest;
import com.zk.wardrobe.entity.User;
import com.zk.wardrobe.service.UserService;
import com.zk.wardrobe.utils.Result;
import com.zk.wardrobe.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 微信小程序免密登录
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody WxLoginRequest loginRequest) {
        // Controller 不处理任何逻辑，直接调用 Service
        return Result.success(userService.wxLogin(loginRequest));
    }

    @PostMapping("/update")
    public Result<String> updateUserInfo(@RequestBody UserUpdateDTO dto) {
        // 1. 从 ThreadLocal 或 JwtInterceptor 解析出当前登录的 userId
        Long currentUserId = UserContext.getUserId();

        // 2. 调用 Service 更新数据库里的用户名
        userService.updateNickName(currentUserId, dto);

        return Result.success();
    }

    @GetMapping("/info")
    public Result<User> getUserInfo() {
        return Result.success(userService.getById(UserContext.getUserId()));
    }
}