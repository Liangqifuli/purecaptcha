import io.github.purecaptcha.CaptchaFactory;
import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.CaptchaType;
import io.github.purecaptcha.model.SliderCaptchaResult;
import io.github.purecaptcha.util.BuiltinSliderBackground;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * 测试内置背景图片功能
 */
public class BuiltinBackgroundTest {

    public static void main(String[] args) throws IOException {
        String outputDir = "examples/builtin-bg-test";
        new File(outputDir).mkdirs();

        System.out.println("=== 内置背景图片测试 ===\n");

        // 测试1: 默认配置（随机选择内置背景）
        System.out.println("1. 默认配置 - 随机内置背景");
        testDefaultBuiltin(outputDir);

        // 测试2: 指定蓝色渐变背景
        System.out.println("\n2. 指定内置背景 - 蓝色渐变");
        testSpecificBuiltin(outputDir, BuiltinSliderBackground.BLUE_GRADIENT, "blue");

        // 测试3: 指定绿色自然背景
        System.out.println("\n3. 指定内置背景 - 绿色自然");
        testSpecificBuiltin(outputDir, BuiltinSliderBackground.GREEN_NATURE, "green");

        // 测试4: 指定紫色梦幻背景
        System.out.println("\n4. 指定内置背景 - 紫色梦幻");
        testSpecificBuiltin(outputDir, BuiltinSliderBackground.PURPLE_DREAM, "purple");

        // 测试5: 指定橙色温暖背景
        System.out.println("\n5. 指定内置背景 - 橙色温暖");
        testSpecificBuiltin(outputDir, BuiltinSliderBackground.ORANGE_WARM, "orange");

        // 测试6: 指定粉色浪漫背景
        System.out.println("\n6. 指定内置背景 - 粉色浪漫");
        testSpecificBuiltin(outputDir, BuiltinSliderBackground.PINK_ROMANTIC, "pink");

        System.out.println("\n✅ 所有测试完成！");
        System.out.println("📁 输出目录: " + new File(outputDir).getAbsolutePath());
        System.out.println("\n💡 说明：这些背景图片已打包在jar中，在任何项目中都能使用！");
    }

    /**
     * 测试默认配置（随机内置背景）
     */
    private static void testDefaultBuiltin(String outputDir) throws IOException {
        SliderCaptchaResult result = (SliderCaptchaResult) CaptchaFactory.create(CaptchaType.SLIDER);

        String bgPath = outputDir + "/00_random_background.png";
        String sliderPath = outputDir + "/00_random_slider.png";

        ImageIO.write(result.getBackgroundImage(), "PNG", new File(bgPath));
        ImageIO.write(result.getSliderImage(), "PNG", new File(sliderPath));

        System.out.println("   ✓ 背景图: " + bgPath);
        System.out.println("   ✓ 滑块图: " + sliderPath);
        System.out.println("   拼图X坐标: " + result.getSliderX());
    }

    /**
     * 测试指定内置背景
     */
    private static void testSpecificBuiltin(String outputDir, BuiltinSliderBackground bg, String name) throws IOException {
        CaptchaConfig config = CaptchaConfig.builder()
                .width(350)
                .height(200)
                .builtinBackground(bg)
                .sliderTolerance(10)
                .build();

        SliderCaptchaResult result = (SliderCaptchaResult) CaptchaFactory.create(CaptchaType.SLIDER, config);

        String bgPath = outputDir + "/" + name + "_background.png";
        String sliderPath = outputDir + "/" + name + "_slider.png";

        ImageIO.write(result.getBackgroundImage(), "PNG", new File(bgPath));
        ImageIO.write(result.getSliderImage(), "PNG", new File(sliderPath));

        System.out.println("   ✓ 背景图: " + bgPath);
        System.out.println("   ✓ 滑块图: " + sliderPath);
        System.out.println("   拼图X坐标: " + result.getSliderX());
    }
}

