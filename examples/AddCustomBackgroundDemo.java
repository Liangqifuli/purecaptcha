import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 演示：如何手动添加自定义背景图片
 * 
 * 这个程序会生成几张示例图片，你可以：
 * 1. 运行这个程序生成示例图片
 * 2. 或者直接用你自己的照片替换
 */
public class AddCustomBackgroundDemo {

    public static void main(String[] args) throws IOException {
        String targetDir = "src/main/resources/slider-backgrounds";
        File dir = new File(targetDir);
        
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("✓ 创建目录: " + dir.getAbsolutePath());
        }

        System.out.println("=== 添加自定义背景图片演示 ===\n");
        System.out.println("目标目录: " + dir.getAbsolutePath() + "\n");

        // 生成3张示例图片供参考
        generateSampleImage1(targetDir);
        generateSampleImage2(targetDir);
        generateSampleImage3(targetDir);

        System.out.println("\n=== 使用说明 ===");
        System.out.println("1. ✅ 图片已生成在: " + dir.getAbsolutePath());
        System.out.println("2. 📝 可以替换为你自己的图片（任意尺寸、任意名字）");
        System.out.println("3. 🔧 修改 BuiltinSliderBackground.java 添加枚举");
        System.out.println("4. 📦 运行 mvn clean install 重新打包");
        System.out.println("5. 🚀 在项目中使用新的背景！");

        System.out.println("\n=== 快速示例 ===");
        System.out.println("// 在 BuiltinSliderBackground.java 中添加：");
        System.out.println("MY_CUSTOM_1(\"slider-backgrounds/custom_sample_1.png\"),");
        System.out.println("MY_CUSTOM_2(\"slider-backgrounds/custom_sample_2.png\"),");
        System.out.println("MY_CUSTOM_3(\"slider-backgrounds/custom_sample_3.png\");");
        System.out.println("\n// 然后使用：");
        System.out.println("CaptchaConfig config = CaptchaConfig.builder()");
        System.out.println("    .builtinBackground(BuiltinSliderBackground.MY_CUSTOM_1)");
        System.out.println("    .build();");
    }

    /**
     * 生成示例图片1：红色渐变 + 圆圈
     */
    private static void generateSampleImage1(String dir) throws IOException {
        BufferedImage img = new BufferedImage(350, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 红色渐变背景
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(255, 99, 71),
            350, 200, new Color(220, 20, 60)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 350, 200);

        // 添加装饰圆圈
        g2d.setColor(new Color(255, 255, 255, 60));
        for (int i = 0; i < 8; i++) {
            int x = (int) (Math.random() * 350);
            int y = (int) (Math.random() * 200);
            int size = (int) (Math.random() * 50) + 30;
            g2d.fillOval(x, y, size, size);
        }

        g2d.dispose();
        File output = new File(dir + "/custom_sample_1.png");
        ImageIO.write(img, "PNG", output);
        System.out.println("✓ 生成示例1: " + output.getName() + " (红色渐变)");
    }

    /**
     * 生成示例图片2：青色渐变 + 方块
     */
    private static void generateSampleImage2(String dir) throws IOException {
        BufferedImage img = new BufferedImage(350, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 青色渐变背景
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(64, 224, 208),
            350, 200, new Color(0, 128, 128)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 350, 200);

        // 添加装饰方块
        g2d.setColor(new Color(255, 255, 255, 50));
        for (int i = 0; i < 10; i++) {
            int x = (int) (Math.random() * 350);
            int y = (int) (Math.random() * 200);
            int size = (int) (Math.random() * 40) + 20;
            g2d.fillRoundRect(x, y, size, size, 8, 8);
        }

        g2d.dispose();
        File output = new File(dir + "/custom_sample_2.png");
        ImageIO.write(img, "PNG", output);
        System.out.println("✓ 生成示例2: " + output.getName() + " (青色渐变)");
    }

    /**
     * 生成示例图片3：黄色渐变 + 星形
     */
    private static void generateSampleImage3(String dir) throws IOException {
        BufferedImage img = new BufferedImage(350, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 黄色渐变背景
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(255, 215, 0),
            350, 200, new Color(255, 165, 0)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, 350, 200);

        // 添加装饰三角形
        g2d.setColor(new Color(255, 255, 255, 70));
        for (int i = 0; i < 12; i++) {
            int x = (int) (Math.random() * 350);
            int y = (int) (Math.random() * 200);
            int size = (int) (Math.random() * 30) + 20;
            
            int[] xPoints = {x, x + size/2, x + size};
            int[] yPoints = {y + size, y, y + size};
            g2d.fillPolygon(xPoints, yPoints, 3);
        }

        g2d.dispose();
        File output = new File(dir + "/custom_sample_3.png");
        ImageIO.write(img, "PNG", output);
        System.out.println("✓ 生成示例3: " + output.getName() + " (黄色渐变)");
    }
}

