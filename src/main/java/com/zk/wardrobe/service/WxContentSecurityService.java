package com.zk.wardrobe.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@Slf4j
@Service
public class WxContentSecurityService {

    /** 微信 img_sec_check 接口限制 1MB，留余量用 800KB */
    private static final int MAX_CHECK_SIZE = 800 * 1024;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${wx.miniapp.appid}")
    private String appid;

    @Value("${wx.miniapp.secret}")
    private String secret;

    /** 缓存的 access token */
    private String cachedToken;

    /** token 过期时间戳（毫秒） */
    private long tokenExpireTime;

    private final Object lock = new Object();

    /**
     * 同步审核图片内容。
     * 审核通过 → 正常返回；审核不通过 / API 异常 → 抛出 RuntimeException。
     *
     * @param imageBytes 图片原始字节（送审时如超过微信限制会自动压缩）
     */
    public void checkImage(byte[] imageBytes) {
        String token = getAccessToken();
        String url = "https://api.weixin.qq.com/wxa/img_sec_check?access_token=" + token;

        // 如果超过微信限制，先压缩再送审
        byte[] checkBytes = imageBytes;
        if (imageBytes.length > MAX_CHECK_SIZE) {
            try {
                checkBytes = compressForCheck(imageBytes);
                log.info("图片送审压缩: {}KB → {}KB", imageBytes.length / 1024, checkBytes.length / 1024);
            } catch (IOException e) {
                log.error("图片压缩失败", e);
                throw new RuntimeException("图片处理异常，请稍后重试");
            }
        }

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("media", new ByteArrayResource(checkBytes) {
            @Override
            public String getFilename() {
                return "check.jpg";
            }
        });

        Map<String, Object> response;
        try {
            response = restTemplate.postForObject(url, body, Map.class);
        } catch (Exception e) {
            log.error("图片审核服务异常", e);
            throw new RuntimeException("图片审核服务异常，请稍后重试");
        }

        if (response == null) {
            throw new RuntimeException("图片审核服务异常，请稍后重试");
        }

        Object errcodeObj = response.get("errcode");
        int errcode = errcodeObj instanceof Number ? ((Number) errcodeObj).intValue() : -1;

        if (errcode != 0) {
            String errmsg = (String) response.getOrDefault("errmsg", "内容违规");
            throw new RuntimeException("图片审核不通过: " + errmsg);
        }
    }

    /**
     * 压缩图片到微信审核限制以内。
     * 策略：等比缩放到 1280px + JPEG 质量压缩
     */
    private byte[] compressForCheck(byte[] imageBytes) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        if (image == null) {
            throw new IOException("无法解析图片");
        }

        // 1. 等比缩放到最大 1280px
        int maxDimension = 1280;
        int w = image.getWidth();
        int h = image.getHeight();
        if (w > maxDimension || h > maxDimension) {
            double scale = Math.min((double) maxDimension / w, (double) maxDimension / h);
            w = (int) (w * scale);
            h = (int) (h * scale);
            BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = scaled.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(image, 0, 0, w, h, null);
            g.dispose();
            image = scaled;
        }

        // 2. JPEG 质量压缩，从 0.6 开始逐步降低直到满足大小限制
        float quality = 0.6f;
        byte[] result;
        do {
            result = encodeJpeg(image, quality);
            quality -= 0.1f;
        } while (result.length > MAX_CHECK_SIZE && quality >= 0.1f);

        if (result.length > MAX_CHECK_SIZE) {
            throw new IOException("图片压缩后仍超过审核大小限制");
        }

        return result;
    }

    private byte[] encodeJpeg(BufferedImage image, float quality) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
        if (!writers.hasNext()) {
            throw new IOException("无 JPEG 编码器");
        }
        ImageWriter writer = writers.next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(bos)) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(image, null, null), param);
            writer.dispose();
        }
        return bos.toByteArray();
    }

    /**
     * 获取有效的 access token（带内存缓存，提前 5 分钟刷新）。
     */
    private String getAccessToken() {
        long now = System.currentTimeMillis();

        if (cachedToken != null && now < tokenExpireTime - 5 * 60 * 1000L) {
            return cachedToken;
        }

        synchronized (lock) {
            if (cachedToken != null && now < tokenExpireTime - 5 * 60 * 1000L) {
                return cachedToken;
            }

            String url = String.format(
                    "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
                    appid, secret
            );

            Map<String, Object> response;
            try {
                response = restTemplate.getForObject(url, Map.class);
            } catch (Exception e) {
                throw new RuntimeException("获取微信access_token失败，请稍后重试");
            }

            if (response == null || response.containsKey("errcode")) {
                String errmsg = response != null
                        ? (String) response.getOrDefault("errmsg", "未知错误")
                        : "未知错误";
                throw new RuntimeException("获取微信access_token失败: " + errmsg);
            }

            cachedToken = (String) response.get("access_token");
            Integer expiresIn = (Integer) response.get("expires_in");
            tokenExpireTime = now + expiresIn * 1000L;

            return cachedToken;
        }
    }
}
