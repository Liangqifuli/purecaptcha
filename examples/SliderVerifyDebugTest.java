import io.github.purecaptcha.CaptchaFactory;
import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.CaptchaType;
import io.github.purecaptcha.model.SliderCaptchaResult;
import io.github.purecaptcha.util.BuiltinSliderBackground;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * 滑动验证码调试测试
 * 帮助诊断验证问题
 */
public class SliderVerifyDebugTest {

    public static void main(String[] args) throws IOException {
        String outputDir = "examples/slider-debug";
        new File(outputDir).mkdirs();

        System.out.println("=== 滑动验证码调试测试 ===\n");

        // 测试不同容差
        System.out.println("【测试1】默认容差（15px）");
        testWithTolerance(outputDir, 15, "01_default");

        System.out.println("\n【测试2】宽松容差（20px）");
        testWithTolerance(outputDir, 20, "02_loose");

        System.out.println("\n【测试3】严格容差（10px）");
        testWithTolerance(outputDir, 10, "03_strict");

        System.out.println("\n【测试4】超宽松容差（25px）- 推荐用于移动端");
        testWithTolerance(outputDir, 25, "04_mobile");

        System.out.println("\n=== 测试完成 ===");
        System.out.println("输出目录: " + new File(outputDir).getAbsolutePath());
        System.out.println("\n建议：");
        System.out.println("- 桌面端：使用15-20px容差");
        System.out.println("- 移动端：使用20-25px容差");
        System.out.println("- 如果用户反馈难以通过，建议使用20px或更大");
    }

    private static void testWithTolerance(String outputDir, int tolerance, String prefix) throws IOException {
        CaptchaConfig config = CaptchaConfig.builder()
                .width(350)
                .height(200)
                .builtinBackground(BuiltinSliderBackground.BLUE_GRADIENT)
                .sliderTolerance(tolerance)
                .build();

        SliderCaptchaResult result = (SliderCaptchaResult) CaptchaFactory.create(CaptchaType.SLIDER, config);

        // 保存图片
        ImageIO.write(result.getBackgroundImage(), "PNG", new File(outputDir + "/" + prefix + "_bg.png"));
        ImageIO.write(result.getSliderImage(), "PNG", new File(outputDir + "/" + prefix + "_slider.png"));

        int correctX = result.getSliderX();
        System.out.println("  正确位置: " + correctX + "px");
        System.out.println("  容差: ±" + tolerance + "px");
        System.out.println("  有效范围: " + (correctX - tolerance) + " ~ " + (correctX + tolerance) + "px");

        // 模拟不同的用户输入
        System.out.println("\n  模拟验证测试:");
        
        // 完全正确
        testPosition(result, correctX, "完全正确");
        
        // 偏左5px
        testPosition(result, correctX - 5, "偏左5px");
        
        // 偏右5px
        testPosition(result, correctX + 5, "偏右5px");
        
        // 偏左10px
        testPosition(result, correctX - 10, "偏左10px");
        
        // 偏右10px
        testPosition(result, correctX + 10, "偏右10px");
        
        // 偏左15px
        testPosition(result, correctX - 15, "偏左15px");
        
        // 偏右15px
        testPosition(result, correctX + 15, "偏右15px");

        // 偏左20px
        testPosition(result, correctX - 20, "偏左20px");
        
        // 偏右20px
        testPosition(result, correctX + 20, "偏右20px");
    }

    private static void testPosition(SliderCaptchaResult result, int userX, String desc) {
        boolean passed = result.verifyPosition(userX);
        boolean passedLoose = result.verifyPositionLoose(userX);
        int diff = result.getPositionDifference(userX);
        
        String icon = passed ? "✓" : "✗";
        String looseIcon = passedLoose ? "✓" : "✗";
        String direction = diff > 0 ? "需右移" + diff + "px" : (diff < 0 ? "需左移" + (-diff) + "px" : "正好");
        
        System.out.println(String.format(
            "    %s [标准%s 宽松%s] %s (位置:%d, %s)",
            icon, icon, looseIcon, desc, userX, direction
        ));
    }
}

