package io.github.purecaptcha.model;

import io.github.purecaptcha.core.Captcha;
import io.github.purecaptcha.core.CaptchaType;

import java.awt.image.BufferedImage;

/**
 * 滑动拼图验证码结果
 * <p>
 * 包含背景图、拼图块图、切口位置等信息
 *
 * @author PureCaptcha
 * @version 1.0.0
 */
public class SliderCaptchaResult implements Captcha {

    private final BufferedImage backgroundImage;  // 带切口的背景图
    private final BufferedImage sliderImage;      // 拼图块图像
    private final BufferedImage originalImage;    // 【新增】原始完整图像（用于智能验证）
    private final int sliderX;                    // 拼图块正确的 X 坐标
    private final int sliderY;                    // 拼图块的 Y 坐标
    private final int puzzleWidth;                // 【新增】拼图块宽度
    private final int puzzleHeight;               // 【新增】拼图块高度
    private final int width;                      // 图像宽度
    private final int height;                     // 图像高度
    private final int tolerance;                  // 验证容差(像素)
    
    // 图像相似度阈值（0-100），超过此值认为匹配成功
    private static final double SIMILARITY_THRESHOLD = 88.0; // 88%，平衡准确性和成功率

    /**
     * 构造滑动拼图验证码结果（旧版本，向后兼容）
     *
     * @param backgroundImage 带切口的背景图
     * @param sliderImage     拼图块图像
     * @param sliderX         拼图块正确的 X 坐标
     * @param sliderY         拼图块的 Y 坐标
     * @param width           图像宽度
     * @param height          图像高度
     * @param tolerance       验证容差(像素)
     */
    @Deprecated
    public SliderCaptchaResult(BufferedImage backgroundImage, BufferedImage sliderImage,
                               int sliderX, int sliderY, int width, int height, int tolerance) {
        this(backgroundImage, sliderImage, null, sliderX, sliderY, 60, 60, width, height, tolerance);
    }

    /**
     * 构造滑动拼图验证码结果（新版本，支持智能验证）
     *
     * @param backgroundImage 带切口的背景图
     * @param sliderImage     拼图块图像
     * @param originalImage   原始完整图像
     * @param sliderX         拼图块正确的 X 坐标
     * @param sliderY         拼图块的 Y 坐标
     * @param puzzleWidth     拼图块宽度
     * @param puzzleHeight    拼图块高度
     * @param width           图像宽度
     * @param height          图像高度
     * @param tolerance       验证容差(像素)
     */
    public SliderCaptchaResult(BufferedImage backgroundImage, BufferedImage sliderImage,
                               BufferedImage originalImage, int sliderX, int sliderY,
                               int puzzleWidth, int puzzleHeight, int width, int height, int tolerance) {
        this.backgroundImage = backgroundImage;
        this.sliderImage = sliderImage;
        this.originalImage = originalImage;
        this.sliderX = sliderX;
        this.sliderY = sliderY;
        this.puzzleWidth = puzzleWidth;
        this.puzzleHeight = puzzleHeight;
        this.width = width;
        this.height = height;
        this.tolerance = tolerance;
    }

    @Override
    public CaptchaType getType() {
        return CaptchaType.SLIDER;
    }

    @Override
    public BufferedImage getImage() {
        return backgroundImage;
    }

    @Override
    public byte[] getImageData() {
        return null; // 滑动验证码使用两张图片,不使用单一字节数组
    }

    @Override
    public String getAnswer() {
        return String.valueOf(sliderX);
    }

