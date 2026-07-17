package com.zk.wardrobe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        // BufferingClientHttpRequestFactory 确保请求体先缓冲再发送，
        // 避免 HttpURLConnection 使用分块传输编码，兼容微信 API
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));

        // 遍历所有的 HttpMessageConverter
        for (HttpMessageConverter<?> converter : restTemplate.getMessageConverters()) {
            // 找到专门处理 JSON 的 MappingJackson2HttpMessageConverter
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter jsonConverter = (MappingJackson2HttpMessageConverter) converter;

                // 获取它原先支持的媒体类型列表 (通常是 application/json)
                List<MediaType> supportedMediaTypes = new ArrayList<>(jsonConverter.getSupportedMediaTypes());

                // 【核心修复】把 text/plain 也加进去，让它把文本也当 JSON 解析
                supportedMediaTypes.add(MediaType.TEXT_PLAIN);
                // 如果微信哪天抽风返回 text/html，也可以顺手加上防范于未然
                supportedMediaTypes.add(MediaType.TEXT_HTML);

                jsonConverter.setSupportedMediaTypes(supportedMediaTypes);
            }
        }

        return restTemplate;
    }
}