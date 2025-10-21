import io.github.purecaptcha.CaptchaFactory;
import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.CaptchaType;
import io.github.purecaptcha.model.SliderCaptchaResult;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * 测试严格验证模式
 * 
 * 优化：
 * 1. 相似度阈值：85% → 90%（更严格）
 * 2. 坐标限制：tolerance*2 → tolerance（收紧30px→15px）
 */
public class TestStrictVerification {

    public static void main(String[] args) throws IOException {
        String outputDir = "strict-verify-test/";
        new File(outputDir).mkdirs();

        System.out.println("========================================================");
        System.out.println("     滑动验证码 - 严格验证模式测试");
        System.out.println("  优化：相似度90% + 严格坐标限制");
        System.out.println("========================================================\n");

        // 测试3组验证码
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

        System.out.println("验证码已生成");
        System.out.println("  正确位置: " + correctX + "px");
        System.out.println("  容差范围: ±" + tolerance + "px");
        System.out.println();

        // 模拟各种偏差的验证
        System.out.println("【严格验证测试】");
        System.out.println("位置      | 偏差  | 验证结果 | 预期");
        System.out.println("----------|-------|----------|----------");

        int[] testPositions = {
            correctX,           // 0px偏差 - 应该通过
            correctX + 3,       // 3px偏差 - 应该通过
            correctX - 5,       // 5px偏差 - 应该通过
            correctX + 8,       // 8px偏差 - 可能通过
            correctX - 10,      // 10px偏差 - 可能通过
            correctX + 15,      // 15px偏差 - 临界，可能失败
            correctX - 18,      // 18px偏差 - 应该失败
            correctX + 25,      // 25px偏差 - 应该失败
            correctX - 30       // 30px偏差 - 应该失败
        };

        String[] expected = {
            "通过", "通过", "通过", "通过", "通过",
            "临界", "失败", "失败", "失败"
        };

        for (int i = 0; i < testPositions.length; i++) {
            int userX = testPositions[i];
            boolean passed = result.verifyPosition(userX);
            int diff = Math.abs(userX - correctX);
            String direction = userX > correctX ? "右" : (userX < correctX ? "左" : "准");
            String status = passed ? "✓ 通过" : "✗ 失败";

            System.out.printf("%3dpx (%s%2d) | %2dpx | %-8s | %s\n", 
                userX, direction, diff, diff, status, expected[i]);
        }

        System.out.println("\n【关键观察】");
        System.out.println("  - 0-10px偏差：应该通过（视觉对齐）");
        System.out.println("  - 10-15px偏差：临界区域");
        System.out.println("  - 15px以上：应该失败（明显偏离）");
    }
}

