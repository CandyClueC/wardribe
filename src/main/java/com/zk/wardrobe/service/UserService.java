package com.zk.wardrobe.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zk.wardrobe.dto.UserUpdateDTO;
import com.zk.wardrobe.dto.WxLoginRequest;
import com.zk.wardrobe.entity.User;

public interface UserService extends IService<User> {
    
    /**
     * 微信登录业务处理
     * @param request 前端传来的凭证信息
     * @return JWT Token
     */
    String wxLogin(WxLoginRequest request);

    void updateNickName(Long currentUserId, UserUpdateDTO dto);
}