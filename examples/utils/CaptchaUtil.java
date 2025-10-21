package utils;

import io.github.purecaptcha.CaptchaFactory;
import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.Captcha;
import io.github.purecaptcha.core.CaptchaType;
import io.github.purecaptcha.model.SliderCaptchaResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用验证码工具类
 * 
 * <p>功能：</p>
 * <ul>
 *   <li>支持5种验证码：字符、算术、中文、GIF动图、滑动拼图</li>
 *   <li>自动管理验证码生命周期（5分钟过期）</li>
 *   <li>线程安全的验证码存储</li>
 *   <li>简单易用的API</li>
 * </ul>
 * 
 * <p>使用示例：</p>
 * <pre>
 * // 1. 生成验证码
 * Map&lt;String, Object&gt; result = CaptchaUtil.generate("ARITHMETIC");
 * String captchaId = (String) result.get("captchaId");
 * String imageBase64 = (String) result.get("imageBase64");
 * 
 * // 2. 验证答案
 * boolean isValid = CaptchaUtil.verify(captchaId, userAnswer);
 * 
 * // 3. 清理过期验证码（可选，自动执行）
 * CaptchaUtil.cleanExpired();
 * </pre>
 * 
 * @author PureCaptcha
 * @version 1.0.0
 */
public class CaptchaUtil {
    
    /**
     * 验证码存储容器（线程安全）
     */
    private static final Map<String, CaptchaWrapper> CAPTCHA_STORE = new ConcurrentHashMap<>();
    
    /**
     * 验证码过期时间（毫秒）- 默认5分钟
     */
    private static final long EXPIRATION_TIME = 5 * 60 * 1000;
    
    /**
     * 自动清理线程是否已启动
     */
    private static volatile boolean cleanerStarted = false;
    
    /**
     * 验证码包装类，存储验证码对象和创建时间
     */
    private static class CaptchaWrapper {
        final Captcha captcha;
        final long createTime;
        
        CaptchaWrapper(Captcha captcha) {
            this.captcha = captcha;
            this.createTime = System.currentTimeMillis();
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() - createTime > EXPIRATION_TIME;
        }
    }
    
    // 启动自动清理线程
    static {
        startCleaner();
    }
    
    /**
     * 生成验证码（使用默认配置）
     * 
     * @param type 验证码类型：ALPHANUMERIC（字符）、ARITHMETIC（算术）、
     *             CHINESE（中文）、ANIMATED_GIF（GIF动图）、SLIDER（滑动拼图）
     * @return 包含验证码信息的Map
     *         - captchaId: 验证码唯一ID
     *         - type: 验证码类型
     *         - imageBase64: 验证码图片（Base64编码）
     *         - sliderY: 滑动验证码的Y坐标（仅SLIDER类型）
     *         - answer: 验证码答案（开发调试用，生产环境不返回）
     */
    public static Map<String, Object> generate(String type) {
        CaptchaConfig config = CaptchaConfig.builder().build();
        return generate(type, config);
    }
    
    /**
     * 生成验证码（自定义配置）
     * 
     * @param type 验证码类型
     * @param config 自定义配置
     * @return 包含验证码信息的Map
     */
    public static Map<String, Object> generate(String type, CaptchaConfig config) {
        // 解析验证码类型
        CaptchaType captchaType = parseCaptchaType(type);
        
        // 生成验证码
        Captcha captcha = CaptchaFactory.create(captchaType, config);
        
        // 生成唯一ID
        String captchaId = UUID.randomUUID().toString().replace("-", "");
        
        // 存储验证码
        CAPTCHA_STORE.put(captchaId, new CaptchaWrapper(captcha));
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("captchaId", captchaId);
        result.put("type", type);
        
        // 根据类型处理图片
        if (captchaType == CaptchaType.SLIDER && captcha instanceof SliderCaptchaResult) {
            SliderCaptchaResult sliderResult = (SliderCaptchaResult) captcha;
            result.put("imageBase64", imageToBase64(sliderResult.getBackgroundImage()));
            result.put("sliderImageBase64", imageToBase64(sliderResult.getSliderImage()));
            result.put("sliderY", sliderResult.getSliderY());
        } else {
            result.put("imageBase64", imageToBase64(captcha.getImage()));
        }
        
        // 开发调试：返回答案（生产环境应删除此行）
        result.put("answer", captcha.getAnswer());
        
        return result;
    }
    
    /**
     * 验证验证码答案
     * 
     * @param captchaId 验证码ID
     * @param userAnswer 用户输入的答案
     * @return true-验证成功，false-验证失败
     */
    public static boolean verify(String captchaId, String userAnswer) {
        if (captchaId == null || userAnswer == null) {
            return false;
        }
        
        // 获取验证码
        CaptchaWrapper wrapper = CAPTCHA_STORE.remove(captchaId);
        if (wrapper == null) {
            return false; // 验证码不存在或已使用
        }
        
        // 检查是否过期
        if (wrapper.isExpired()) {
            return false;
        }
        
        // 验证答案
        return wrapper.captcha.verify(userAnswer);
    }
    
