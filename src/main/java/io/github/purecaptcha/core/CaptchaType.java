package io.github.purecaptcha.core;

/**
 * 验证码类型枚举
 *
 * @author PureCaptcha
 * @version 1.0.0
 */
public enum CaptchaType {

    /**
     * 字符验证码(数字、字母或混合)
     */
    ALPHANUMERIC("字符验证码"),

    /**
     * 算术验证码(四则运算)
     */
    ARITHMETIC("算术验证码"),

    /**
     * 中文验证码(成语、诗词、常用词)
     */
    CHINESE("中文验证码"),

    /**
     * GIF动画验证码
     */
    ANIMATED_GIF("GIF动画验证码"),

    /**
     * 滑动拼图验证码
     */
    SLIDER("滑动拼图验证码");

    private final String description;

    CaptchaType(String description) {
        this.description = description;
    }

    /**
     * 获取验证码类型描述
     *
     * @return 类型描述
     */
    public String getDescription() {
        return description;
    }
}
