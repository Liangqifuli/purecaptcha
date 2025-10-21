import io.github.purecaptcha.CaptchaFactory;
import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.Captcha;
import io.github.purecaptcha.core.CaptchaType;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * 测试算术验证码的答案验证
 * 核查加减乘除是否都有答案错误的问题
 */
public class TestArithmeticVerification {
    public static void main(String[] args) throws IOException {
        // 创建输出目录
        File outputDir = new File("examples/arithmetic-verification-test");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║        测试算术验证码 - 验证答案正确性                         ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        // 测试每种运算符各10次
        testOperator("加法 (+)", "ADD_SUBTRACT", 10, outputDir);
        System.out.println();
        testOperator("减法 (-)", "ADD_SUBTRACT", 10, outputDir);
        System.out.println();
        testOperator("乘法 (×)", "ALL", 10, outputDir);
        System.out.println();
        testOperator("除法 (÷)", "ALL", 10, outputDir);

        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                        测试完成！                              ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println("\n📁 输出目录: examples/arithmetic-verification-test/");
        System.out.println("\n⚠️  请检查上面的输出，看是否有答案不匹配的情况");
    }

    private static void testOperator(String operatorName, String operatorType, int count, File outputDir) throws IOException {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  " + operatorName + " 运算测试");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        int successCount = 0;
        int failCount = 0;

        for (int i = 1; i <= count; i++) {
            CaptchaConfig config = CaptchaConfig.builder()
                    .width(200)
                    .height(80)
                    .operatorType(operatorType)
                    .build();

            Captcha captcha = CaptchaFactory.create(CaptchaType.ARITHMETIC, config);
            
            // 获取答案
            String answer = captcha.getAnswer();
            
            // 保存图片
            String filename = String.format("test_%s_%02d.png", 
                    operatorName.replaceAll("[^a-zA-Z]", ""), i);
            File outputFile = new File(outputDir, filename);
            ImageIO.write(captcha.getImage(), "png", outputFile);
            
            // 验证答案（模拟用户输入）
            boolean verified = captcha.verify(answer);
            
            if (verified) {
                successCount++;
                System.out.printf("  ✅ [%02d] 答案: %-4s | 验证: 成功 ✓\n", i, answer);
            } else {
                failCount++;
                System.out.printf("  ❌ [%02d] 答案: %-4s | 验证: 失败 ✗ ⚠️  BUG发现！\n", i, answer);
            }
        }

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.printf("  总计: %d | 成功: %d | 失败: %d", count, successCount, failCount);
        if (failCount > 0) {
            System.out.print(" ⚠️  发现验证错误！");
        }
        System.out.println();
    }
}

