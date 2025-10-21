import io.github.purecaptcha.CaptchaFactory;
import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.CaptchaType;
import io.github.purecaptcha.model.SliderCaptchaResult;
import io.github.purecaptcha.util.BuiltinSliderBackground;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * 测试新的智能验证算法
 * 
 * 【新算法】基于图像相似度匹配，而不是简单的坐标对比
 * - 自动适应视觉对齐
 * - 在容差范围内搜索最佳匹配位置
 * - 计算图像相似度，超过阈值即通过
 */
public class TestSmartVerification {

    public static void main(String[] args) throws IOException {
        String outputDir = "smart-verify-test/";
        new File(outputDir).mkdirs();

        System.out.println("========================================================");
        System.out.println("     滑动验证码 - 智能验证算法测试");
        System.out.println("  (基于图像相似度，解决明明对齐了却失败的问题)");
        System.out.println("========================================================\n");

        // 测试多组验证码
        for (int testCase = 1; testCase <= 3; testCase++) {
            System.out.println("=======================================================");
            System.out.println("测试案例 " + testCase);
            System.out.println("=======================================================");
            
            testVerificationCase(testCase, outputDir);
            System.out.println();
        }

        System.out.println("========================================================");
        System.out.println("  测试完成!");
        System.out.println("========================================================");
        System.out.println("\n输出目录: " + new File(outputDir).getAbsolutePath());
        System.out.println("\n查看详细验证报告，了解智能验证的工作原理");
    }

    private static void testVerificationCase(int caseNum, String outputDir) throws IOException {
        // 生成验证码
        CaptchaConfig config = CaptchaConfig.builder()
                .width(350)
                .height(200)
                .sliderTolerance(15)
                .build();

        SliderCaptchaResult result = (SliderCaptchaResult) 
            CaptchaFactory.create(CaptchaType.SLIDER, config);

        // 保存图片
        String prefix = "case" + caseNum;
        ImageIO.write(result.getBackgroundImage(), "PNG", 
            new File(outputDir + prefix + "_bg.png"));
        ImageIO.write(result.getSliderImage(), "PNG", 
            new File(outputDir + prefix + "_slider.png"));

        int correctX = result.getSliderX();
        int tolerance = result.getTolerance();

        System.out.println("✓ 验证码已生成");
        System.out.println("  正确位置: " + correctX + "px");
        System.out.println("  容差范围: ±" + tolerance + "px");
        System.out.println();

        // 模拟各种用户滑动情况
        int[] testPositions = {
            correctX,                    // 完美对齐
            correctX + 2,                // 偏右2px
            correctX - 3,                // 偏左3px
            correctX + 8,                // 偏右8px
            correctX - 10,               // 偏左10px
            correctX + 15,               // 偏右15px（临界）
            correctX - 18,               // 偏左18px（略超）
            correctX + 25                // 偏右25px（明显偏离）
        };

        System.out.println("【验证测试】模拟用户滑动到不同位置：");
        System.out.println("位置      | 偏差  | 智能验证 | 说明");
        System.out.println("----------|-------|----------|------------------------");

        for (int userX : testPositions) {
            boolean passed = result.verifyPosition(userX);
            int diff = Math.abs(userX - correctX);
            String direction = userX > correctX ? "右" : (userX < correctX ? "左" : "准");
            String status = passed ? "✓ 通过" : "✗ 失败";
            String note = "";

            if (diff == 0) {
                note = "完美对齐";
            } else if (diff <= 5) {
                note = "轻微偏差，视觉几乎对齐";
            } else if (diff <= 10) {
                note = "小偏差，视觉可能看起来对齐";
            } else if (diff <= 15) {
                note = "中等偏差";
            } else if (diff <= 20) {
                note = "较大偏差";
            } else {
                note = "明显偏离";
            }

            System.out.printf("%3dpx (%s%2d) | %2dpx | %-8s | %s\n", 
                userX, direction, diff, diff, status, note);
        }

        // 显示一个位置的详细验证报告
        System.out.println("\n【详细验证报告示例】（位置: " + (correctX + 10) + "px）");
        System.out.println(result.getVerifyDetails(correctX + 10));
    }
}

