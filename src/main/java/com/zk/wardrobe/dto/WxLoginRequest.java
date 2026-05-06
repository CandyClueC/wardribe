package com.zk.wardrobe.dto;

import lombok.Data;

@Data
public class WxLoginRequest {
    /**
     * wx.login 获取的临时登录凭证
     */
    private String code;
    
    /**
     * 非必传，如果想在登录时同步更新头像昵称
     */
    private String nickname;
    private String avatarUrl;
}