package io.github.purecaptcha.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图像工具类
 * <p>
 * 提供图像绘制相关的通用功能
 *
 * @author PureCaptcha
 * @version 1.0.0
 */
public class ImageUtil {

    /**
     * 创建BufferedImage并开启抗锯齿
     *
     * @param width 宽度
     * @param height 高度
     * @return BufferedImage对象
     */
    public static BufferedImage createImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    /**
     * 获取Graphics2D并配置渲染提示
     *
     * @param image BufferedImage对象
     * @return 配置好的Graphics2D对象
     */
    public static Graphics2D getGraphics(BufferedImage image) {
        Graphics2D g2d = image.createGraphics();

        // 开启抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 高质量渲染
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                            RenderingHints.VALUE_RENDER_QUALITY);

        return g2d;
    }

    /**
     * 绘制背景
     *
     * @param g2d Graphics2D对象
     * @param width 宽度
     * @param height 高度
     * @param color 背景颜色
     */
    public static void drawBackground(Graphics2D g2d, int width, int height, Color color) {
        g2d.setColor(color);
        g2d.fillRect(0, 0, width, height);
    }

    /**
     * 绘制干扰线
     *
     * @param g2d Graphics2D对象
     * @param width 图像宽度
     * @param height 图像高度
     * @param count 干扰线数量
     */
    public static void drawInterferenceLines(Graphics2D g2d, int width, int height, int count) {
        for (int i = 0; i < count; i++) {
            g2d.setColor(ColorUtil.randomColor());
            g2d.setStroke(new BasicStroke(RandomUtil.randomInt(1, 3)));

            int x1 = RandomUtil.randomInt(width);
            int y1 = RandomUtil.randomInt(height);
            int x2 = RandomUtil.randomInt(width);
            int y2 = RandomUtil.randomInt(height);

            g2d.drawLine(x1, y1, x2, y2);
        }
    }

    /**
     * 绘制噪点
     *
     * @param g2d Graphics2D对象
     * @param width 图像宽度
     * @param height 图像高度
     * @param count 噪点数量
     */
    public static void drawNoisePoints(Graphics2D g2d, int width, int height, int count) {
        for (int i = 0; i < count; i++) {
            g2d.setColor(ColorUtil.randomColor());
            int x = RandomUtil.randomInt(width);
            int y = RandomUtil.randomInt(height);
            g2d.fillRect(x, y, 1, 1);
        }
    }

    /**
     * 绘制旋转文字
     *
     * @param g2d Graphics2D对象
     * @param text 文字内容
     * @param x X坐标
     * @param y Y坐标
     * @param angle 旋转角度(弧度)
     */
    public static void drawRotatedText(Graphics2D g2d, String text, int x, int y, double angle) {
        // 保存原始变换
        java.awt.geom.AffineTransform oldTransform = g2d.getTransform();

        // 旋转
        g2d.rotate(angle, x, y);
        g2d.drawString(text, x, y);

        // 恢复变换
        g2d.setTransform(oldTransform);
    }

    /**
     * 绘制带描边和阴影的文字（现代化风格）
     *
     * @param g2d Graphics2D对象
     * @param text 文字内容
     * @param x X坐标
     * @param y Y坐标
     * @param angle 旋转角度(弧度)
     * @param fillColor 填充颜色
     * @param outlineColor 描边颜色
     */
    public static void drawStyledText(Graphics2D g2d, String text, int x, int y, double angle,
                                     Color fillColor, Color outlineColor) {
        // 保存原始变换
        java.awt.geom.AffineTransform oldTransform = g2d.getTransform();
        
        // 旋转到指定角度
        g2d.rotate(angle, x, y);
        
        // 获取字体轮廓
        Font font = g2d.getFont();
        java.awt.font.FontRenderContext frc = g2d.getFontRenderContext();
        java.awt.font.GlyphVector gv = font.createGlyphVector(frc, text);
        java.awt.Shape shape = gv.getOutline(x, y);
        
        // 1. 绘制阴影（偏移3像素）
        g2d.translate(3, 3);
        g2d.setColor(ColorUtil.withAlpha(Color.BLACK, 30));
        g2d.fill(shape);
        g2d.translate(-3, -3);
        
        // 2. 绘制描边
        g2d.setColor(outlineColor);
        g2d.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(shape);
        
        // 3. 绘制填充（使用更亮的颜色）
        g2d.setColor(ColorUtil.brighten(fillColor, 0.2f));
        g2d.fill(shape);
        
        // 恢复变换
        g2d.setTransform(oldTransform);
    }

    /**
     * 绘制装饰性圆圈背景
     *
     * @param g2d Graphics2D对象
     * @param width 图像宽度
     * @param height 图像高度
     * @param count 圆圈数量
     */
    public static void drawDecorativeCircles(Graphics2D g2d, int width, int height, int count) {
        for (int i = 0; i < count; i++) {
            // 随机位置和大小
            int x = RandomUtil.randomInt(width);
            int y = RandomUtil.randomInt(height);
            int size = RandomUtil.randomInt(10, 30);
            
            // 随机颜色（半透明）
            Color color = ColorUtil.randomVibrantColor();
            g2d.setColor(ColorUtil.withAlpha(color, 25));
            
            // 绘制实心圆
            g2d.fillOval(x - size / 2, y - size / 2, size, size);
            
            // 绘制圆环
            g2d.setColor(ColorUtil.withAlpha(color, 50));
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawOval(x - size / 2, y - size / 2, size, size);
        }
    }

    /**
     * 绘制现代化干扰线（横穿字体、更柔和、更美观）
     *
     * @param g2d Graphics2D对象
     * @param width 图像宽度
     * @param height 图像高度
     * @param count 干扰线数量
     */
    public static void drawModernInterferenceLines(Graphics2D g2d, int width, int height, int count) {
        for (int i = 0; i < count; i++) {
            // 随机决定是横向还是纵向线条
            boolean isHorizontal = RandomUtil.randomInt(100) < 60; // 60%概率横向
            
            int x1, y1, x2, y2, ctrlX, ctrlY;
            
            if (isHorizontal) {
                // 横向线条（横穿字体区域）
                x1 = 0;
                x2 = width;
                y1 = RandomUtil.randomInt(height / 4, height * 3 / 4); // 在中间区域
                y2 = y1 + RandomUtil.randomInt(-10, 10);
                ctrlX = width / 2 + RandomUtil.randomInt(-30, 30);
                ctrlY = (y1 + y2) / 2 + RandomUtil.randomInt(-15, 15);
            } else {
                // 随机线条
                x1 = RandomUtil.randomInt(width);
                y1 = RandomUtil.randomInt(height);
                x2 = RandomUtil.randomInt(width);
                y2 = RandomUtil.randomInt(height);
                ctrlX = RandomUtil.randomInt(width);
                ctrlY = RandomUtil.randomInt(height);
            }
            
            // 半透明的鲜艳颜色
            Color color = ColorUtil.randomVibrantColor();
            g2d.setColor(ColorUtil.withAlpha(color, 35));
            g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            // 绘制二次贝塞尔曲线
            java.awt.geom.QuadCurve2D curve = new java.awt.geom.QuadCurve2D.Float(
                x1, y1, ctrlX, ctrlY, x2, y2
            );
            g2d.draw(curve);
        }
    }
    
    /**
     * 绘制镂空文字（只有轮廓，中间镂空）
     *
     * @param g2d Graphics2D对象
     * @param text 文字内容
     * @param x X坐标
     * @param y Y坐标
     * @param angle 旋转角度(弧度)
     * @param color 轮廓颜色
     */
    public static void drawHollowText(Graphics2D g2d, String text, int x, int y, double angle, Color color) {
        // 保存原始变换
        java.awt.geom.AffineTransform oldTransform = g2d.getTransform();
        
        // 旋转到指定角度
        g2d.rotate(angle, x, y);
        
        // 获取字体轮廓
        Font font = g2d.getFont();
        java.awt.font.FontRenderContext frc = g2d.getFontRenderContext();
        java.awt.font.GlyphVector gv = font.createGlyphVector(frc, text);
        java.awt.Shape shape = gv.getOutline(x, y);
        
        // 1. 绘制轻微的外部描边（增强可见性）
        g2d.setColor(ColorUtil.darken(color, 0.1f));
        g2d.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(shape);
        
        // 2. 绘制主要的镂空轮廓（稍细）
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(shape);
        
        // 3. 绘制内部高光（增加镂空感）
        g2d.setColor(ColorUtil.brighten(color, 0.3f));
        g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(shape);
        
        // 恢复变换
        g2d.setTransform(oldTransform);
    }

    /**
     * 绘制简化版风格化文字（用于中文，描边更细）
     *
     * @param g2d Graphics2D对象
     * @param text 文字内容
     * @param x X坐标
     * @param y Y坐标
     * @param angle 旋转角度(弧度)
     * @param fillColor 填充颜色
     * @param outlineColor 描边颜色
     */
    public static void drawSimpleStyledText(Graphics2D g2d, String text, int x, int y, double angle,
                                           Color fillColor, Color outlineColor) {
        // 保存原始变换
        java.awt.geom.AffineTransform oldTransform = g2d.getTransform();
        
        // 旋转到指定角度
        g2d.rotate(angle, x, y);
        
        // 获取字体轮廓
        Font font = g2d.getFont();
        java.awt.font.FontRenderContext frc = g2d.getFontRenderContext();
        java.awt.font.GlyphVector gv = font.createGlyphVector(frc, text);
        java.awt.Shape shape = gv.getOutline(x, y);
        
        // 1. 绘制轻微阴影（偏移2像素）
        g2d.translate(2, 2);
        g2d.setColor(ColorUtil.withAlpha(Color.BLACK, 20));
        g2d.fill(shape);
        g2d.translate(-2, -2);
        
        // 2. 绘制描边（更细的描边）
        g2d.setColor(outlineColor);
        g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(shape);
        
        // 3. 绘制填充
        g2d.setColor(ColorUtil.brighten(fillColor, 0.2f));
        g2d.fill(shape);
        
        // 恢复变换
        g2d.setTransform(oldTransform);
    }

    /**
     * 从文件路径加载图片
     *
     * @param imagePath 图片文件路径
     * @return BufferedImage对象,加载失败返回null
     */
    public static BufferedImage loadImageFromFile(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return null;
        }
        try {
            File imageFile = new File(imagePath);
            if (!imageFile.exists() || !imageFile.isFile()) {
                System.err.println("图片文件不存在: " + imagePath);
                return null;
            }
            return ImageIO.read(imageFile);
        } catch (IOException e) {
            System.err.println("加载图片失败: " + imagePath + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * 从classpath资源加载图片
     *
     * @param resourcePath 资源路径
     * @return BufferedImage对象,加载失败返回null
     */
    public static BufferedImage loadImageFromResource(String resourcePath) {
        if (resourcePath == null || resourcePath.trim().isEmpty()) {
            return null;
        }
        try {
            InputStream inputStream = ImageUtil.class.getClassLoader().getResourceAsStream(resourcePath);
            if (inputStream == null) {
                System.err.println("资源图片不存在: " + resourcePath);
                return null;
            }
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            System.err.println("加载资源图片失败: " + resourcePath + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * 缩放图片到指定尺寸
     *
     * @param source 原始图片
     * @param targetWidth 目标宽度
     * @param targetHeight 目标高度
     * @return 缩放后的图片
     */
    public static BufferedImage scaleImage(BufferedImage source, int targetWidth, int targetHeight) {
        if (source == null) {
            return null;
        }
        
        // 创建目标图片
        BufferedImage scaledImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = scaledImage.createGraphics();
        
        // 设置高质量渲染
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 绘制缩放后的图片
        g2d.drawImage(source, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        
        return scaledImage;
    }

    /**
     * 按比例缩放图片（保持宽高比）
     *
     * @param source 原始图片
     * @param maxWidth 最大宽度
     * @param maxHeight 最大高度
     * @return 缩放后的图片
     */
    public static BufferedImage scaleImageKeepRatio(BufferedImage source, int maxWidth, int maxHeight) {
        if (source == null) {
            return null;
        }
        
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        
        // 计算缩放比例
        double widthRatio = (double) maxWidth / sourceWidth;
        double heightRatio = (double) maxHeight / sourceHeight;
        double ratio = Math.min(widthRatio, heightRatio);
        
        // 计算目标尺寸
        int targetWidth = (int) (sourceWidth * ratio);
        int targetHeight = (int) (sourceHeight * ratio);
        
        return scaleImage(source, targetWidth, targetHeight);
    }
}
