package io.github.purecaptcha.util;

import java.awt.Color;

/**
 * 颜色工具类
 * <p>
 * 提供颜色生成和处理功能
 *
 * @author PureCaptcha
 * @version 1.0.0
 */
public class ColorUtil {

    /**
     * 生成随机颜色(排除过浅和过深的颜色)
     *
     * @return 随机颜色
     */
    public static Color randomColor() {
        return randomColor(50, 200);
    }

    /**
     * 生成指定范围内的随机颜色
     *
     * @param min RGB最小值(0-255)
     * @param max RGB最大值(0-255)
     * @return 随机颜色
     */
    public static Color randomColor(int min, int max) {
        int r = RandomUtil.randomInt(min, max);
        int g = RandomUtil.randomInt(min, max);
        int b = RandomUtil.randomInt(min, max);
        return new Color(r, g, b);
    }

    /**
     * 生成深色(用于字体)
     *
     * @return 深色
     */
    public static Color randomDarkColor() {
        return randomColor(0, 120);
    }

    /**
     * 生成浅色(用于背景)
     *
     * @return 浅色
     */
    public static Color randomLightColor() {
        return randomColor(200, 255);
    }

    /**
     * 颜色渐变
     *
     * @param start 起始颜色
     * @param end 结束颜色
     * @param ratio 渐变比例(0.0-1.0)
     * @return 渐变后的颜色
     */
    public static Color gradient(Color start, Color end, float ratio) {
        if (ratio < 0) ratio = 0;
        if (ratio > 1) ratio = 1;

        int r = (int) (start.getRed() + (end.getRed() - start.getRed()) * ratio);
        int g = (int) (start.getGreen() + (end.getGreen() - start.getGreen()) * ratio);
        int b = (int) (start.getBlue() + (end.getBlue() - start.getBlue()) * ratio);

        return new Color(r, g, b);
    }

    /**
     * 鲜艳颜色调色板（用于现代化验证码）
     */
    private static final Color[] VIBRANT_COLORS = {
        new Color(255, 107, 107),  // 珊瑚红
        new Color(255, 159, 67),   // 橙色
        new Color(255, 195, 18),   // 金黄色
        new Color(72, 219, 251),   // 天蓝色
        new Color(29, 209, 161),   // 青绿色
        new Color(94, 156, 255),   // 蓝紫色
        new Color(162, 155, 254),  // 淡紫色
        new Color(255, 89, 163),   // 粉红色
        new Color(255, 121, 63),   // 深橙色
        new Color(88, 177, 159),   // 青色
        new Color(250, 130, 49),   // 亮橙色
        new Color(69, 170, 242),   // 亮蓝色
        new Color(252, 92, 101),   // 西瓜红
        new Color(67, 160, 71),    // 绿色
        new Color(156, 39, 176)    // 紫色
    };

    /**
     * 随机获取一个鲜艳的颜色
     *
     * @return 鲜艳颜色
     */
    public static Color randomVibrantColor() {
        return VIBRANT_COLORS[RandomUtil.randomInt(VIBRANT_COLORS.length)];
    }

    /**
     * 获取颜色的半透明版本
     *
     * @param color 原始颜色
     * @param alpha 透明度(0-255)
     * @return 半透明颜色
     */
    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    /**
     * 使颜色变亮
     *
     * @param color 原始颜色
     * @param factor 变亮系数(0.0-1.0)
     * @return 变亮后的颜色
     */
    public static Color brighten(Color color, float factor) {
        int r = Math.min(255, (int) (color.getRed() + (255 - color.getRed()) * factor));
        int g = Math.min(255, (int) (color.getGreen() + (255 - color.getGreen()) * factor));
        int b = Math.min(255, (int) (color.getBlue() + (255 - color.getBlue()) * factor));
        return new Color(r, g, b);
    }

    /**
     * 使颜色变暗
     *
     * @param color 原始颜色
     * @param factor 变暗系数(0.0-1.0)
     * @return 变暗后的颜色
     */
    public static Color darken(Color color, float factor) {
        int r = (int) (color.getRed() * (1 - factor));
        int g = (int) (color.getGreen() * (1 - factor));
        int b = (int) (color.getBlue() * (1 - factor));
        return new Color(r, g, b);
    }
}