    @Override
    public boolean verify(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return false;
        }
        try {
            int userX = Integer.parseInt(userInput.trim());
            // 判断用户滑动的 X 坐标是否在容差范围内
            return Math.abs(userX - sliderX) <= tolerance;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 验证滑动距离（推荐使用，结合坐标和图像相似度）
     * <p>
     * 【新算法】：
     * 1. 优先使用图像相似度匹配（如果有原始图像）
     * 2. 回退到坐标匹配（兼容旧版本）
     *
     * @param userX 用户滑动到的 X 坐标
     * @return 是否验证通过
     */
    public boolean verifyPosition(int userX) {
        // 策略1：如果有原始图像，使用智能验证（推荐）
        if (originalImage != null) {
            return verifyPositionSmart(userX);
        }
        
        // 策略2：回退到坐标验证（向后兼容）
        return Math.abs(userX - sliderX) <= tolerance;
    }
    
    /**
     * 【新增】智能验证 - 基于图像相似度匹配
     * <p>
     * 原理：提取用户位置的图像区域，与原始图像对应区域进行像素级对比，
     * 计算相似度，超过阈值则认为匹配成功。
     * <p>
     * 优势：
     * - 不依赖精确坐标，更贴近视觉判断
     * - 自动适应拼图块的实际尺寸
     * - 抗干扰能力强
     *
     * @param userX 用户滑动到的 X 坐标
     * @return 是否验证通过
     */
    public boolean verifyPositionSmart(int userX) {
        if (originalImage == null) {
            // 没有原始图像，回退到坐标验证
            return Math.abs(userX - sliderX) <= tolerance;
        }
        
        // 在用户位置附近搜索最佳匹配点（容差范围内）
        int searchStart = Math.max(0, userX - tolerance);
        int searchEnd = Math.min(width - puzzleWidth, userX + tolerance);
        
        double maxSimilarity = 0.0;
        int bestX = userX;
        
        // 在容差范围内搜索最佳匹配位置
        for (int testX = searchStart; testX <= searchEnd; testX++) {
            double similarity = calculateImageSimilarity(testX, sliderY);
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                bestX = testX;
            }
        }
        
        // 判断：最佳匹配位置的相似度超过阈值，且在容差范围内
        boolean similarityPass = maxSimilarity >= SIMILARITY_THRESHOLD;
        boolean positionPass = Math.abs(bestX - sliderX) <= tolerance; // 严格坐标限制
        
        return similarityPass && positionPass;
    }
    
    /**
     * 【新增】计算指定位置的图像相似度
     * <p>
     * 提取原始图像中指定位置的区域，与拼图块进行像素级对比
     *
     * @param x 测试位置的 X 坐标
     * @param y 测试位置的 Y 坐标
     * @return 相似度（0-100）
     */
    private double calculateImageSimilarity(int x, int y) {
        if (originalImage == null || sliderImage == null) {
            return 0.0;
        }
        
        // 边界检查
        if (x < 0 || y < 0 || x + puzzleWidth > originalImage.getWidth() 
            || y + puzzleHeight > originalImage.getHeight()) {
            return 0.0;
        }
        
        int totalPixels = 0;
        int matchedPixels = 0;
        long totalColorDiff = 0;
        
        // 逐像素对比
        for (int dy = 0; dy < puzzleHeight; dy++) {
            for (int dx = 0; dx < puzzleWidth; dx++) {
                // 获取原始图像中对应位置的像素
                int originalRGB = originalImage.getRGB(x + dx, y + dy);
                
                // 获取拼图块的像素（需要处理透明区域）
                int sliderRGB = sliderImage.getRGB(dx, dy);
                int sliderAlpha = (sliderRGB >> 24) & 0xFF;
                
                // 跳过拼图块的透明区域（只比较有效像素）
                if (sliderAlpha < 50) {
                    continue;
                }
                
                totalPixels++;
                
                // 提取RGB分量
                int r1 = (originalRGB >> 16) & 0xFF;
                int g1 = (originalRGB >> 8) & 0xFF;
                int b1 = originalRGB & 0xFF;
                
                int r2 = (sliderRGB >> 16) & 0xFF;
                int g2 = (sliderRGB >> 8) & 0xFF;
                int b2 = sliderRGB & 0xFF;
                
                // 计算颜色差异
                int colorDiff = Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
                totalColorDiff += colorDiff;
                
                // 如果差异很小（< 30），认为匹配
                if (colorDiff < 30) {
                    matchedPixels++;
                }
            }
        }
        
        if (totalPixels == 0) {
            return 0.0;
        }
        
        // 计算相似度：匹配像素比例 + 颜色相似度
        double pixelMatchRate = (double) matchedPixels / totalPixels * 100.0;
        double avgColorDiff = (double) totalColorDiff / totalPixels;
        double colorSimilarity = Math.max(0, 100.0 - avgColorDiff / 7.65); // 归一化到0-100
        
        // 综合评分：70% 像素匹配率 + 30% 颜色相似度
        return pixelMatchRate * 0.7 + colorSimilarity * 0.3;
    }

    /**
     * 验证滑动距离（宽松模式）
     * 容差自动放大1.5倍，适用于移动端或触摸操作
     *
     * @param userX 用户滑动到的 X 坐标
     * @return 是否验证通过
     */
    public boolean verifyPositionLoose(int userX) {
        int looseTolerance = (int) (tolerance * 1.5);
        return Math.abs(userX - sliderX) <= looseTolerance;
    }

