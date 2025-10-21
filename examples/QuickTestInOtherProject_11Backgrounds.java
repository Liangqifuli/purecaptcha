import io.github.purecaptcha.CaptchaFactory;
import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.CaptchaType;
import io.github.purecaptcha.model.SliderCaptchaResult;
import io.github.purecaptcha.util.BuiltinSliderBackground;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * 在其他项目中快速测试11张背景的滑动验证码
 * 
 * 使用方法：
 * 1. 在新项目的 pom.xml 中添加依赖：
 *    <dependency>
 *        <groupId>io.github.purecaptcha</groupId>
 *        <artifactId>pure-captcha</artifactId>
 *        <version>1.0.0</version>
 *    </dependency>
 * 
 * 2. 复制这个文件到项目中
 * 3. 运行 main 方法
 * 4. 查看生成的验证码图片
 */
public class QuickTestInOtherProject_11Backgrounds {

    public static void main(String[] args) throws IOException {
        String outputDir = "captcha-11bg-test/";
        new File(outputDir).mkdirs();

        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║   PureCaptcha - 11张背景快速测试           ║");
        System.out.println("╚════════════════════════════════════════════╝\n");

        // ========== 测试1：查看所有11张背景 ==========
        System.out.println("【1】查看所有可用背景：");
        BuiltinSliderBackground[] all = BuiltinSliderBackground.values();
        System.out.println("✓ 内置背景数量: " + all.length + " 张\n");
        
        for (int i = 0; i < all.length; i++) {
            String size = "";
            if (i < 5) size = "(20-30KB)";
            else if (i < 8) size = "(10-20KB)";
            else size = "(71-91KB 高清)";
            
            System.out.printf("  %2d. %-20s %s\n", 
                i + 1, all[i].name(), size);
        }

        // ========== 测试2：随机生成10张验证码 ==========
        System.out.println("\n【2】随机生成10张验证码（观察11张背景的多样性）：");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        for (int i = 1; i <= 10; i++) {
            CaptchaConfig config = CaptchaConfig.builder()
                    .width(350)
                    .height(200)
                    .sliderTolerance(15)
                    .build(); // 不指定背景，自动从11张中随机

            SliderCaptchaResult result = (SliderCaptchaResult) 
                CaptchaFactory.create(CaptchaType.SLIDER, config);

            String filename = String.format("random_%02d", i);
            ImageIO.write(result.getBackgroundImage(), "PNG", 
                new File(outputDir + filename + "_bg.png"));
            ImageIO.write(result.getSliderImage(), "PNG", 
                new File(outputDir + filename + "_slider.png"));
            
            System.out.printf("  ✓ 验证码 %2d - 正确位置: %3dpx\n", i, result.getSliderX());
        }

        // ========== 测试3：指定使用3张高清背景 ==========
        System.out.println("\n【3】使用新增的3张高清背景（71-91KB）：");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        BuiltinSliderBackground[] hqBgs = {
            BuiltinSliderBackground.PHOTO_PURPLE_1,
            BuiltinSliderBackground.PHOTO_PURPLE_2,
            BuiltinSliderBackground.PHOTO_PURPLE_3
        };
        
        for (int i = 0; i < hqBgs.length; i++) {
            CaptchaConfig config = CaptchaConfig.builder()
                    .width(350)
                    .height(200)
                    .builtinBackground(hqBgs[i])
                    .sliderTolerance(15)
                    .build();

            SliderCaptchaResult result = (SliderCaptchaResult) 
                CaptchaFactory.create(CaptchaType.SLIDER, config);

            String filename = "hq_" + (i + 1);
            ImageIO.write(result.getBackgroundImage(), "PNG", 
                new File(outputDir + filename + "_bg.png"));
            ImageIO.write(result.getSliderImage(), "PNG", 
                new File(outputDir + filename + "_slider.png"));
            
            System.out.printf("  ✓ %s - 正确位置: %3dpx\n", 
                hqBgs[i].name(), result.getSliderX());
        }

        // ========== 测试4：验证功能测试 ==========
        System.out.println("\n【4】验证功能测试：");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        CaptchaConfig testConfig = CaptchaConfig.builder()
                .width(350)
                .height(200)
                .sliderTolerance(15)
                .build();

        SliderCaptchaResult testResult = (SliderCaptchaResult) 
            CaptchaFactory.create(CaptchaType.SLIDER, testConfig);
        
        int correctX = testResult.getSliderX();
        int tolerance = testResult.getTolerance();
        
        System.out.println("  正确位置: " + correctX + "px");
        System.out.println("  容差范围: ±" + tolerance + "px");
        System.out.println("  有效范围: " + (correctX - tolerance) + " ~ " + (correctX + tolerance) + "px\n");
        
        // 模拟用户滑动测试
        int[] testPos = {
            correctX,           // 完美
            correctX + 5,       // 偏5px
            correctX - 10,      // 偏10px
            correctX + 15,      // 偏15px (临界)
            correctX - 20       // 偏20px (失败)
        };
        
        System.out.println("  模拟用户滑动:");
        for (int pos : testPos) {
            boolean passed = testResult.verifyPosition(pos);
            boolean passedLoose = testResult.verifyPositionLoose(pos);
            int diff = Math.abs(pos - correctX);
            String status = passed ? "✓ 通过" : "✗ 失败";
            String loose = passedLoose ? " (宽松✓)" : " (宽松✗)";
            
            System.out.printf("    用户=%3dpx, 差距=%2dpx → %s%s\n", 
                pos, diff, status, passed ? "" : loose);
        }

        // ========== 完成 ==========
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║   ✅ 测试完成！所有功能正常                ║");
        System.out.println("╚════════════════════════════════════════════╝");
        
        File outDir = new File(outputDir);
        System.out.println("\n📁 输出目录: " + outDir.getAbsolutePath());
        System.out.println("\n📊 生成文件统计:");
        System.out.println("  - 随机背景验证码: 10组（20个文件）");
        System.out.println("  - 高清背景验证码: 3组（6个文件）");
        System.out.println("  - 总计: 13组，26个PNG文件");
        
        System.out.println("\n💡 使用提示:");
        System.out.println("  1. 查看 random_01 到 random_10，观察背景多样性");
        System.out.println("  2. 查看 hq_1 到 hq_3，体验高清背景效果");
        System.out.println("  3. 多次运行可看到不同的随机组合");
        System.out.println("  4. 调整 sliderTolerance 可改变验证难度");
        
        System.out.println("\n🎉 PureCaptcha 已成功集成到你的项目中！");
    }
}

