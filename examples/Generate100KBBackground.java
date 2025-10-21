import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

/**
 * 生成100KB左右的高质量背景图片
 * 演示：更高分辨率 + 更复杂效果 = 更大文件
 */
public class Generate100KBBackground {

    public static void main(String[] args) throws IOException {
        String outputDir = "examples/100kb-test/";
        new File(outputDir).mkdirs();

        System.out.println("=== 生成100KB级别的高质量背景 ===\n");

        // 生成3张不同风格的高质量背景
        generateHighQualityBackground(outputDir, "hq_bg1_ocean.png", "海洋风格");
        generateHighQualityBackground(outputDir, "hq_bg2_sunset.png", "日落风格");
        generateHighQualityBackground(outputDir, "hq_bg3_forest.png", "森林风格");

        System.out.println("\n=== 文件大小统计 ===");
        File dir = new File(outputDir);
        long totalSize = 0;
        for (File file : dir.listFiles()) {
            if (file.getName().endsWith(".png")) {
                long size = file.length();
                totalSize += size;
                System.out.printf("  %s: %.1f KB\n", file.getName(), size / 1024.0);
            }
        }
        System.out.printf("\n总大小: %.1f KB\n", totalSize / 1024.0);
        System.out.println("\n✅ 生成完成！");
        System.out.println("📁 输出目录: " + dir.getAbsolutePath());
    }

    private static void generateHighQualityBackground(String outputDir, String filename, String style) throws IOException {
        // 更大的尺寸 = 更大的文件
        int width = 800;   // 比之前的350宽很多
        int height = 500;  // 比之前的200高很多

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 高质量渲染设置
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

        Random random = new Random();

        // 根据风格选择颜色
        Color[] colors = getColorsForStyle(style);

        // 复杂的多层渐变背景
        for (int layer = 0; layer < 3; layer++) {
            GradientPaint gradient = new GradientPaint(
                random.nextInt(width), random.nextInt(height), colors[layer],
                random.nextInt(width), random.nextInt(height), colors[(layer + 1) % colors.length]
            );
            g2d.setPaint(gradient);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
            g2d.fillRect(0, 0, width, height);
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        // 添加大量装饰元素（增加文件大小）
        drawComplexDecorations(g2d, width, height, colors);

        g2d.dispose();

        // 保存为PNG（高质量）
        ImageIO.write(image, "PNG", new File(outputDir + filename));
        System.out.println("✓ 已生成: " + filename + " (" + style + ")");
    }

    private static Color[] getColorsForStyle(String style) {
        switch (style) {
            case "海洋风格":
                return new Color[]{
                    new Color(0, 105, 148),    // 深海蓝
                    new Color(0, 150, 199),    // 海洋蓝
                    new Color(72, 202, 228),   // 天空蓝
                    new Color(144, 224, 239)   // 浅蓝
                };
            case "日落风格":
                return new Color[]{
                    new Color(255, 94, 77),    // 橙红
                    new Color(255, 154, 0),    // 橙色
                    new Color(255, 193, 7),    // 金黄
                    new Color(255, 138, 101)   // 珊瑚色
                };
            case "森林风格":
                return new Color[]{
                    new Color(27, 94, 32),     // 深绿
                    new Color(56, 142, 60),    // 森林绿
                    new Color(102, 187, 106),  // 草绿
                    new Color(165, 214, 167)   // 浅绿
                };
            default:
                return new Color[]{Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW};
        }
    }

    private static void drawComplexDecorations(Graphics2D g2d, int width, int height, Color[] colors) {
        Random random = new Random();

        // 1. 绘制50个半透明圆形
        for (int i = 0; i < 50; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int size = 30 + random.nextInt(100);
            Color color = colors[random.nextInt(colors.length)];
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
            g2d.fillOval(x - size / 2, y - size / 2, size, size);
        }

        // 2. 绘制30条贝塞尔曲线
        for (int i = 0; i < 30; i++) {
            Path2D path = new Path2D.Float();
            path.moveTo(random.nextInt(width), random.nextInt(height));
            path.curveTo(
                random.nextInt(width), random.nextInt(height),
                random.nextInt(width), random.nextInt(height),
                random.nextInt(width), random.nextInt(height)
            );
            Color color = colors[random.nextInt(colors.length)];
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
            g2d.setStroke(new BasicStroke(2 + random.nextInt(3)));
            g2d.draw(path);
        }

        // 3. 绘制20个渐变矩形
        for (int i = 0; i < 20; i++) {
            int x = random.nextInt(width - 100);
            int y = random.nextInt(height - 100);
            int w = 50 + random.nextInt(100);
            int h = 50 + random.nextInt(100);
            Color c1 = colors[random.nextInt(colors.length)];
            Color c2 = colors[random.nextInt(colors.length)];
            GradientPaint gp = new GradientPaint(x, y, 
                new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), 50),
                x + w, y + h,
                new Color(c2.getRed(), c2.getGreen(), c2.getBlue(), 50));
            g2d.setPaint(gp);
            g2d.fillRect(x, y, w, h);
        }
    }
}

