package io.github.purecaptcha.core;

import io.github.purecaptcha.config.CaptchaConfig;

/**
 * 验证码生成器接口
 * <p>
 * 所有验证码生成器的顶层接口,定义统一的生成方法
 *
 * @author PureCaptcha
 * @version 1.0.0
 */
public interface CaptchaGenerator {

    /**
     * 生成验证码
     *
     * @param config 验证码配置
     * @return 验证码对象
     */
    Captcha generate(CaptchaConfig config);

    /**
     * 使用默认配置生成验证码
     *
     * @return 验证码对象
     */
    Captcha generate();

    /**
     * 获取生成器支持的验证码类型
     *
     * @return 验证码类型
     */
    CaptchaType getSupportedType();
}
