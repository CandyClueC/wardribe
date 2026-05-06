package com.zk.wardrobe.handler;

import com.zk.wardrobe.utils.JwtUtils;
import com.zk.wardrobe.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 目标方法执行前拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 从请求头中获取 token (微信小程序通常放在 header 的 Authorization 字段)
        String token = request.getHeader("Authorization");

        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("无访问权限，请先登录");
        }

        try {
            // 如果前端传过来的 token 带有 "Bearer " 前缀，需要截取掉
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // 2. 校验并解析 Token
            Long userId = jwtUtils.getUserIdFromToken(token); // 需在 JwtUtils 中实现该方法

            // 3. 将 userId 存入当前线程的上下文中
            UserContext.setUserId(userId);
            
            // 放行，让请求继续往下走到 Controller
            return true;
            
        } catch (Exception e) {
            throw new RuntimeException("Token 已失效或不合法，请重新登录");
        }
    }

    /**
     * 整个请求完成后执行 (无论成功还是报错都会执行)
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 【核心避坑】必须在这里调用 remove！
        // 因为 Tomcat 的线程池会复用线程，如果不清理，上一个用户的 ID 会残留在线程里，造成严重的数据安全事故。
        UserContext.remove();
    }
}