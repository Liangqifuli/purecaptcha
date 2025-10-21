import io.github.purecaptcha.CaptchaFactory;
import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.Captcha;
import io.github.purecaptcha.core.CaptchaType;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 验证码功能验证测试
 * <p>
 * 生成多种类型的验证码样本,用于人工和自动化验证
 */
public class VerificationTest {

    private static final String OUTPUT_DIR = "verification-output";
    private static int totalTests = 0;
    private static int passedTests = 0;

    public static void main(String[] args) throws Exception {
        File outputDir = new File(OUTPUT_DIR);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║         PureCaptcha 功能验证测试                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        // 测试1: 字符验证码基础功能
        test1_BasicAlphanumeric(outputDir, timestamp);

        // 测试2: 字符验证码不同配置
        test2_AlphanumericVariations(outputDir, timestamp);

        // 测试3: 算术验证码基础功能
        test3_BasicArithmetic(outputDir, timestamp);

        // 测试4: 算术验证码不同运算符
        test4_ArithmeticOperators(outputDir, timestamp);

        // 测试5: 答案验证功能
        test5_AnswerVerification();

        // 测试6: 批量生成测试(性能和稳定性)
        test6_BatchGeneration();

        // 测试7: 边界条件测试
        test7_EdgeCases(outputDir, timestamp);

        // 打印总结
        printSummary();

        System.out.println("\n所有验证码样本已保存到: " + new File(OUTPUT_DIR).getAbsolutePath());
        System.out.println("请打开图片文件进行人工检查!");
    }

    /**
     * 测试1: 字符验证码基础功能
     */
    private static void test1_BasicAlphanumeric(File dir, String timestamp) throws Exception {
        System.out.println("【测试1】字符验证码 - 基础功能");
        System.out.println("─────────────────────────────────────────────────────");

        for (int i = 1; i <= 5; i++) {
            Captcha captcha = CaptchaFactory.create(CaptchaType.ALPHANUMERIC);
            String filename = String.format("%s_01_basic_alphanumeric_%02d.png", timestamp, i);
            File output = new File(dir, filename);
            ImageIO.write(captcha.getImage(), "PNG", output);

            boolean lengthOk = captcha.getAnswer().length() == 5;
            boolean imageOk = captcha.getImage() != null;
            boolean sizeOk = captcha.getWidth() == 150 && captcha.getHeight() == 50;

            checkTest("样本" + i, lengthOk && imageOk && sizeOk,
                String.format("答案: %s | 尺寸: %dx%d | 文件: %s",
                    captcha.getAnswer(), captcha.getWidth(), captcha.getHeight(), filename));
        }
        System.out.println();
    }

    /**
     * 测试2: 字符验证码不同配置
     */
    private static void test2_AlphanumericVariations(File dir, String timestamp) throws Exception {
        System.out.println("【测试2】字符验证码 - 不同配置");
        System.out.println("─────────────────────────────────────────────────────");

        // 纯数字
        testConfig("纯数字(4位)", dir, timestamp, "02_numeric",
            CaptchaConfig.builder().charType("NUMERIC").charLength(4).build());

        // 大写字母
        testConfig("大写字母(6位)", dir, timestamp, "02_uppercase",
            CaptchaConfig.builder().charType("UPPERCASE").charLength(6).build());

        // 小写字母
        testConfig("小写字母(5位)", dir, timestamp, "02_lowercase",
            CaptchaConfig.builder().charType("LOWERCASE").charLength(5).build());

        // 混合(排除易混淆)
        testConfig("混合(排除易混淆)", dir, timestamp, "02_mixed_exclude",
            CaptchaConfig.builder().charType("MIXED").excludeConfusingChars(true).charLength(8).build());

        // 自定义颜色
        testConfig("自定义颜色", dir, timestamp, "02_custom_color",
            CaptchaConfig.builder()
                .backgroundColor(new Color(255, 250, 205))
                .fontColor(new Color(139, 0, 0))
                .build());

        System.out.println();
    }

