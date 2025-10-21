import io.github.purecaptcha.CaptchaFactory;
import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.CaptchaType;
import io.github.purecaptcha.model.SliderCaptchaResult;
import io.github.purecaptcha.util.BuiltinSliderBackground;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试所有11张内置背景是否都能被随机到
 */
public class TestAllBackgrounds {

    public static void main(String[] args) throws IOException {
        String outputDir = "examples/all-backgrounds-test";
        new File(outputDir).mkdirs();

        System.out.println("=== 测试所有内置背景图片 ===\n");

        // 测试1：列出所有背景
        System.out.println("【1】列出所有内置背景（共" + BuiltinSliderBackground.values().length + "张）:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        for (int i = 0; i < BuiltinSliderBackground.values().length; i++) {
            BuiltinSliderBackground bg = BuiltinSliderBackground.values()[i];
            System.out.println((i + 1) + ". " + bg.name() + " - " + bg.getResourcePath());
        }

        // 测试2：逐个测试每张背景
        System.out.println("\n【2】逐个生成验证码:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        for (BuiltinSliderBackground bg : BuiltinSliderBackground.values()) {
            testSpecificBackground(outputDir, bg);
        }

        // 测试3：随机测试100次，统计分布
        System.out.println("\n【3】随机测试100次，统计分布:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        testRandomDistribution();

        System.out.println("\n✅ 测试完成！");
        System.out.println("📁 输出目录: " + new File(outputDir).getAbsolutePath());
        System.out.println("\n如果所有11张背景都能生成，说明问题已解决！");
    }

    private static void testSpecificBackground(String outputDir, BuiltinSliderBackground bg) throws IOException {
        CaptchaConfig config = CaptchaConfig.builder()
                .width(350)
                .height(200)
                .builtinBackground(bg)
                .sliderTolerance(15)
                .build();

        SliderCaptchaResult result = (SliderCaptchaResult) 
            CaptchaFactory.create(CaptchaType.SLIDER, config);

        String filename = bg.name().toLowerCase();
        ImageIO.write(result.getBackgroundImage(), "PNG", 
            new File(outputDir + "/" + filename + "_bg.png"));
        ImageIO.write(result.getSliderImage(), "PNG", 
            new File(outputDir + "/" + filename + "_slider.png"));

        System.out.println("  ✓ " + bg.name() + " - 生成成功");
    }

    private static void testRandomDistribution() {
        Map<String, Integer> distribution = new HashMap<>();
        
        // 随机100次
        for (int i = 0; i < 100; i++) {
            BuiltinSliderBackground bg = BuiltinSliderBackground.random();
            distribution.put(bg.name(), distribution.getOrDefault(bg.name(), 0) + 1);
        }

        // 输出统计
        System.out.println("100次随机测试结果:");
        for (BuiltinSliderBackground bg : BuiltinSliderBackground.values()) {
            int count = distribution.getOrDefault(bg.name(), 0);
            String bar = "█".repeat(count / 2); // 简单的柱状图
            System.out.printf("  %s: %2d次 %s\n", 
                String.format("%-20s", bg.name()), count, bar);
        }

        // 检查是否有背景没被随机到
        boolean allTested = true;
        for (BuiltinSliderBackground bg : BuiltinSliderBackground.values()) {
            if (!distribution.containsKey(bg.name())) {
                System.out.println("  ⚠ " + bg.name() + " 没有被随机到！");
                allTested = false;
            }
        }

        if (allTested) {
            System.out.println("\n  ✅ 所有11张背景都被随机到了！");
        } else {
            System.out.println("\n  ❌ 有背景没被随机到，请检查！");
        }
    }
}

