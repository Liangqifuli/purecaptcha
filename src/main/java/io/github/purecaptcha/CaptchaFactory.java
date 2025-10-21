package io.github.purecaptcha;

import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.Captcha;
import io.github.purecaptcha.core.CaptchaGenerator;
import io.github.purecaptcha.core.CaptchaType;
import io.github.purecaptcha.generator.AlphanumericCaptchaGenerator;
import io.github.purecaptcha.generator.AnimatedGifCaptchaGenerator;
import io.github.purecaptcha.generator.ArithmeticCaptchaGenerator;
import io.github.purecaptcha.generator.ChineseCaptchaGenerator;
import io.github.purecaptcha.generator.SliderCaptchaGenerator;

/**
 * 验证码工厂类
 * <p>
 * 提供统一的验证码生成入口
 *
 * @author PureCaptcha
 * @version 1.0.0
 */
public class CaptchaFactory {

    /**
     * 根据类型创建验证码生成器
     *
     * @param type 验证码类型
     * @return 验证码生成器
     */
    public static CaptchaGenerator getGenerator(CaptchaType type) {
        switch (type) {
            case ALPHANUMERIC:
                return new AlphanumericCaptchaGenerator();
            case ARITHMETIC:
                return new ArithmeticCaptchaGenerator();
            case CHINESE:
                return new ChineseCaptchaGenerator();
            case ANIMATED_GIF:
                return new AnimatedGifCaptchaGenerator();
            case SLIDER:
                return new SliderCaptchaGenerator();
            default:
                throw new IllegalArgumentException("不支持的验证码类型: " + type);
        }
    }

    /**
     * 使用默认配置生成验证码
     *
     * @param type 验证码类型
     * @return 验证码对象
     */
    public static Captcha create(CaptchaType type) {
        return getGenerator(type).generate();
    }

    /**
     * 使用自定义配置生成验证码
     *
     * @param type 验证码类型
     * @param config 配置对象
     * @return 验证码对象
     */
    public static Captcha create(CaptchaType type, CaptchaConfig config) {
        return getGenerator(type).generate(config);
    }
}