    /**
     * 测试3: 算术验证码基础功能
     */
    private static void test3_BasicArithmetic(File dir, String timestamp) throws Exception {
        System.out.println("【测试3】算术验证码 - 基础功能");
        System.out.println("─────────────────────────────────────────────────────");

        for (int i = 1; i <= 5; i++) {
            Captcha captcha = CaptchaFactory.create(CaptchaType.ARITHMETIC);
            String filename = String.format("%s_03_basic_arithmetic_%02d.png", timestamp, i);
            File output = new File(dir, filename);
            ImageIO.write(captcha.getImage(), "PNG", output);

            int answer = Integer.parseInt(captcha.getAnswer());
            boolean answerOk = answer > 0;
            boolean imageOk = captcha.getImage() != null;

            checkTest("样本" + i, answerOk && imageOk,
                String.format("答案: %s (正整数✓) | 文件: %s", captcha.getAnswer(), filename));
        }
        System.out.println();
    }

    /**
     * 测试4: 算术验证码不同运算符
     */
    private static void test4_ArithmeticOperators(File dir, String timestamp) throws Exception {
        System.out.println("【测试4】算术验证码 - 不同运算符");
        System.out.println("─────────────────────────────────────────────────────");

        // 仅加减
        testArithmeticConfig("仅加减", dir, timestamp, "04_add_subtract",
            CaptchaConfig.builder().operatorType("ADD_SUBTRACT").build(), 3);

        // 全部运算符
        testArithmeticConfig("全部运算符(含乘除)", dir, timestamp, "04_all_operators",
            CaptchaConfig.builder().operatorType("ALL").build(), 5);

        System.out.println();
    }

    /**
     * 测试5: 答案验证功能
     */
    private static void test5_AnswerVerification() {
        System.out.println("【测试5】答案验证功能");
        System.out.println("─────────────────────────────────────────────────────");

        // 测试字符验证码
        Captcha alphaCaptcha = CaptchaFactory.create(CaptchaType.ALPHANUMERIC);
        String answer = alphaCaptcha.getAnswer();

        checkTest("正确答案", alphaCaptcha.verify(answer),
            String.format("输入'%s' -> 通过", answer));

        checkTest("错误答案", !alphaCaptcha.verify("wrongAnswer"),
            "输入'wrongAnswer' -> 拒绝");

        checkTest("空字符串", !alphaCaptcha.verify(""),
            "输入'' -> 拒绝");

        checkTest("null值", !alphaCaptcha.verify(null),
            "输入null -> 拒绝");

        // 测试大小写(默认不区分)
        checkTest("大小写(不区分)", alphaCaptcha.verify(answer.toUpperCase()),
            String.format("输入'%s' -> 通过", answer.toUpperCase()));

        // 测试算术验证码
        Captcha arithCaptcha = CaptchaFactory.create(CaptchaType.ARITHMETIC);
        String arithAnswer = arithCaptcha.getAnswer();

        checkTest("算术正确答案", arithCaptcha.verify(arithAnswer),
            String.format("答案'%s' -> 通过", arithAnswer));

        checkTest("算术错误答案", !arithCaptcha.verify("999"),
            "输入'999' -> 拒绝");

        System.out.println();
    }

    /**
     * 测试6: 批量生成测试
     */
    private static void test6_BatchGeneration() {
        System.out.println("【测试6】批量生成测试(性能与稳定性)");
        System.out.println("─────────────────────────────────────────────────────");

        // 字符验证码批量生成
        long start = System.currentTimeMillis();
        int count = 100;
        boolean allSuccess = true;

        for (int i = 0; i < count; i++) {
            try {
                Captcha captcha = CaptchaFactory.create(CaptchaType.ALPHANUMERIC);
                if (captcha.getImage() == null || captcha.getAnswer() == null) {
                    allSuccess = false;
                    break;
                }
            } catch (Exception e) {
                allSuccess = false;
                break;
            }
        }

        long duration = System.currentTimeMillis() - start;
        checkTest("字符验证码批量生成", allSuccess,
            String.format("生成%d个验证码 | 耗时: %dms | 平均: %.1fms/个",
                count, duration, (double)duration/count));

        // 算术验证码批量生成
        start = System.currentTimeMillis();
        allSuccess = true;

        for (int i = 0; i < count; i++) {
            try {
                Captcha captcha = CaptchaFactory.create(CaptchaType.ARITHMETIC);
                int answer = Integer.parseInt(captcha.getAnswer());
                if (captcha.getImage() == null || answer <= 0) {
                    allSuccess = false;
                    break;
                }
            } catch (Exception e) {
                allSuccess = false;
                break;
            }
        }

        duration = System.currentTimeMillis() - start;
        checkTest("算术验证码批量生成", allSuccess,
            String.format("生成%d个验证码 | 耗时: %dms | 平均: %.1fms/个",
                count, duration, (double)duration/count));

        System.out.println();
    }

