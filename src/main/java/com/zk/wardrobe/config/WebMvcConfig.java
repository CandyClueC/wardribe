package com.zk.wardrobe.config;

import com.zk.wardrobe.handler.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    // --- 这里是我们之前写的 Token 拦截器 ---
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/login",
                        "/upload",     // 【新增】上传接口不需要拦截，因为可能有单独的上传组件
                        "/images/**",  // 【新增】查看图片的静态资源请求绝对不能拦截，否则图片裂开
                        "/error"
                );
    }

    // --- 【这里是新增的：静态资源映射】 ---
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取文件夹的绝对路径 (防止相对路径在不同系统下解析出错)
        String absolutePath = new File(System.getProperty("user.dir") + "/uploads").getAbsolutePath();

        // 配置：如果请求路径是 /images/ 下的所有内容，就映射到硬盘的绝对路径下
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + absolutePath + File.separator);
    }
}