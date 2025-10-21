package io.github.purecaptcha.generator;

import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.Captcha;
import io.github.purecaptcha.core.CaptchaGenerator;
import io.github.purecaptcha.core.CaptchaType;
import io.github.purecaptcha.model.CaptchaResult;
import io.github.purecaptcha.util.AnimatedGifEncoder;
import io.github.purecaptcha.util.ColorUtil;
import io.github.purecaptcha.util.ImageUtil;
import io.github.purecaptcha.util.RandomUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * GIF 动画验证码生成器
 * <p>
 * 支持多帧动画效果,字符随机抖动
 *
 * @author PureCaptcha
 * @version 1.0.0
 */
public class AnimatedGifCaptchaGenerator implements CaptchaGenerator {

    private static final String NUMBERS = "0123456789";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";

    @Override
    public Captcha generate(CaptchaConfig config) {
        try {
            String captchaText = generateCaptchaText(config);
            byte[] gifData = generateAnimatedGif(captchaText, config);

            String answer = captchaText.toLowerCase();

            return new CaptchaResult(
                CaptchaType.ANIMATED_GIF,
                gifData,
                answer,
                config.getWidth(),
                config.getHeight(),
                false
            );

        } catch (IOException e) {
            throw new RuntimeException("生成 GIF 动画验证码失败", e);
        }
    }

    @Override
    public Captcha generate() {
        return generate(CaptchaConfig.defaultConfig());
    }

    @Override
    public CaptchaType getSupportedType() {
        return CaptchaType.ANIMATED_GIF;
    }

    private String generateCaptchaText(CaptchaConfig config) {
        String charset = NUMBERS + UPPERCASE + LOWERCASE;
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < config.getCharLength(); i++) {
            text.append(RandomUtil.randomChar(charset));
        }
        return text.toString();
    }

    /**
     * 生成动画 GIF
     * 包含 5 帧动画,每帧字符位置和旋转角度略有变化
     */
    private byte[] generateAnimatedGif(String text, CaptchaConfig config) throws IOException {
        int width = config.getWidth();
        int height = config.getHeight();
        int frameCount = 5; // 5 帧动画

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();

        encoder.start(baos);
        encoder.setRepeat(0); // 无限循环
        encoder.setDelay(200); // 每帧延迟 200ms

        // 生成多帧
        for (int frame = 0; frame < frameCount; frame++) {
            BufferedImage image = generateFrame(text, config, frame);
            encoder.addFrame(image);
        }

        encoder.finish();
        return baos.toByteArray();
    }

    /**
     * 生成单帧图像
     * 每帧字符会有轻微的位置和角度变化
     */
    private BufferedImage generateFrame(String text, CaptchaConfig config, int frameIndex) {
        int width = config.getWidth();
        int height = config.getHeight();

        BufferedImage image = ImageUtil.createImage(width, height);
        Graphics2D g2d = ImageUtil.getGraphics(image);

        ImageUtil.drawBackground(g2d, width, height, config.getBackgroundColor());
        
        // 绘制装饰性圆圈（每帧略有变化）
        ImageUtil.drawDecorativeCircles(g2d, width, height, 8);
        
        // 绘制现代化干扰线
        ImageUtil.drawModernInterferenceLines(g2d, width, height, config.getInterferenceLineCount());
        
        // 绘制风格化动画字符
        drawStyledCharactersAnimated(g2d, text, config, frameIndex);
        
        // 绘制少量噪点
        ImageUtil.drawNoisePoints(g2d, width, height, config.getNoisePointCount() / 3);

        g2d.dispose();
        return image;
    }

    /**
     * 绘制带动画效果的字符
     * 每帧字符位置和旋转角度略有不同
     */
    private void drawCharactersAnimated(Graphics2D g2d, String text, CaptchaConfig config, int frameIndex) {
        int width = config.getWidth();
        int height = config.getHeight();
        int charCount = text.length();
        int charWidth = width / charCount;

        g2d.setFont(config.getFont());

        for (int i = 0; i < charCount; i++) {
            char c = text.charAt(i);
            Color color = config.getFontColor() != null ? config.getFontColor() : ColorUtil.randomDarkColor();
            g2d.setColor(color);

            // 基础位置
            int baseX = charWidth * i + charWidth / 2;
            int baseY = height / 2;

            // 添加随机抖动(每帧不同)
            int offsetX = (int)(Math.sin(frameIndex * 0.8 + i) * 3);
            int offsetY = (int)(Math.cos(frameIndex * 0.8 + i) * 3);

            int x = baseX + offsetX;
            int y = baseY + offsetY;

            // 随机旋转角度(每帧略有变化)
            double baseAngle = RandomUtil.randomInt(-25, 25);
            double angleOffset = Math.sin(frameIndex * 0.5 + i) * 5;
            double angle = Math.toRadians(baseAngle + angleOffset);

            ImageUtil.drawRotatedText(g2d, String.valueOf(c), x, y, angle);
        }
    }

    /**
     * 绘制镂空风格的带动画效果的字符
     * 每帧字符位置、角度和颜色略有不同
     */
    private void drawStyledCharactersAnimated(Graphics2D g2d, String text, CaptchaConfig config, int frameIndex) {
        int width = config.getWidth();
        int height = config.getHeight();
        int charCount = text.length();

        // 使用清晰的字体，字号稍微减小
        Font styledFont = new Font("Arial", Font.BOLD, 
                                   Math.max(24, config.getFont().getSize() - 4));
        g2d.setFont(styledFont);

        // 获取字体度量信息
        FontMetrics fm = g2d.getFontMetrics(styledFont);
        int ascent = fm.getAscent();
        
        // 计算字符间距
        int charSpacing = 15;
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

            // 添加平滑的动画抖动(每帧不同)
            int offsetX = (int)(Math.sin(frameIndex * 0.5 + i * 0.4) * 2);
            int offsetY = (int)(Math.cos(frameIndex * 0.5 + i * 0.4) * 2);

            int x = currentX + charWidth / 2 + offsetX;
            int y = baseY + offsetY;

            // 动画旋转角度(每帧平滑变化)
            double baseAngle = (i - (charCount - 1) / 2.0) * 3; // 每个字符基础角度不同
            double angleOffset = Math.sin(frameIndex * 0.3 + i * 0.25) * 5;
            double angle = Math.toRadians(baseAngle + angleOffset);

            // 绘制镂空字符
            ImageUtil.drawHollowText(g2d, String.valueOf(c), x, y, angle, charColor);
            
            // 移动到下一个字符位置
            currentX += charWidth + charSpacing;
        }
    }
}
