package io.github.purecaptcha.util;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * 拼图工具类
 * <p>
 * 用于生成拼图块形状和切割图像
 *
 * @author PureCaptcha
 * @version 1.0.0
 */
public class PuzzleUtil {

    // 拼图块默认尺寸
    private static final int DEFAULT_PUZZLE_WIDTH = 60;
    private static final int DEFAULT_PUZZLE_HEIGHT = 60;

    // 凸起/凹陷圆的半径
    private static final int CIRCLE_RADIUS = 10;

    /**
     * 创建拼图块形状
     * <p>
     * 形状为带有凸起/凹陷的矩形
     *
     * @param x      起始 X 坐标
     * @param y      起始 Y 坐标
     * @param width  拼图块宽度
     * @param height 拼图块高度
     * @return 拼图块形状
     */
    public static Shape createPuzzleShape(int x, int y, int width, int height) {
        // 基础矩形
        Area puzzleShape = new Area(new RoundRectangle2D.Double(x, y, width, height, 5, 5));

        // 右侧凸起
        int rightCircleX = x + width - CIRCLE_RADIUS;
        int rightCircleY = y + height / 2 - CIRCLE_RADIUS;
        Ellipse2D rightCircle = new Ellipse2D.Double(rightCircleX, rightCircleY, CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2);
        puzzleShape.add(new Area(rightCircle));

        // 底部凹陷
        int bottomCircleX = x + width / 2 - CIRCLE_RADIUS;
        int bottomCircleY = y + height - CIRCLE_RADIUS;
        Ellipse2D bottomCircle = new Ellipse2D.Double(bottomCircleX, bottomCircleY, CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2);
        puzzleShape.subtract(new Area(bottomCircle));

        return puzzleShape;
    }

    /**
     * 从原图中切出拼图块
     *
     * @param originalImage 原始图像
     * @param x             拼图块 X 坐标
     * @param y             拼图块 Y 坐标
     * @param width         拼图块宽度
     * @param height        拼图块高度
     * @return 拼图块图像
     */
    public static BufferedImage cutPuzzlePiece(BufferedImage originalImage, int x, int y, int width, int height) {
        // 创建拼图块图像(只需要基本宽高,不需要额外空间)
        BufferedImage puzzlePiece = new BufferedImage(width + CIRCLE_RADIUS, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = puzzlePiece.createGraphics();

        // 抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 创建拼图形状(从 0,0 开始)
        Shape puzzleShape = createPuzzleShape(0, 0, width, height);

        // 设置裁剪区域
        g2d.setClip(puzzleShape);

        // 绘制原图的对应区域
        g2d.drawImage(originalImage, -x, -y, null);

        // 绘制拼图块边框
        g2d.setClip(null);
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(puzzleShape);

        g2d.dispose();
        return puzzlePiece;
    }

    /**
     * 在背景图上绘制拼图切口阴影
     *
     * @param backgroundImage 背景图像
     * @param x               切口 X 坐标
     * @param y               切口 Y 坐标
     * @param width           切口宽度
     * @param height          切口高度
     */
    public static void drawPuzzleCutout(BufferedImage backgroundImage, int x, int y, int width, int height) {
        Graphics2D g2d = backgroundImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 创建拼图形状
        Shape puzzleShape = createPuzzleShape(x, y, width, height);

        // 填充半透明黑色阴影
        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.fill(puzzleShape);

        // 绘制白色边框
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(puzzleShape);

        g2d.dispose();
    }

    /**
     * 生成随机拼图块位置
     * <p>
     * 确保拼图块不会太靠边
     *
     * @param imageWidth  图像宽度
     * @param puzzleWidth 拼图块宽度
     * @return 拼图块 X 坐标
     */
    public static int generateRandomPuzzleX(int imageWidth, int puzzleWidth) {
        // 留出左右边距,右侧需要考虑凸起圆的额外宽度
        int minX = imageWidth / 3;
        int maxX = imageWidth - puzzleWidth - CIRCLE_RADIUS - 20;
        return RandomUtil.randomInt(minX, maxX);
    }

    /**
     * 生成随机拼图块 Y 坐标
     *
     * @param imageHeight  图像高度
     * @param puzzleHeight 拼图块高度
     * @return 拼图块 Y 坐标
     */
    public static int generateRandomPuzzleY(int imageHeight, int puzzleHeight) {
        // 留出上下边距
        int minY = 20;
        int maxY = imageHeight - puzzleHeight - 20;
        return RandomUtil.randomInt(minY, maxY);
    }

    /**
     * 获取默认拼图块宽度
     */
    public static int getDefaultPuzzleWidth() {
        return DEFAULT_PUZZLE_WIDTH;
    }

    /**
     * 获取默认拼图块高度
     */
    public static int getDefaultPuzzleHeight() {
        return DEFAULT_PUZZLE_HEIGHT;
    }
}