    /**
     * 验证滑动验证码位置
     * 
     * @param captchaId 验证码ID
     * @param userX 用户滑动的X坐标
     * @return true-验证成功，false-验证失败
     */
    public static boolean verifySlider(String captchaId, int userX) {
        if (captchaId == null) {
            return false;
        }
        
        // 获取验证码（不删除，允许重试）
        CaptchaWrapper wrapper = CAPTCHA_STORE.get(captchaId);
        if (wrapper == null) {
            return false;
        }
        
        // 检查是否过期
        if (wrapper.isExpired()) {
            CAPTCHA_STORE.remove(captchaId);
            return false;
        }
        
        // 验证位置
        Captcha captcha = wrapper.captcha;
        if (captcha instanceof SliderCaptchaResult) {
            SliderCaptchaResult sliderResult = (SliderCaptchaResult) captcha;
            boolean result = sliderResult.verifyPosition(userX);
            
            // 验证成功后删除
            if (result) {
                CAPTCHA_STORE.remove(captchaId);
            }
            
            return result;
        }
        
        return false;
    }
    
    /**
     * 获取验证码调试信息（滑动验证码专用）
     * 
     * @param captchaId 验证码ID
     * @return 调试信息Map，包含正确位置、容差等信息
     */
    public static Map<String, Object> getSliderDebugInfo(String captchaId) {
        Map<String, Object> info = new HashMap<>();
        
        CaptchaWrapper wrapper = CAPTCHA_STORE.get(captchaId);
        if (wrapper != null && wrapper.captcha instanceof SliderCaptchaResult) {
            SliderCaptchaResult sliderResult = (SliderCaptchaResult) wrapper.captcha;
            info.put("correctPosition", sliderResult.getSliderX());
            info.put("tolerance", 12); // 默认容差
            info.put("expired", wrapper.isExpired());
        } else {
            info.put("error", "验证码不存在或不是滑动类型");
        }
        
        return info;
    }
    
    /**
     * 删除指定验证码
     * 
     * @param captchaId 验证码ID
     */
    public static void remove(String captchaId) {
        CAPTCHA_STORE.remove(captchaId);
    }
    
    /**
     * 清理过期的验证码
     * 
     * @return 清理的数量
     */
    public static int cleanExpired() {
        int count = 0;
        for (Map.Entry<String, CaptchaWrapper> entry : CAPTCHA_STORE.entrySet()) {
            if (entry.getValue().isExpired()) {
                CAPTCHA_STORE.remove(entry.getKey());
                count++;
            }
        }
        return count;
    }
    
    /**
     * 获取当前验证码数量
     * 
     * @return 验证码数量
     */
    public static int getCount() {
        return CAPTCHA_STORE.size();
    }
    
    /**
     * 清空所有验证码
     */
    public static void clear() {
        CAPTCHA_STORE.clear();
    }
    
    /**
     * 启动自动清理线程
     */
    private static void startCleaner() {
        if (cleanerStarted) {
            return;
        }
        
        synchronized (CaptchaUtil.class) {
            if (cleanerStarted) {
                return;
            }
            
            Thread cleanerThread = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(60000); // 每分钟清理一次
                        int cleaned = cleanExpired();
                        if (cleaned > 0) {
                            System.out.println("CaptchaUtil: 清理了 " + cleaned + " 个过期验证码，当前剩余: " + getCount());
                        }
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }, "CaptchaUtil-Cleaner");
            
            cleanerThread.setDaemon(true);
            cleanerThread.start();
            cleanerStarted = true;
        }
    }
    
    /**
     * 解析验证码类型
     */
    private static CaptchaType parseCaptchaType(String type) {
        if (type == null) {
            return CaptchaType.ALPHANUMERIC;
        }
        
        switch (type.toUpperCase()) {
            case "ALPHANUMERIC":
                return CaptchaType.ALPHANUMERIC;
            case "ARITHMETIC":
                return CaptchaType.ARITHMETIC;
            case "CHINESE":
                return CaptchaType.CHINESE;
            case "ANIMATED_GIF":
            case "GIF":
                return CaptchaType.ANIMATED_GIF;
            case "SLIDER":
                return CaptchaType.SLIDER;
            default:
                return CaptchaType.ALPHANUMERIC;
        }
    }
    
    /**
     * 将BufferedImage转换为Base64字符串
     */
    private static String imageToBase64(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException("图片转换失败", e);
        }
    }
    
    /**
     * 快速生成字符验证码
     */
    public static Map<String, Object> alphanumeric() {
        return generate("ALPHANUMERIC");
    }
    
    /**
     * 快速生成算术验证码
     */
    public static Map<String, Object> arithmetic() {
        return generate("ARITHMETIC");
    }
    
    /**
     * 快速生成算术验证码（支持乘除）
     * 
     * @param useAllOperators true-使用加减乘除，false-仅使用加减
     */
    public static Map<String, Object> arithmetic(boolean useAllOperators) {
        CaptchaConfig config = CaptchaConfig.builder()
                .operatorType(useAllOperators ? "ALL" : "ADD_SUBTRACT")
                .build();
        return generate("ARITHMETIC", config);
    }
    
    /**
     * 快速生成中文验证码
     */
    public static Map<String, Object> chinese() {
        return generate("CHINESE");
    }
    
    /**
     * 快速生成GIF动图验证码
     */
    public static Map<String, Object> gif() {
        return generate("ANIMATED_GIF");
    }
    
    /**
     * 快速生成滑动拼图验证码
     */
    public static Map<String, Object> slider() {
        return generate("SLIDER");
    }
    
    /**
     * 快速生成滑动拼图验证码（自定义背景）
     * 
     * @param backgroundPath 背景图片路径（可以是文件路径或classpath路径）
     */
    public static Map<String, Object> slider(String backgroundPath) {
        CaptchaConfig config = CaptchaConfig.builder()
                .sliderBackgroundImagePath(backgroundPath)
                .build();
        return generate("SLIDER", config);
    }
}

