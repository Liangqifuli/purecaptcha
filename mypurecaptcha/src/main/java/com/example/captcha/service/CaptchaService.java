package com.example.captcha.service;

import io.github.purecaptcha.CaptchaFactory;
import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.Captcha;
import io.github.purecaptcha.core.CaptchaType;
import io.github.purecaptcha.model.SliderCaptchaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务层（Redis版本 - 优化序列化）
 * <p>
 * ⭐ 核心改进：不直接存储BufferedImage，避免Jackson序列化错误
 * 
 * <h3>核心特性</h3>
 * <ul>
 *   <li>✅ Redis存储：只存储必要的验证数据（答案、type、时间戳）</li>
 *   <li>✅ 滑动验证码：存储byte[]而不是BufferedImage</li>
 *   <li>✅ 多验证码并存：不同类型的验证码可以同时存在</li>
 *   <li>✅ 自动过期：Redis TTL=3分钟</li>
 *   <li>✅ 验证后删除：验证成功后立即删除</li>
 * </ul>
 * 
 * @author PureCaptcha Team
 * @version 3.0.0 (优化序列化)
 * @since 2025-10-21
 */
@Service
public class CaptchaService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Value("${captcha.expire.minutes:3}")
    private long expireMinutes;

    /**
     * 生成Redis Key
     * <p>
     * 格式：captcha:{sessionId}:{type}
     * 
     * @param sessionId 会话ID
     * @param type 验证码类型
     * @return Redis Key
     */
    private String generateRedisKey(String sessionId, CaptchaType type) {
        return String.format("captcha:%s:%s", sessionId, type.name());
    }

    /**
     * 生成验证码
     * 
     * @param sessionId 会话ID
     * @param type 验证码类型
     * @return 验证码数据
     * @throws IOException 图片处理异常
     */
    public Object generateCaptcha(String sessionId, CaptchaType type) throws IOException {
        System.out.println("📝 生成验证码 - SessionID: " + sessionId + ", 类型: " + type);

        // 根据类型配置验证码
        CaptchaConfig config = switch (type) {
            case ALPHANUMERIC -> CaptchaConfig.builder()
                    .width(200)
                    .height(80)
                    .build();
            case ARITHMETIC -> CaptchaConfig.builder()
                    .width(200)
                    .height(80)
                    .operatorType("ALL")
                    .build();
            case CHINESE -> CaptchaConfig.builder()
                    .width(200)
                    .height(80)
                    .charLength(4)
                    .build();
            case ANIMATED_GIF -> CaptchaConfig.builder()
                    .width(200)
                    .height(80)
                    .frameCount(5)
                    .build();
            case SLIDER -> CaptchaConfig.builder()
                    .width(350)
                    .height(200)
                    .sliderTolerance(12)
                    .build();
        };
        
        Captcha captcha = CaptchaFactory.create(type, config);

        // ⭐ 使用简化的数据结构存储到Redis（避免BufferedImage序列化错误）
        String redisKey = generateRedisKey(sessionId, type);
        SimpleCaptchaData simpleCaptchaData;
        
        // 滑动验证码需要特殊处理
        if (type == CaptchaType.SLIDER) {
            SliderCaptchaResult sliderCaptcha = (SliderCaptchaResult) captcha;
            
            // ⭐ 只存储必要的验证信息（不存储图片）
            simpleCaptchaData = new SimpleCaptchaData(
                captcha.getAnswer(),
                type,
                System.currentTimeMillis(),
                sliderCaptcha.getSliderX(),
                sliderCaptcha.getSliderY(),
                null,  // 不存储原始图片，节省Redis空间
                config.getSliderTolerance()
            );
            
            System.out.println("  ├─ Redis Key: " + redisKey);
            System.out.println("  ├─ 过期时间: " + expireMinutes + "分钟");
            System.out.println("  ├─ 滑动位置: X=" + sliderCaptcha.getSliderX() + ", Y=" + sliderCaptcha.getSliderY());
            System.out.println("  └─ 容差: " + config.getSliderTolerance() + "px");

            // 存储到Redis
            redisTemplate.opsForValue().set(redisKey, simpleCaptchaData, expireMinutes, TimeUnit.MINUTES);

            // 返回图片数据给前端
            String backgroundData = convertImageToBase64(sliderCaptcha.getBackgroundImage(), "PNG");
            String sliderData = convertImageToBase64(sliderCaptcha.getSliderImage(), "PNG");

            Map<String, Object> result = new HashMap<>();
            result.put("backgroundImage", backgroundData);
            result.put("sliderImage", sliderData);
            result.put("sliderY", sliderCaptcha.getSliderY());
            result.put("sessionId", sessionId);
            result.put("type", type.name());
            return result;
        }

        // 其他类型验证码只需存储答案
        simpleCaptchaData = new SimpleCaptchaData(
            captcha.getAnswer(),
            type,
            System.currentTimeMillis(),
            0, 0, null, 0
        );
        
        System.out.println("  ├─ Redis Key: " + redisKey);
        System.out.println("  ├─ 过期时间: " + expireMinutes + "分钟");
        System.out.println("  └─ 验证码答案: " + captcha.getAnswer());
        
        // 存储到Redis
        redisTemplate.opsForValue().set(redisKey, simpleCaptchaData, expireMinutes, TimeUnit.MINUTES);

        // 返回图片数据给前端
        Map<String, Object> result = new HashMap<>();
        result.put("image", convertCaptchaToBase64(captcha, type));
        result.put("sessionId", sessionId);
        result.put("type", type.name());
        return result;
    }

    /**
     * 验证用户输入
     * <p>
     * ⭐ 验证成功后立即删除Redis中的验证码
     * 
     * @param sessionId 会话ID
     * @param type 验证码类型
     * @param userAnswer 用户答案
     * @return 验证结果
     */
    public boolean verifyCaptcha(String sessionId, CaptchaType type, String userAnswer) {
        // ⭐ 根据type从Redis获取验证码
        String redisKey = generateRedisKey(sessionId, type);
        SimpleCaptchaData captchaData = (SimpleCaptchaData) redisTemplate.opsForValue().get(redisKey);
        
        if (captchaData == null) {
            System.out.println("❌ 验证失败: SessionID " + sessionId + " 类型 " + type + " 的验证码不存在或已过期");
            System.out.println("  └─ Redis Key: " + redisKey);
            return false;
        }

        try {
            boolean passed;
            
            // 滑动验证码使用坐标验证
            if (type == CaptchaType.SLIDER) {
                int userX = Integer.parseInt(userAnswer);
                int correctX = captchaData.sliderX;
                int tolerance = captchaData.tolerance;
                
                // ⭐ 坐标验证（简化版本）
                int deviation = Math.abs(userX - correctX);
                passed = deviation <= tolerance;
                
                System.out.println("🎯 " + type + " 验证 - SessionID: " + sessionId);
                System.out.println("  ├─ 用户位置: " + userX);
                System.out.println("  ├─ 正确位置: " + correctX);
                System.out.println("  ├─ 偏差: " + deviation + "px (容差: " + tolerance + "px)");
                System.out.println("  └─ 结果: " + (passed ? "通过 ✅" : "失败 ❌"));
            } else {
                // 其他类型验证码（大小写不敏感）
                String correctAnswer = captchaData.answer;
                passed = correctAnswer.equalsIgnoreCase(userAnswer);
                System.out.println("🎯 " + type + " 验证 - SessionID: " + sessionId);
                System.out.println("  ├─ Redis Key: " + redisKey);
                System.out.println("  ├─ 答案: " + correctAnswer);
                System.out.println("  ├─ 用户输入: " + userAnswer);
                System.out.println("  └─ 结果: " + (passed ? "通过 ✅" : "失败 ❌"));
            }
            
            // ⭐ 验证后立即删除该类型的验证码（无论成功还是失败）
            redisTemplate.delete(redisKey);
            System.out.println("  └─ 已删除Redis Key: " + redisKey);
            
            return passed;
        } catch (NumberFormatException e) {
            System.out.println("❌ 验证失败: 用户输入 '" + userAnswer + "' 不是有效的数字");
            // 删除验证码
            redisTemplate.delete(redisKey);
            return false;
        }
    }

    /**
     * 简化的验证码数据（可序列化）
     * <p>
     * ⭐ 不包含BufferedImage，避免Jackson序列化错误
     */
    public static class SimpleCaptchaData implements Serializable {
        private static final long serialVersionUID = 1L;
        
        public String answer;           // 验证码答案
        public CaptchaType type;        // 验证码类型
        public long createTime;         // 创建时间
        
        // 滑动验证码专用字段
        public int sliderX;             // 滑块X坐标
        public int sliderY;             // 滑块Y坐标
        public byte[] originalImageBytes; // 原始图片的字节数组
        public int tolerance;           // 容差
        
        public SimpleCaptchaData() {
        }
        
        public SimpleCaptchaData(String answer, CaptchaType type, long createTime,
                                int sliderX, int sliderY, byte[] originalImageBytes, int tolerance) {
            this.answer = answer;
            this.type = type;
            this.createTime = createTime;
            this.sliderX = sliderX;
            this.sliderY = sliderY;
            this.originalImageBytes = originalImageBytes;
            this.tolerance = tolerance;
        }
    }

    // ==================== 辅助方法 ====================

    private String convertImageToBase64(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        return "data:image/" + format.toLowerCase() + ";base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    private String convertCaptchaToBase64(Captcha captcha, CaptchaType type) throws IOException {
        byte[] imageBytes;
        String mimeType;

        if (type == CaptchaType.ANIMATED_GIF) {
            imageBytes = captcha.getImageData();
            mimeType = "image/gif";
        } else {
            BufferedImage image = captcha.getImage();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            imageBytes = baos.toByteArray();
            mimeType = "image/png";
        }
        return "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(imageBytes);
    }
}
