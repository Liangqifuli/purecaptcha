package io.github.purecaptcha.generator;

import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.Captcha;
import io.github.purecaptcha.core.CaptchaGenerator;
import io.github.purecaptcha.core.CaptchaType;
import io.github.purecaptcha.model.CaptchaResult;
import io.github.purecaptcha.util.ColorUtil;
import io.github.purecaptcha.util.ImageUtil;
import io.github.purecaptcha.util.RandomUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 字符验证码生成器
 * <p>
 * 支持数字、字母或混合字符验证码
 *
 * @author PureCaptcha
 * @version 1.0.0
 */
public class AlphanumericCaptchaGenerator implements CaptchaGenerator {

    // 字符集常量
    private static final String NUMBERS = "0123456789";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";

    // 易混淆字符
    private static final String CONFUSING_CHARS = "0O1lI2Z5S8B";

    @Override
    public Captcha generate(CaptchaConfig config) {
        // 生成验证码文本
        String captchaText = generateCaptchaText(config);

        // 创建图像
        BufferedImage image = ImageUtil.createImage(config.getWidth(), config.getHeight());
        Graphics2D g2d = ImageUtil.getGraphics(image);

        // 绘制背景
        ImageUtil.drawBackground(g2d, config.getWidth(), config.getHeight(),
                                config.getBackgroundColor());

        // 绘制装饰性圆圈（添加现代化背景）
        ImageUtil.drawDecorativeCircles(g2d, config.getWidth(), config.getHeight(), 8);

        // 绘制现代化干扰线
        ImageUtil.drawModernInterferenceLines(g2d, config.getWidth(), config.getHeight(),
                                             config.getInterferenceLineCount());

        // 绘制字符（使用新的风格化方法）
        drawStyledCharacters(g2d, captchaText, config);

        // 绘制少量噪点（减少以保持清爽）
        ImageUtil.drawNoisePoints(g2d, config.getWidth(), config.getHeight(),
                                 config.getNoisePointCount() / 3);

        g2d.dispose();

        // 返回结果(如果不区分大小写,答案转为小写)
        String answer = config.isCaseSensitive() ? captchaText : captchaText.toLowerCase();

        return new CaptchaResult(CaptchaType.ALPHANUMERIC, image, answer,
                               config.isCaseSensitive());
    }

    @Override
    public Captcha generate() {
        return generate(CaptchaConfig.defaultConfig());
    }

    @Override
    public CaptchaType getSupportedType() {
        return CaptchaType.ALPHANUMERIC;
    }

    /**
     * 生成验证码文本
     *
     * @param config 配置
     * @return 验证码文本
     */
    private String generateCaptchaText(CaptchaConfig config) {
        String charset = getCharset(config);
        StringBuilder text = new StringBuilder();

        for (int i = 0; i < config.getCharLength(); i++) {
            text.append(RandomUtil.randomChar(charset));
        }

        return text.toString();
    }

    /**
     * 根据配置获取字符集
     *
     * @param config 配置
     * @return 字符集
     */
    private String getCharset(CaptchaConfig config) {
        String charset;

        switch (config.getCharType()) {
            case "NUMERIC":
                charset = NUMBERS;
                break;
            case "UPPERCASE":
                charset = UPPERCASE;
                break;
            case "LOWERCASE":
                charset = LOWERCASE;
                break;
            case "MIXED":
            default:
                charset = NUMBERS + UPPERCASE + LOWERCASE;
                break;
        }

        // 排除易混淆字符
        if (config.isExcludeConfusingChars()) {
            charset = excludeConfusingChars(charset);
        }

        return charset;
    }

    /**
     * 排除易混淆字符
     *
     * @param charset 原字符集
     * @return 过滤后的字符集
     */
    private String excludeConfusingChars(String charset) {
        StringBuilder filtered = new StringBuilder();

        for (char c : charset.toCharArray()) {
            if (CONFUSING_CHARS.indexOf(c) == -1) {
                filtered.append(c);
            }
        }

        return filtered.toString();
    }

    /**
     * 绘制字符
     *
     * @param g2d Graphics2D对象
     * @param text 验证码文本
     * @param config 配置
     */
    private void drawCharacters(Graphics2D g2d, String text, CaptchaConfig config) {
        int width = config.getWidth();
        int height = config.getHeight();
        int charCount = text.length();

        // 计算每个字符的宽度
        int charWidth = width / charCount;

        // 设置字体
        g2d.setFont(config.getFont());

        for (int i = 0; i < charCount; i++) {
            char c = text.charAt(i);

            // 设置颜色(如果配置了固定颜色则使用,否则随机)
            Color color = config.getFontColor() != null
                    ? config.getFontColor()
                    : ColorUtil.randomDarkColor();
            g2d.setColor(color);

            // 计算字符位置(添加随机偏移)
            int x = charWidth * i + RandomUtil.randomInt(charWidth / 4, charWidth / 2);
            int y = height / 2 + RandomUtil.randomInt(-height / 6, height / 6);

            // 随机旋转角度(-30度到30度)
            double angle = Math.toRadians(RandomUtil.randomInt(-30, 30));

            // 绘制旋转的字符
            ImageUtil.drawRotatedText(g2d, String.valueOf(c), x, y, angle);
        }
    }

    /**
     * 绘制风格化字符（镂空效果、鲜艳颜色、合理间距）
     *
     * @param g2d Graphics2D对象
     * @param text 验证码文本
     * @param config 配置
     */
    private void drawStyledCharacters(Graphics2D g2d, String text, CaptchaConfig config) {
        int width = config.getWidth();
        int height = config.getHeight();
        int charCount = text.length();

        // 使用清晰的字体，字号稍微减小以避免臃肿
        Font styledFont = new Font("Arial", Font.BOLD, 
                                   Math.max(24, config.getFont().getSize() - 4));
        g2d.setFont(styledFont);

        // 获取字体度量信息
        FontMetrics fm = g2d.getFontMetrics(styledFont);
        int ascent = fm.getAscent();
        
        // 计算总宽度（增加字符间距）
        int charSpacing = 15; // 字符间距增加到15px，避免紧密
        int totalCharsWidth = 0;
        for (int i = 0; i < charCount; i++) {
            totalCharsWidth += fm.charWidth(text.charAt(i));
        }
        totalCharsWidth += (charCount - 1) * charSpacing;
        
        // 计算起始X位置，使字符整体居中
        int startX = (width - totalCharsWidth) / 2;
        
        // Y轴居中位置
        int baseY = (height + ascent) / 2;

        int currentX = startX;
        for (int i = 0; i < charCount; i++) {
            char c = text.charAt(i);
            int charWidth = fm.charWidth(c);

            // 每个字符使用鲜艳的随机颜色
            Color charColor = config.getFontColor() != null
                    ? config.getFontColor()
                    : ColorUtil.randomVibrantColor();

            // 计算字符位置（添加轻微随机偏移）
            int x = currentX + charWidth / 2 + RandomUtil.randomInt(-2, 2);
            int y = baseY + RandomUtil.randomInt(-4, 4);

            // 随机旋转角度（范围更小，保持可读性）
            double angle = Math.toRadians(RandomUtil.randomInt(-15, 15));

            // 绘制镂空字符（只有轮廓，中间镂空）
            ImageUtil.drawHollowText(g2d, String.valueOf(c), x, y, angle, charColor);
            
            // 移动到下一个字符位置
            currentX += charWidth + charSpacing;
        }
    }
}
