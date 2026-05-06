package com.zk.wardrobe.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zk.wardrobe.dto.UserUpdateDTO;
import com.zk.wardrobe.dto.WxLoginRequest;
import com.zk.wardrobe.entity.User;
import com.zk.wardrobe.mapper.UserMapper;
import com.zk.wardrobe.service.UserService;
import com.zk.wardrobe.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${wx.miniapp.appid}")
    private String appid;

    @Value("${wx.miniapp.secret}")
    private String secret;

    @Override
    public String wxLogin(WxLoginRequest request) {
        // 1. 调用微信接口换取 openid
        String url = String.format(
            "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
            appid, secret, request.getCode()
        );
        
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        
        if (response == null || response.containsKey("errcode")) {
            // 这里建议抛出自定义异常，由全局异常处理器捕获后返回 Result.error()
            // 简单起见，这里抛出 RuntimeException
            throw new RuntimeException("微信登录验证失败: " + response.get("errmsg"));
        }

        String openid = (String) response.get("openid");

        // 2. 根据 openid 查询数据库是否存在该用户
        User user = this.getOne(new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));

        // 3. 如果是新用户，则自动执行注册逻辑
        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setNickname(request.getNickname());
            user.setAvatarUrl(request.getAvatarUrl());
            this.save(user); // 继承自 ServiceImpl 的方法
        }

        // 4. 生成 JWT Token 并返回给 Controller
        return jwtUtils.createToken(user.getId());
    }

    @Override
    public void updateNickName(Long currentUserId, UserUpdateDTO dto) {
        User user = this.getOne(new LambdaQueryWrapper<User>().eq(User::getId, currentUserId));
        user.setNickname(dto.getNickname());
        user.setAvatarUrl(dto.getAvatarUrl());
        this.updateById(user);
    }
}