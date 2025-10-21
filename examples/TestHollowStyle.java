import io.github.purecaptcha.CaptchaFactory;
import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.Captcha;
import io.github.purecaptcha.core.CaptchaType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 测试新的镂空风格验证码
 * 
 * 展示优化后的验证码效果：
 * - 镂空字体（中间镂空，只有轮廓）
 * - 字符间距合理（不紧密）
 * - 鲜艳的随机颜色
 * - 干扰线横穿字体
 */
public class TestHollowStyle {
    
    public static void main(String[] args) throws IOException {
        // 创建输出目录
        File outputDir = new File("examples/hollow-style-output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                               ║");
        System.out.println("║           新版镂空风格验证码测试                              ║");
        System.out.println("║                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        System.out.println("【优化特点】");
        System.out.println("  ✓ 镂空字体 - 中间镂空，只有彩色轮廓");
        System.out.println("  ✓ 合理间距 - 字符间距15px，不紧密不臃肿");
        System.out.println("  ✓ 鲜艳颜色 - 每个字符使用不同的鲜艳随机颜色");
        System.out.println("  ✓ 干扰线横穿 - 60%干扰线横穿字体区域");
        System.out.println("  ✓ 清晰字体 - 使用Arial，字号适中");
        System.out.println();
        
        // 创建配置（使用默认宽度200，高度80）
        CaptchaConfig config = CaptchaConfig.builder()
                .width(200)
                .height(80)
                .build();
        
        // 1. 测试字符验证码
        System.out.println("【1. 字符验证码】");
        for (int i = 1; i <= 5; i++) {
            Captcha captcha = CaptchaFactory.create(CaptchaType.ALPHANUMERIC, config);
            String filename = String.format("hollow_alphanumeric_%02d.png", i);
            File outputFile = new File(outputDir, filename);
            ImageIO.write(captcha.getImage(), "PNG", outputFile);
            System.out.println("  " + (i) + ". 答案: " + captcha.getAnswer() + " → " + filename);
        }
        System.out.println();
        
        // 2. 测试算术验证码
        System.out.println("【2. 算术验证码】");
        for (int i = 1; i <= 5; i++) {
            Captcha captcha = CaptchaFactory.create(CaptchaType.ARITHMETIC, config);
            String filename = String.format("hollow_arithmetic_%02d.png", i);
            File outputFile = new File(outputDir, filename);
            ImageIO.write(captcha.getImage(), "PNG", outputFile);
            System.out.println("  " + (i) + ". 答案: " + captcha.getAnswer() + " → " + filename);
        }
        System.out.println();
        
        // 3. 测试中文验证码
        System.out.println("【3. 中文验证码】");
        for (int i = 1; i <= 5; i++) {
            Captcha captcha = CaptchaFactory.create(CaptchaType.CHINESE, config);
            String filename = String.format("hollow_chinese_%02d.png", i);
            File outputFile = new File(outputDir, filename);
            ImageIO.write(captcha.getImage(), "PNG", outputFile);
            System.out.println("  " + (i) + ". 答案: " + captcha.getAnswer() + " → " + filename);
        }
        System.out.println();
        
        // 4. 测试GIF验证码
        System.out.println("【4. GIF动图验证码】");
        for (int i = 1; i <= 5; i++) {
            Captcha captcha = CaptchaFactory.create(CaptchaType.ANIMATED_GIF, config);
            String filename = String.format("hollow_gif_%02d.gif", i);
            File outputFile = new File(outputDir, filename);
            
            // 保存GIF数据
            java.io.FileOutputStream fos = new java.io.FileOutputStream(outputFile);
            fos.write(captcha.getImageData());
            fos.close();
            
            System.out.println("  " + (i) + ". 答案: " + captcha.getAnswer() + " → " + filename);
        }
        System.out.println();
        
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                               ║");
        System.out.println("║           ✅ 测试完成！                                        ║");
        System.out.println("║                                                               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("输出目录: " + outputDir.getAbsolutePath());
        System.out.println("生成文件数: 20 个（字符5张 + 算术5张 + 中文5张 + GIF5张）");
        System.out.println();
        System.out.println("【对比说明】");
        System.out.println("  旧版：字体臃肿、紧密、填充实心、颜色暗淡");
        System.out.println("  新版：字体镂空、间距合理、只有轮廓、颜色鲜艳");
        System.out.println();
    }
}

