import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 生成内置背景图片
 * 运行此程序生成几张漂亮的背景图，然后复制到 src/main/resources/slider-backgrounds/
 */
public class GenerateBuiltinBackgrounds {

    public static void main(String[] args) throws IOException {
        String outputDir = "src/main/resources/slider-backgrounds";
        new File(outputDir).mkdirs();

        System.out.println("=== 生成内置背景图片 ===\n");

        // 生成5张不同风格的背景图
        generateBackground1(outputDir);
        generateBackground2(outputDir);
        generateBackground3(outputDir);
        generateBackground4(outputDir);
        generateBackground5(outputDir);

        System.out.println("\n✅ 所有背景图生成完成！");
        System.out.println("📁 位置: " + new File(outputDir).getAbsolutePath());
    }

    /**
     * 风格1：蓝色渐变 + 几何图形
     */
    private static void generateBackground1(String dir) throws IOException {
        BufferedImage img = new BufferedImage(350, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 蓝色渐变背景
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(135, 206, 250),
            350, 200, new Color(70, 130, 180)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 350, 200);

        // 添加半透明图形
        drawRandomCircles(g2d, new Color(255, 255, 255, 60), 8);
        drawRandomRects(g2d, new Color(0, 0, 139, 40), 5);

        g2d.dispose();
        ImageIO.write(img, "PNG", new File(dir + "/bg1_blue_gradient.png"));
        System.out.println("✓ 生成: bg1_blue_gradient.png");
    }

    /**
     * 风格2：绿色自然 + 波浪
     */
    private static void generateBackground2(String dir) throws IOException {
        BufferedImage img = new BufferedImage(350, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绿色渐变背景
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(144, 238, 144),
            350, 200, new Color(34, 139, 34)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 350, 200);

        // 添加波浪线
        g2d.setColor(new Color(255, 255, 255, 80));
        g2d.setStroke(new BasicStroke(3));
        for (int i = 0; i < 5; i++) {
            drawWaveLine(g2d, i * 40);
        }

        drawRandomCircles(g2d, new Color(255, 255, 255, 50), 6);

        g2d.dispose();
        ImageIO.write(img, "PNG", new File(dir + "/bg2_green_nature.png"));
        System.out.println("✓ 生成: bg2_green_nature.png");
    }

    /**
     * 风格3：紫色梦幻 + 星星
     */
    private static void generateBackground3(String dir) throws IOException {
        BufferedImage img = new BufferedImage(350, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 紫色渐变背景
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(138, 43, 226),
            350, 200, new Color(75, 0, 130)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 350, 200);

        // 添加星星
        g2d.setColor(new Color(255, 255, 255, 150));
        for (int i = 0; i < 30; i++) {
            int x = (int) (Math.random() * 350);
            int y = (int) (Math.random() * 200);
            int size = (int) (Math.random() * 3) + 1;
            g2d.fillOval(x, y, size, size);
        }

        drawRandomCircles(g2d, new Color(255, 192, 203, 60), 5);

        g2d.dispose();
        ImageIO.write(img, "PNG", new File(dir + "/bg3_purple_dream.png"));
        System.out.println("✓ 生成: bg3_purple_dream.png");
    }

    /**
     * 风格4：橙色温暖 + 几何拼接
     */
    private static void generateBackground4(String dir) throws IOException {
        BufferedImage img = new BufferedImage(350, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 橙色渐变背景
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(255, 218, 185),
            350, 200, new Color(255, 140, 0)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 350, 200);

        // 几何拼接效果
        drawRandomPolygons(g2d, 8);

        g2d.dispose();
        ImageIO.write(img, "PNG", new File(dir + "/bg4_orange_warm.png"));
        System.out.println("✓ 生成: bg4_orange_warm.png");
    }

    /**
     * 风格5：粉色浪漫 + 气泡
     */
    private static void generateBackground5(String dir) throws IOException {
        BufferedImage img = new BufferedImage(350, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 粉色渐变背景
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(255, 182, 193),
            350, 200, new Color(255, 105, 180)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 350, 200);

        // 气泡效果
        for (int i = 0; i < 15; i++) {
            int x = (int) (Math.random() * 350);
            int y = (int) (Math.random() * 200);
            int size = (int) (Math.random() * 40) + 20;
            g2d.setColor(new Color(255, 255, 255, (int) (Math.random() * 60) + 40));
            g2d.fillOval(x, y, size, size);
            g2d.setColor(new Color(255, 255, 255, 100));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(x, y, size, size);
        }

        g2d.dispose();
        ImageIO.write(img, "PNG", new File(dir + "/bg5_pink_romantic.png"));
        System.out.println("✓ 生成: bg5_pink_romantic.png");
    }

    // 辅助方法
    private static void drawRandomCircles(Graphics2D g2d, Color color, int count) {
        g2d.setColor(color);
        for (int i = 0; i < count; i++) {
            int x = (int) (Math.random() * 350);
            int y = (int) (Math.random() * 200);
            int size = (int) (Math.random() * 50) + 30;
            g2d.fillOval(x, y, size, size);
        }
    }

    private static void drawRandomRects(Graphics2D g2d, Color color, int count) {
        g2d.setColor(color);
        for (int i = 0; i < count; i++) {
            int x = (int) (Math.random() * 350);
            int y = (int) (Math.random() * 200);
            int w = (int) (Math.random() * 60) + 40;
            int h = (int) (Math.random() * 60) + 40;
            g2d.fillRoundRect(x, y, w, h, 10, 10);
        }
    }

    private static void drawWaveLine(Graphics2D g2d, int offsetY) {
        for (int x = 0; x < 350; x += 10) {
            int y = (int) (Math.sin(x * 0.05) * 20) + 100 + offsetY;
            if (x > 0) {
                int prevY = (int) (Math.sin((x - 10) * 0.05) * 20) + 100 + offsetY;
                g2d.drawLine(x - 10, prevY, x, y);
            }
        }
    }

    private static void drawRandomPolygons(Graphics2D g2d, int count) {
        for (int i = 0; i < count; i++) {
            int x = (int) (Math.random() * 350);
            int y = (int) (Math.random() * 200);
            int size = (int) (Math.random() * 50) + 30;

            int[] xPoints = new int[3];
            int[] yPoints = new int[3];
            for (int j = 0; j < 3; j++) {
                xPoints[j] = x + (int) (Math.random() * size) - size / 2;
                yPoints[j] = y + (int) (Math.random() * size) - size / 2;
            }

            g2d.setColor(new Color(
                (int) (Math.random() * 255),
                (int) (Math.random() * 255),
                (int) (Math.random() * 255),
                60
            ));
            g2d.fillPolygon(xPoints, yPoints, 3);
        }
    }
}

