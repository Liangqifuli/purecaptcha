package io.github.purecaptcha.config;

import io.github.purecaptcha.util.BuiltinSliderBackground;

import java.awt.Color;
import java.awt.Font;

/**
 * 验证码配置类
 * <p>
 * 通过Builder模式构建,提供所有可配置项
 *
 * @author PureCaptcha
 * @version 1.0.0
 */
public class CaptchaConfig {

    // 图像尺寸
    private final int width;
    private final int height;

    // 字符配置
    private final int charLength;
    private final String charType;
    private final boolean excludeConfusingChars;
    private final boolean caseSensitive;

    // 算术验证码配置
    private final String operatorType;

    // 中文验证码配置
    private final String dictionaryType;

    // GIF动画配置
    private final int frameCount;
    private final int frameDelay;

    // 样式配置
    private final Color backgroundColor;
    private final Color fontColor;
    private final Font font;
    private final int interferenceLineCount;
    private final int noisePointCount;

    // 滑动拼图配置
    private final String sliderBackgroundImagePath;        // 自定义背景图片路径
    private final BuiltinSliderBackground builtinBackground; // 内置背景图片
    private final int sliderTolerance;                      // 滑动验证容差(像素)

    private CaptchaConfig(Builder builder) {
        this.width = builder.width;
        this.height = builder.height;
        this.charLength = builder.charLength;
        this.charType = builder.charType;
        this.excludeConfusingChars = builder.excludeConfusingChars;
        this.caseSensitive = builder.caseSensitive;
        this.operatorType = builder.operatorType;
        this.dictionaryType = builder.dictionaryType;
        this.frameCount = builder.frameCount;
        this.frameDelay = builder.frameDelay;
        this.backgroundColor = builder.backgroundColor;
        this.fontColor = builder.fontColor;
        this.font = builder.font;
        this.interferenceLineCount = builder.interferenceLineCount;
        this.noisePointCount = builder.noisePointCount;
        this.sliderBackgroundImagePath = builder.sliderBackgroundImagePath;
        this.builtinBackground = builder.builtinBackground;
        this.sliderTolerance = builder.sliderTolerance;
    }

    // Getter方法
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getCharLength() { return charLength; }
    public String getCharType() { return charType; }
    public boolean isExcludeConfusingChars() { return excludeConfusingChars; }
    public boolean isCaseSensitive() { return caseSensitive; }
    public String getOperatorType() { return operatorType; }
    public String getDictionaryType() { return dictionaryType; }
    public int getFrameCount() { return frameCount; }
    public int getFrameDelay() { return frameDelay; }
    public Color getBackgroundColor() { return backgroundColor; }
    public Color getFontColor() { return fontColor; }
    public Font getFont() { return font; }
    public int getInterferenceLineCount() { return interferenceLineCount; }
    public int getNoisePointCount() { return noisePointCount; }
    public String getSliderBackgroundImagePath() { return sliderBackgroundImagePath; }
    public BuiltinSliderBackground getBuiltinBackground() { return builtinBackground; }
    public int getSliderTolerance() { return sliderTolerance; }

    /**
     * 创建Builder
     *
     * @return Builder实例
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 创建默认配置
     *
     * @return 默认配置实例
     */
    public static CaptchaConfig defaultConfig() {
        return new Builder().build();
    }

    /**
     * Builder类
     */
    public static class Builder {
        // 默认值
        private int width = 200;
        private int height = 50;
        private int charLength = 5;
        private String charType = "MIXED"; // NUMERIC, UPPERCASE, LOWERCASE, MIXED
        private boolean excludeConfusingChars = true;
        private boolean caseSensitive = false;
        private String operatorType = "ADD_SUBTRACT"; // ADD_SUBTRACT, ALL
        private String dictionaryType = "IDIOM"; // IDIOM, POETRY, COMMON
        private int frameCount = 5;
        private int frameDelay = 200;
        private Color backgroundColor = Color.WHITE;
        private Color fontColor = null; // null表示随机颜色
        private Font font = new Font("Arial", Font.BOLD, 32);
        private int interferenceLineCount = 3;
        private int noisePointCount = 50;
        private String sliderBackgroundImagePath = null; // null表示使用默认生成的背景
        private BuiltinSliderBackground builtinBackground = null; // null表示随机选择内置背景
        private int sliderTolerance = 12; // 默认12像素容差，平衡准确性和用户体验

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder charLength(int charLength) {
            this.charLength = charLength;
            return this;
        }

        public Builder charType(String charType) {
            this.charType = charType;
            return this;
        }

        public Builder excludeConfusingChars(boolean excludeConfusingChars) {
            this.excludeConfusingChars = excludeConfusingChars;
            return this;
        }

        public Builder caseSensitive(boolean caseSensitive) {
            this.caseSensitive = caseSensitive;
            return this;
        }

        public Builder operatorType(String operatorType) {
            this.operatorType = operatorType;
            return this;
        }

        public Builder dictionaryType(String dictionaryType) {
            this.dictionaryType = dictionaryType;
            return this;
        }

        public Builder frameCount(int frameCount) {
            this.frameCount = frameCount;
            return this;
        }

        public Builder frameDelay(int frameDelay) {
            this.frameDelay = frameDelay;
            return this;
        }

        public Builder backgroundColor(Color backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder fontColor(Color fontColor) {
            this.fontColor = fontColor;
            return this;
        }

        public Builder font(Font font) {
            this.font = font;
            return this;
        }

        public Builder interferenceLineCount(int interferenceLineCount) {
            this.interferenceLineCount = interferenceLineCount;
            return this;
        }

        public Builder noisePointCount(int noisePointCount) {
            this.noisePointCount = noisePointCount;
            return this;
        }

        public Builder sliderBackgroundImagePath(String sliderBackgroundImagePath) {
            this.sliderBackgroundImagePath = sliderBackgroundImagePath;
            return this;
        }

        public Builder builtinBackground(BuiltinSliderBackground builtinBackground) {
            this.builtinBackground = builtinBackground;
            return this;
        }

        public Builder sliderTolerance(int sliderTolerance) {
            this.sliderTolerance = sliderTolerance;
            return this;
        }

        public CaptchaConfig build() {
            return new CaptchaConfig(this);
        }
    }
}
