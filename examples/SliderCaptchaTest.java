import io.github.purecaptcha.CaptchaFactory;
import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.Captcha;
import io.github.purecaptcha.core.CaptchaType;
import io.github.purecaptcha.model.SliderCaptchaResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 滑动拼图验证码测试
 */
public class SliderCaptchaTest {

    public static void main(String[] args) throws IOException {
        String outputDir = "examples/slider-output";
        new File(outputDir).mkdirs();

        System.out.println("=== 滑动拼图验证码测试 ===\n");

        // 测试1: 默认配置（使用生成的背景，10px容差）
        System.out.println("1. 默认配置测试");
        testDefaultSlider(outputDir);

        // 测试2: 自定义容差（更宽松，15px）
        System.out.println("\n2. 自定义容差测试（15px）");
        testCustomTolerance(outputDir);

        // 测试3: 自定义背景图片（如果有的话）
        System.out.println("\n3. 自定义背景图片测试");
        testCustomBackground(outputDir);

        System.out.println("\n✅ 所有测试完成！");
        System.out.println("📁 输出目录: " + new File(outputDir).getAbsolutePath());
    }

    /**
     * 测试1: 默认配置
     */
    private static void testDefaultSlider(String outputDir) throws IOException {
        // 使用工厂方法创建（默认350x200, 10px容差）
        Captcha captcha = CaptchaFactory.create(CaptchaType.SLIDER);
        SliderCaptchaResult result = (SliderCaptchaResult) captcha;

        // 保存图片
        saveSliderImages(result, outputDir, "01_default");

        // 输出信息
        System.out.println("   拼图X坐标: " + result.getSliderX());
        System.out.println("   拼图Y坐标: " + result.getSliderY());
        System.out.println("   验证容差: " + result.getTolerance() + "px");

        // 测试验证逻辑
        testVerification(result);
    }

    /**
     * 测试2: 自定义容差
     */
    private static void testCustomTolerance(String outputDir) throws IOException {
        CaptchaConfig config = CaptchaConfig.builder()
                .width(400)
                .height(250)
                .sliderTolerance(15)  // 更宽松的容差
                .build();

        Captcha captcha = CaptchaFactory.create(CaptchaType.SLIDER, config);
        SliderCaptchaResult result = (SliderCaptchaResult) captcha;

        saveSliderImages(result, outputDir, "02_custom_tolerance");

        System.out.println("   拼图X坐标: " + result.getSliderX());
        System.out.println("   验证容差: " + result.getTolerance() + "px");

        testVerification(result);
    }

    /**
     * 测试3: 自定义背景图片
     */
    private static void testCustomBackground(String outputDir) throws IOException {
        // 你可以指定自己的图片路径
        String customImagePath = "examples/custom-bg.jpg"; // 修改为你的图片路径

        File customImageFile = new File(customImagePath);
        if (!customImageFile.exists()) {
            System.out.println("   ⚠ 未找到自定义背景图片: " + customImagePath);
            System.out.println("   ℹ 使用默认生成背景");
            customImagePath = null;
        }

        CaptchaConfig config = CaptchaConfig.builder()
                .width(350)
                .height(200)
                .sliderBackgroundImagePath(customImagePath)
                .sliderTolerance(10)
                .build();

        Captcha captcha = CaptchaFactory.create(CaptchaType.SLIDER, config);
        SliderCaptchaResult result = (SliderCaptchaResult) captcha;

        saveSliderImages(result, outputDir, "03_custom_background");

        System.out.println("   拼图X坐标: " + result.getSliderX());
        System.out.println("   背景图片: " + (customImagePath != null ? customImagePath : "默认生成"));

        testVerification(result);
    }

    /**
     * 保存滑动验证码图片
     */
    private static void saveSliderImages(SliderCaptchaResult result, String outputDir, String prefix) throws IOException {
        // 保存背景图
        String bgPath = outputDir + "/" + prefix + "_background.png";
        ImageIO.write(result.getBackgroundImage(), "PNG", new File(bgPath));

        // 保存滑块图
        String sliderPath = outputDir + "/" + prefix + "_slider.png";
        ImageIO.write(result.getSliderImage(), "PNG", new File(sliderPath));

        System.out.println("   ✓ 背景图: " + bgPath);
        System.out.println("   ✓ 滑块图: " + sliderPath);
    }

    /**
     * 测试验证逻辑
     */
    private static void testVerification(SliderCaptchaResult result) {
        int correctX = result.getSliderX();
        int tolerance = result.getTolerance();

        // 测试正确位置
        System.out.println("   验证测试:");
        System.out.println("      ✓ 正确位置(" + correctX + "): " + result.verifyPosition(correctX));

        // 测试容差范围内
        int almostCorrect = correctX + tolerance / 2;
        System.out.println("      ✓ 容差内(" + almostCorrect + "): " + result.verifyPosition(almostCorrect));

        // 测试容差范围外
        int wrongPosition = correctX + tolerance + 10;
        System.out.println("      ✗ 容差外(" + wrongPosition + "): " + result.verifyPosition(wrongPosition));
    }
}

