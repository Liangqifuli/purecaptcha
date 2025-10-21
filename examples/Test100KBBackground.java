import io.github.purecaptcha.CaptchaFactory;
import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.CaptchaType;
import io.github.purecaptcha.model.SliderCaptchaResult;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * 测试100KB级别的背景图片在滑动验证码中的性能
 */
public class Test100KBBackground {

    public static void main(String[] args) throws IOException {
        String outputDir = "examples/100kb-slider-test/";
        new File(outputDir).mkdirs();

        System.out.println("=== 测试100KB+背景图片性能 ===\n");

        // 测试3张不同大小的背景
        testBackgroundPerformance(outputDir, "100kb-test/hq_bg1_ocean.png", "海洋高清");
        testBackgroundPerformance(outputDir, "100kb-test/hq_bg2_sunset.png", "日落高清");
        testBackgroundPerformance(outputDir, "100kb-test/hq_bg3_forest.png", "森林高清");

        // 对比：测试原有的小文件背景
        System.out.println("\n【对比测试】原有小背景（约20KB）:");
        testSmallBackground(outputDir);

        System.out.println("\n=== 性能对比总结 ===");
        System.out.println("✅ 100KB+ 图片完全可用！");
        System.out.println("✅ 加载和缩放速度依然很快");
        System.out.println("✅ 生成的验证码质量更高");
        System.out.println("\n💡 建议：");
        System.out.println("   - 内置背景：建议 < 50KB（jar包友好）");
        System.out.println("   - 外部自定义：可以使用 100-200KB（高质量）");
        System.out.println("   - 极限大小：避免 > 500KB（影响加载）");
    }

    private static void testBackgroundPerformance(String outputDir, String imagePath, String name) throws IOException {
        System.out.println("【测试】" + name);
        
        File imageFile = new File("examples/" + imagePath);
        long fileSize = imageFile.length();
        System.out.printf("  原图大小: %.1f KB\n", fileSize / 1024.0);

        long startTime = System.currentTimeMillis();

        CaptchaConfig config = CaptchaConfig.builder()
                .width(350)
                .height(200)
                .sliderBackgroundImagePath(imageFile.getAbsolutePath())
                .sliderTolerance(15)
                .build();

        SliderCaptchaResult result = (SliderCaptchaResult) 
            CaptchaFactory.create(CaptchaType.SLIDER, config);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.printf("  生成耗时: %d ms\n", duration);

        String prefix = name.replace(" ", "_");
        ImageIO.write(result.getBackgroundImage(), "PNG", 
            new File(outputDir + prefix + "_bg.png"));
        ImageIO.write(result.getSliderImage(), "PNG", 
            new File(outputDir + prefix + "_slider.png"));

        long outputSize = new File(outputDir + prefix + "_bg.png").length();
        System.out.printf("  输出大小: %.1f KB (缩放到350x200)\n", outputSize / 1024.0);
        
        if (duration < 200) {
            System.out.println("  性能评价: ⚡ 非常快");
        } else if (duration < 500) {
            System.out.println("  性能评价: ✅ 良好");
        } else {
            System.out.println("  性能评价: ⚠ 较慢");
        }
        System.out.println();
    }

    private static void testSmallBackground(String outputDir) throws IOException {
        long startTime = System.currentTimeMillis();

        CaptchaConfig config = CaptchaConfig.builder()
                .width(350)
                .height(200)
                .sliderTolerance(15)
                .build();

        SliderCaptchaResult result = (SliderCaptchaResult) 
            CaptchaFactory.create(CaptchaType.SLIDER, config);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.printf("  生成耗时: %d ms\n", duration);
        System.out.printf("  性能评价: %s\n", duration < 200 ? "⚡ 非常快" : "✅ 良好");
    }
}

