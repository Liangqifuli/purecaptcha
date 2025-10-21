package io.github.purecaptcha.generator;

import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.Captcha;
import io.github.purecaptcha.core.CaptchaGenerator;
import io.github.purecaptcha.core.CaptchaType;
import io.github.purecaptcha.model.SliderCaptchaResult;
import io.github.purecaptcha.util.BuiltinSliderBackground;
import io.github.purecaptcha.util.ColorUtil;
import io.github.purecaptcha.util.ImageUtil;
import io.github.purecaptcha.util.PuzzleUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 滑动拼图验证码生成器
 * <p>
 * 生成带有拼图块的背景图,用户需要滑动拼图块到正确位置
 *
 * @author PureCaptcha
 * @version 1.0.0
 */
public class SliderCaptchaGenerator implements CaptchaGenerator {

    // 默认验证容差(像素) - 优化为12px，平衡准确性和用户体验
    private static final int DEFAULT_TOLERANCE = 12;

    @Override
    public Captcha generate(CaptchaConfig config) {
        int width = config.getWidth();
        int height = config.getHeight();

        // 创建背景图（支持自定义图片）
        BufferedImage backgroundImage = generateBackgroundImage(width, height, config);

        // 复制一份用于切出拼图块
        BufferedImage originalImage = copyImage(backgroundImage);

        // 获取拼图块尺寸
        int puzzleWidth = PuzzleUtil.getDefaultPuzzleWidth();
        int puzzleHeight = PuzzleUtil.getDefaultPuzzleHeight();

        // 生成随机拼图块位置
        int puzzleX = PuzzleUtil.generateRandomPuzzleX(width, puzzleWidth);
        int puzzleY = PuzzleUtil.generateRandomPuzzleY(height, puzzleHeight);

        // 从原图切出拼图块
        BufferedImage sliderImage = PuzzleUtil.cutPuzzlePiece(originalImage, puzzleX, puzzleY, puzzleWidth, puzzleHeight);

        // 在背景图上绘制切口阴影
        PuzzleUtil.drawPuzzleCutout(backgroundImage, puzzleX, puzzleY, puzzleWidth, puzzleHeight);

        // 使用配置中的容差或默认值
        int tolerance = config.getSliderTolerance() > 0 ? config.getSliderTolerance() : DEFAULT_TOLERANCE;

        // 【新增】保存原始图像用于智能验证
        return new SliderCaptchaResult(
            backgroundImage,
            sliderImage,
            originalImage,  // 保存原始图像
            puzzleX,
            puzzleY,
            puzzleWidth,
            puzzleHeight,
            width,
            height,
            tolerance
        );
    }

    @Override
    public Captcha generate() {
        return generate(CaptchaConfig.builder()
            .width(350)
            .height(200)
            .build());
    }

    @Override
    public CaptchaType getSupportedType() {
        return CaptchaType.SLIDER;
    }

    /**
     * 生成背景图像
     * <p>
     * 优先级：自定义图片路径 > 指定内置背景 > 随机内置背景 > 生成渐变背景
     */
    private BufferedImage generateBackgroundImage(int width, int height, CaptchaConfig config) {
        BufferedImage image;
        
        // 1. 优先：尝试加载自定义背景图片（外部文件）
        String imagePath = config.getSliderBackgroundImagePath();
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            BufferedImage customImage = ImageUtil.loadImageFromFile(imagePath);
            if (customImage != null) {
                image = ImageUtil.scaleImage(customImage, width, height);
                return image;
            } else {
                System.out.println("警告：无法加载自定义背景图片 " + imagePath + "，尝试使用内置背景");
            }
        }
        
        // 2. 其次：使用内置背景图片（打包在jar中）
        BuiltinSliderBackground builtinBg = config.getBuiltinBackground();
        if (builtinBg == null) {
            // 未指定时随机选择一个内置背景
            builtinBg = BuiltinSliderBackground.random();
        }
        
        BufferedImage builtinImage = ImageUtil.loadImageFromResource(builtinBg.getResourcePath());
        if (builtinImage != null) {
            image = ImageUtil.scaleImage(builtinImage, width, height);
            return image;
        } else {
            System.out.println("警告：无法加载内置背景图片，使用默认生成背景");
        }
        
        // 3. 最后：生成默认渐变背景（回退方案）
        image = ImageUtil.createImage(width, height);
        Graphics2D g2d = ImageUtil.getGraphics(image);

        // 绘制渐变背景
        Color color1 = config.getBackgroundColor() != null ? config.getBackgroundColor() : ColorUtil.randomLightColor();
        Color color2 = ColorUtil.randomLightColor();
        GradientPaint gradient = new GradientPaint(0, 0, color1, width, height, color2);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);

        // 添加随机图案(圆形、矩形等)
        drawRandomShapes(g2d, width, height);

        // 添加干扰线
        ImageUtil.drawInterferenceLines(g2d, width, height, 3);

        g2d.dispose();
        return image;
    }

    /**
     * 绘制随机图案
     */
    private void drawRandomShapes(Graphics2D g2d, int width, int height) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制 10-15 个随机形状
        int shapeCount = 10 + (int) (Math.random() * 5);
        for (int i = 0; i < shapeCount; i++) {
            // 随机颜色(半透明)
            Color color = new Color(
                (int) (Math.random() * 255),
                (int) (Math.random() * 255),
                (int) (Math.random() * 255),
                50 + (int) (Math.random() * 100)
            );
            g2d.setColor(color);

            // 随机位置和大小
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);
            int size = 20 + (int) (Math.random() * 50);

            // 随机形状类型
            int shapeType = (int) (Math.random() * 3);
            switch (shapeType) {
                case 0: // 圆形
                    g2d.fillOval(x, y, size, size);
                    break;
                case 1: // 矩形
                    g2d.fillRect(x, y, size, size);
                    break;
                case 2: // 圆角矩形
                    g2d.fillRoundRect(x, y, size, size, 10, 10);
                    break;
            }
        }
    }

    /**
     * 复制图像
     */
    private BufferedImage copyImage(BufferedImage source) {
        BufferedImage copy = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics2D g2d = copy.createGraphics();
        g2d.drawImage(source, 0, 0, null);
        g2d.dispose();
        return copy;
    }
}