    /**
     * 测试7: 边界条件测试
     */
    private static void test7_EdgeCases(File dir, String timestamp) throws Exception {
        System.out.println("【测试7】边界条件测试");
        System.out.println("─────────────────────────────────────────────────────");

        // 最短字符验证码
        testConfig("最短字符(4位)", dir, timestamp, "07_shortest",
            CaptchaConfig.builder().charLength(4).build());

        // 最长字符验证码
        testConfig("最长字符(8位)", dir, timestamp, "07_longest",
            CaptchaConfig.builder().charLength(8).build());

        // 最小尺寸
        testConfig("最小尺寸(100x30)", dir, timestamp, "07_smallest",
            CaptchaConfig.builder().width(100).height(30).build());

        // 最大尺寸
        testConfig("最大尺寸(300x100)", dir, timestamp, "07_largest",
            CaptchaConfig.builder().width(300).height(100).build());

        // 无干扰线和噪点
        testConfig("无干扰", dir, timestamp, "07_no_interference",
            CaptchaConfig.builder()
                .interferenceLineCount(0)
                .noisePointCount(0)
                .build());

        // 最多干扰
        testConfig("最多干扰", dir, timestamp, "07_max_interference",
            CaptchaConfig.builder()
                .interferenceLineCount(10)
                .noisePointCount(200)
                .build());

        System.out.println();
    }

    // ==================== 辅助方法 ====================

    private static void testConfig(String name, File dir, String timestamp,
                                   String prefix, CaptchaConfig config) throws Exception {
        Captcha captcha = CaptchaFactory.create(CaptchaType.ALPHANUMERIC, config);
        String filename = String.format("%s_%s.png", timestamp, prefix);
        File output = new File(dir, filename);
        ImageIO.write(captcha.getImage(), "PNG", output);

        boolean success = captcha.getImage() != null && captcha.getAnswer() != null;
        checkTest(name, success,
            String.format("答案: %s | 文件: %s", captcha.getAnswer(), filename));
    }

    private static void testArithmeticConfig(String name, File dir, String timestamp,
                                            String prefix, CaptchaConfig config, int samples) throws Exception {
        for (int i = 1; i <= samples; i++) {
            Captcha captcha = CaptchaFactory.create(CaptchaType.ARITHMETIC, config);
            String filename = String.format("%s_%s_%02d.png", timestamp, prefix, i);
            File output = new File(dir, filename);
            ImageIO.write(captcha.getImage(), "PNG", output);

            int answer = Integer.parseInt(captcha.getAnswer());
            boolean success = answer > 0;

            checkTest(name + " #" + i, success,
                String.format("答案: %s | 文件: %s", captcha.getAnswer(), filename));
        }
    }

    private static void checkTest(String testName, boolean passed, String detail) {
        totalTests++;
        if (passed) {
            passedTests++;
            System.out.println("  ✓ " + testName + " - " + detail);
        } else {
            System.out.println("  ✗ " + testName + " - " + detail + " [失败]");
        }
    }

    private static void printSummary() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                      测试总结                                 ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.printf("║  总测试数: %-6d  通过: %-6d  失败: %-6d         ║%n",
            totalTests, passedTests, totalTests - passedTests);
        System.out.printf("║  通过率: %.1f%%                                              ║%n",
            (double)passedTests / totalTests * 100);
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        if (passedTests == totalTests) {
            System.out.println("\n🎉 恭喜! 所有测试通过!");
        } else {
            System.out.println("\n⚠️  有测试失败,请检查详细输出!");
        }
    }
}