    /**
     * 获取验证详情（用于调试）- 增强版
     * <p>
     * 显示坐标验证和智能验证的详细信息
     *
     * @param userX 用户滑动到的 X 坐标
     * @return 验证详情字符串
     */
    public String getVerifyDetails(int userX) {
        int diff = Math.abs(userX - sliderX);
        boolean coordPass = diff <= tolerance;
        
        StringBuilder details = new StringBuilder();
        details.append("═══════════════════════════════════════\n");
        details.append("       滑动验证码详细验证报告\n");
        details.append("═══════════════════════════════════════\n\n");
        
        // 基本信息
        details.append("【坐标信息】\n");
        details.append(String.format("  正确位置: %d px\n", sliderX));
        details.append(String.format("  用户位置: %d px\n", userX));
        details.append(String.format("  坐标差距: %d px\n", diff));
        details.append(String.format("  设定容差: %d px\n", tolerance));
        details.append(String.format("  坐标验证: %s\n\n", coordPass ? "✓ 通过" : "✗ 失败"));
        
        // 智能验证信息
        if (originalImage != null) {
            details.append("【智能验证】(基于图像相似度)\n");
            
            // 计算用户位置的相似度
            double userSimilarity = calculateImageSimilarity(userX, sliderY);
            
            // 搜索最佳匹配位置
            int searchStart = Math.max(0, userX - tolerance);
            int searchEnd = Math.min(width - puzzleWidth, userX + tolerance);
            double maxSimilarity = 0.0;
            int bestX = userX;
            
            for (int testX = searchStart; testX <= searchEnd; testX++) {
                double sim = calculateImageSimilarity(testX, sliderY);
                if (sim > maxSimilarity) {
                    maxSimilarity = sim;
                    bestX = testX;
                }
            }
            
            details.append(String.format("  用户位置相似度: %.2f%%\n", userSimilarity));
            details.append(String.format("  最佳匹配位置: %d px (相似度 %.2f%%)\n", bestX, maxSimilarity));
            details.append(String.format("  相似度阈值: %.2f%%\n", SIMILARITY_THRESHOLD));
            
            boolean similarityPass = maxSimilarity >= SIMILARITY_THRESHOLD;
            boolean smartPass = verifyPositionSmart(userX);
            
            details.append(String.format("  相似度判定: %s\n", similarityPass ? "✓ 达标" : "✗ 未达标"));
            details.append(String.format("  智能验证结果: %s\n\n", smartPass ? "✓ 通过" : "✗ 失败"));
            
            // 最终结果
            details.append("【最终结果】\n");
            details.append(String.format("  验证方式: 智能验证（图像匹配）\n"));
            details.append(String.format("  验证结果: %s\n", smartPass ? "✅ 通过" : "❌ 失败"));
            
            if (!smartPass && maxSimilarity > 0) {
                details.append("\n【建议】\n");
                if (maxSimilarity < SIMILARITY_THRESHOLD) {
                    details.append(String.format("  相似度不足，需要更精确对齐\n"));
                    details.append(String.format("  建议移动到位置: %d px\n", bestX));
                } else {
                    details.append(String.format("  位置偏差过大\n"));
                    details.append(String.format("  需要移动: %s %d px\n", 
                        bestX > userX ? "右移" : "左移", Math.abs(bestX - userX)));
                }
            }
        } else {
            details.append("【验证方式】传统坐标验证\n");
            details.append(String.format("  最终结果: %s\n", coordPass ? "✅ 通过" : "❌ 失败"));
        }
        
        details.append("\n═══════════════════════════════════════");
        return details.toString();
    }

    /**
     * 检查用户位置距离正确位置有多远
     *
     * @param userX 用户滑动到的 X 坐标
     * @return 距离正确位置的像素差（正数表示需要右移，负数表示需要左移）
     */
    public int getPositionDifference(int userX) {
        return sliderX - userX;
    }

    public BufferedImage getBackgroundImage() {
        return backgroundImage;
    }

    public BufferedImage getSliderImage() {
        return sliderImage;
    }

    public int getSliderX() {
        return sliderX;
    }

    public int getSliderY() {
        return sliderY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTolerance() {
        return tolerance;
    }
}
