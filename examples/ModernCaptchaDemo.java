import io.github.purecaptcha.CaptchaFactory;
import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.Captcha;
import io.github.purecaptcha.core.CaptchaType;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.awt.Font;

/**
 * 现代化验证码演示
 * 展示优化后的验证码效果
 */
public class ModernCaptchaDemo {

    public static void main(String[] args) {
        System.out.println("=== PureCaptcha 现代化验证码演示 ===\n");

        // 创建输出目录
        File outputDir = new File("examples/modern-output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        try {
            // 1. 生成现代化字符验证码
            System.out.println("1. 生成现代化字符验证码...");
            generateAlphanumericCaptchas(outputDir);

            // 2. 生成现代化算术验证码
            System.out.println("2. 生成现代化算术验证码...");
            generateArithmeticCaptchas(outputDir);

            // 3. 生成现代化中文验证码
            System.out.println("3. 生成现代化中文验证码...");
            generateChineseCaptchas(outputDir);

            // 4. 生成自定义配置的验证码
            System.out.println("4. 生成自定义配置的验证码...");
            generateCustomStyledCaptchas(outputDir);

            System.out.println("\n✓ 所有现代化验证码已生成到: " + outputDir.getAbsolutePath());
            System.out.println("请打开文件查看效果!");

        } catch (IOException e) {
            System.err.println("生成验证码失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 生成字符验证码
     */
    private static void generateAlphanumericCaptchas(File outputDir) throws IOException {
        for (int i = 1; i <= 5; i++) {
            Captcha captcha = CaptchaFactory.create(CaptchaType.ALPHANUMERIC);
            String filename = String.format("modern_alphanumeric_%02d.png", i);
            File file = new File(outputDir, filename);
            ImageIO.write(captcha.getImage(), "PNG", file);
            System.out.println("   ✓ " + filename + " - 答案: " + captcha.getAnswer());
        }
    }

    /**
     * 生成算术验证码
     */
    private static void generateArithmeticCaptchas(File outputDir) throws IOException {
        for (int i = 1; i <= 5; i++) {
            Captcha captcha = CaptchaFactory.create(CaptchaType.ARITHMETIC);
            String filename = String.format("modern_arithmetic_%02d.png", i);
            File file = new File(outputDir, filename);
            ImageIO.write(captcha.getImage(), "PNG", file);
            System.out.println("   ✓ " + filename + " - 答案: " + captcha.getAnswer());
        }
    }

    /**
     * 生成中文验证码
     */
    private static void generateChineseCaptchas(File outputDir) throws IOException {
        for (int i = 1; i <= 5; i++) {
            Captcha captcha = CaptchaFactory.create(CaptchaType.CHINESE);
            String filename = String.format("modern_chinese_%02d.png", i);
            File file = new File(outputDir, filename);
            ImageIO.write(captcha.getImage(), "PNG", file);
            System.out.println("   ✓ " + filename + " - 答案: " + captcha.getAnswer());
        }
    }

    /**
     * 生成自定义配置的验证码
     */
    private static void generateCustomStyledCaptchas(File outputDir) throws IOException {
        // 大尺寸验证码
        CaptchaConfig largeConfig = CaptchaConfig.builder()
                .width(250)
                .height(80)
                .charLength(6)
                .font(new Font("Arial", Font.BOLD, 38))
                .interferenceLineCount(3)
                .build();

        Captcha largeCaptcha = CaptchaFactory.create(CaptchaType.ALPHANUMERIC, largeConfig);
        File largeFile = new File(outputDir, "modern_large_captcha.png");
        ImageIO.write(largeCaptcha.getImage(), "PNG", largeFile);
        System.out.println("   ✓ modern_large_captcha.png - 答案: " + largeCaptcha.getAnswer());

        // 紧凑型验证码
        CaptchaConfig compactConfig = CaptchaConfig.builder()
                .width(120)
                .height(40)
                .charLength(4)
                .font(new Font("Arial", Font.BOLD, 24))
                .interferenceLineCount(2)
                .build();

        Captcha compactCaptcha = CaptchaFactory.create(CaptchaType.ALPHANUMERIC, compactConfig);
        File compactFile = new File(outputDir, "modern_compact_captcha.png");
        ImageIO.write(compactCaptcha.getImage(), "PNG", compactFile);
        System.out.println("   ✓ modern_compact_captcha.png - 答案: " + compactCaptcha.getAnswer());

        // 纯数字验证码
        CaptchaConfig numericConfig = CaptchaConfig.builder()
                .charType("NUMERIC")
                .charLength(6)
                .build();

        Captcha numericCaptcha = CaptchaFactory.create(CaptchaType.ALPHANUMERIC, numericConfig);
        File numericFile = new File(outputDir, "modern_numeric_captcha.png");
        ImageIO.write(numericCaptcha.getImage(), "PNG", numericFile);
        System.out.println("   ✓ modern_numeric_captcha.png - 答案: " + numericCaptcha.getAnswer());
    }
}

