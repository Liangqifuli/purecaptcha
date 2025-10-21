package com.example.captcha.service;

import io.github.purecaptcha.CaptchaFactory;
import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.Captcha;
import io.github.purecaptcha.core.CaptchaType;
import io.github.purecaptcha.model.SliderCaptchaResult;
import io.github.purecaptcha.util.BuiltinSliderBackground;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务
 * 
 * 功能：
 * 1. 生成各类验证码（字符、算术、中文、GIF、滑动）
 * 2. 验证用户输入（滑动验证码使用智能验证算法）
 * 3. 自动清理过期验证码
 * 
 * @author PureCaptcha
 */
@Service
public class CaptchaService {

    /**
     * 验证码存储（包含创建时间）
     */
    private final Map<String, CaptchaWrapper> captchaStore = new ConcurrentHashMap<>();
    
    /**
     * 定时清理器
     */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    /**
     * 验证码过期时间（毫秒）
     */
    private static final long EXPIRE_TIME = 5 * 60 * 1000; // 5分钟
    
    /**
     * 验证码包装类（记录创建时间）
     */
    private static class CaptchaWrapper {
        Captcha captcha;
        long createTime;
        
        CaptchaWrapper(Captcha captcha) {
            this.captcha = captcha;
            this.createTime = System.currentTimeMillis();
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() - createTime > EXPIRE_TIME;
        }
    }
    
    /**
     * 构造函数 - 启动定时清理任务
     */
    public CaptchaService() {
        // 每分钟清理一次过期验证码
        scheduler.scheduleAtFixedRate(() -> {
            captchaStore.entrySet().removeIf(entry -> entry.getValue().isExpired());
            System.out.println("验证码清理完成，当前数量: " + captchaStore.size());
        }, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * 生成验证码
     * 
     * @param sessionId 会话ID
     * @param type 验证码类型
     * @return 验证码数据（Base64或包含多张图片的Map）
     * @throws IOException IO异常
     */
    public Object generateCaptcha(String sessionId, CaptchaType type) throws IOException {
        // 生成验证码（使用默认配置）
        Captcha captcha = CaptchaFactory.create(type);

        // 保存完整对象（支持智能验证）
        captchaStore.put(sessionId, new CaptchaWrapper(captcha));

        // 滑动验证码需要返回两张图片
        if (type == CaptchaType.SLIDER) {
            return generateSliderResponse((SliderCaptchaResult) captcha);
        }

        // 其他类型验证码返回单张图片Base64
        return generateImageResponse(captcha, type);
    }

    /**
     * 生成滑动验证码（自定义配置）
     * 
     * @param sessionId 会话ID
     * @param width 宽度
     * @param height 高度
     * @param useBuiltinBg 是否使用内置背景
     * @return 滑动验证码数据
     * @throws IOException IO异常
     */
    public Object generateSliderCaptcha(String sessionId, int width, int height, boolean useBuiltinBg) throws IOException {
        CaptchaConfig.Builder configBuilder = CaptchaConfig.builder()
                .width(width)
                .height(height)
                .sliderTolerance(15); // 容差15px
        
        // 随机使用内置背景（11张）
        if (useBuiltinBg) {
            configBuilder.builtinBackground(BuiltinSliderBackground.random());
        }
        
        Captcha captcha = CaptchaFactory.create(CaptchaType.SLIDER, configBuilder.build());
        captchaStore.put(sessionId, new CaptchaWrapper(captcha));
        
        return generateSliderResponse((SliderCaptchaResult) captcha);
    }

    /**
     * 验证用户输入
     * 
     * @param sessionId 会话ID
     * @param userAnswer 用户答案
     * @return 是否通过验证
     */
    public boolean verifyCaptcha(String sessionId, String userAnswer) {
        CaptchaWrapper wrapper = captchaStore.get(sessionId);
        
        if (wrapper == null) {
            System.out.println("验证失败：验证码不存在 - " + sessionId);
            return false;
        }
        
        if (wrapper.isExpired()) {
            captchaStore.remove(sessionId);
            System.out.println("验证失败：验证码已过期 - " + sessionId);
            return false;
        }

        try {
            Captcha captcha = wrapper.captcha;
            
            // 滑动验证码使用智能验证（图像相似度匹配）
            if (captcha instanceof SliderCaptchaResult) {
                SliderCaptchaResult sliderCaptcha = (SliderCaptchaResult) captcha;
                int userX = Integer.parseInt(userAnswer);
                
                // 智能验证（自动使用图像相似度算法）
                boolean passed = sliderCaptcha.verifyPosition(userX);
                
                System.out.println(String.format(
                    "滑动验证 - SessionID: %s, 用户位置: %d, 正确位置: %d, 结果: %s",
                    sessionId, userX, sliderCaptcha.getSliderX(), passed ? "通过" : "失败"
                ));
                
                return passed;
            }
            
            // 其他类型验证码
            return captcha.verify(userAnswer);
            
        } catch (NumberFormatException e) {
            System.out.println("验证失败：答案格式错误 - " + userAnswer);
            return false;
        } finally {
            // 验证后删除（一次性使用）
            captchaStore.remove(sessionId);
        }
    }

    /**
     * 获取验证详情（调试用）
     * 
     * @param sessionId 会话ID
     * @param userAnswer 用户答案
     * @return 详细验证信息
     */
    public String getVerifyDetails(String sessionId, String userAnswer) {
        CaptchaWrapper wrapper = captchaStore.get(sessionId);
        
        if (wrapper == null) {
            return "验证码不存在或已过期";
        }
        
        if (wrapper.isExpired()) {
            return "验证码已过期";
        }

        Captcha captcha = wrapper.captcha;
        
        if (captcha instanceof SliderCaptchaResult) {
            SliderCaptchaResult sliderCaptcha = (SliderCaptchaResult) captcha;
            try {
                int userX = Integer.parseInt(userAnswer);
                return sliderCaptcha.getVerifyDetails(userX);
            } catch (NumberFormatException e) {
                return "用户输入格式错误: " + userAnswer;
            }
        }
        
        return "不支持的验证码类型";
    }

    /**
     * 清理所有验证码（测试用）
     */
    public void clearAll() {
        captchaStore.clear();
    }

    /**
     * 获取当前验证码数量（监控用）
     */
    public int getCaptchaCount() {
        return captchaStore.size();
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 生成滑动验证码响应
     */
    private Map<String, Object> generateSliderResponse(SliderCaptchaResult sliderCaptcha) throws IOException {
        // 转换背景图为Base64
        String backgroundData = imageToBase64(sliderCaptcha.getBackgroundImage(), "PNG");
        
        // 转换拼图块为Base64
        String sliderData = imageToBase64(sliderCaptcha.getSliderImage(), "PNG");

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("backgroundImage", backgroundData);
        result.put("sliderImage", sliderData);
        result.put("sliderY", sliderCaptcha.getSliderY());
        
        return result;
    }

    /**
     * 生成普通图片验证码响应
     */
    private String generateImageResponse(Captcha captcha, CaptchaType type) throws IOException {
        byte[] imageBytes;
        String mimeType;

        if (type == CaptchaType.ANIMATED_GIF) {
            // GIF 动画
            imageBytes = captcha.getImageData();
            mimeType = "image/gif";
        } else {
            // 静态图片
            BufferedImage image = captcha.getImage();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            imageBytes = baos.toByteArray();
            mimeType = "image/png";
        }

        return "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * 图片转Base64
     */
    private String imageToBase64(BufferedImage image, String format) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, format, baos);
        return "data:image/" + format.toLowerCase() + ";base64," + 
               Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}

