package io.github.purecaptcha.examples;

import io.github.purecaptcha.CaptchaFactory;
import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.Captcha;
import io.github.purecaptcha.core.CaptchaType;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;

/**
 * PureCaptcha 使用示例
 * <p>
 * 演示各种验证码的生成和配置
 *
 * @author PureCaptcha
 * @version 1.0.0
 */
public class CaptchaExample {

    public static void main(String[] args) throws IOException {
        // 创建输出目录
        File outputDir = new File("examples/output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        System.out.println("=== PureCaptcha 使用示例 ===\n");

        // 示例1: 默认字符验证码
        example1_DefaultAlphanumeric(outputDir);

        // 示例2: 自定义配置的字符验证码
        example2_CustomAlphanumeric(outputDir);

        // 示例3: 纯数字验证码
        example3_NumericOnly(outputDir);

        // 示例4: 默认算术验证码
        example4_DefaultArithmetic(outputDir);

        // 示例5: 包含乘除的算术验证码
        example5_ArithmeticWithMultiplyDivide(outputDir);

        // 示例6: 验证用户输入
        example6_VerifyUserInput();

        System.out.println("\n所有示例已生成完毕,图片保存在: " + outputDir.getAbsolutePath());
    }

    /**
     * 示例1: 默认字符验证码
     */
    private static void example1_DefaultAlphanumeric(File outputDir) throws IOException {
        System.out.println("示例1: 默认字符验证码");

        Captcha captcha = CaptchaFactory.create(CaptchaType.ALPHANUMERIC);

        System.out.println("  类型: " + captcha.getType().getDescription());
        System.out.println("  答案: " + captcha.getAnswer());
        System.out.println("  尺寸: " + captcha.getWidth() + "x" + captcha.getHeight());

        File output = new File(outputDir, "example1-default-alphanumeric.png");
        ImageIO.write(captcha.getImage(), "PNG", output);
        System.out.println("  已保存: " + output.getName() + "\n");
    }

    /**
     * 示例2: 自定义配置的字符验证码
     */
    private static void example2_CustomAlphanumeric(File outputDir) throws IOException {
        System.out.println("示例2: 自定义配置的字符验证码");

        CaptchaConfig config = CaptchaConfig.builder()
                .width(200)
                .height(60)
                .charLength(6)
                .charType("UPPERCASE")
                .excludeConfusingChars(true)
                .caseSensitive(false)
                .backgroundColor(new Color(240, 248, 255))
                .fontColor(new Color(0, 0, 139))
                .font(new Font("Arial", Font.BOLD, 36))
                .interferenceLineCount(5)
                .noisePointCount(100)
                .build();

        Captcha captcha = CaptchaFactory.create(CaptchaType.ALPHANUMERIC, config);

        System.out.println("  类型: " + captcha.getType().getDescription());
        System.out.println("  答案: " + captcha.getAnswer());
        System.out.println("  配置: 6位大写字母,排除易混淆字符,不区分大小写");

        File output = new File(outputDir, "example2-custom-alphanumeric.png");
        ImageIO.write(captcha.getImage(), "PNG", output);
        System.out.println("  已保存: " + output.getName() + "\n");
    }

    /**
     * 示例3: 纯数字验证码
     */
    private static void example3_NumericOnly(File outputDir) throws IOException {
        System.out.println("示例3: 纯数字验证码");

        CaptchaConfig config = CaptchaConfig.builder()
                .charType("NUMERIC")
                .charLength(4)
                .excludeConfusingChars(false) // 数字验证码通常不需要排除
                .build();

        Captcha captcha = CaptchaFactory.create(CaptchaType.ALPHANUMERIC, config);

        System.out.println("  类型: " + captcha.getType().getDescription());
        System.out.println("  答案: " + captcha.getAnswer());
        System.out.println("  配置: 4位纯数字");

        File output = new File(outputDir, "example3-numeric-only.png");
        ImageIO.write(captcha.getImage(), "PNG", output);
        System.out.println("  已保存: " + output.getName() + "\n");
    }

    /**
     * 示例4: 默认算术验证码
     */
    private static void example4_DefaultArithmetic(File outputDir) throws IOException {
        System.out.println("示例4: 默认算术验证码(仅加减)");

        Captcha captcha = CaptchaFactory.create(CaptchaType.ARITHMETIC);

        System.out.println("  类型: " + captcha.getType().getDescription());
        System.out.println("  答案: " + captcha.getAnswer());

        File output = new File(outputDir, "example4-arithmetic-add-subtract.png");
        ImageIO.write(captcha.getImage(), "PNG", output);
        System.out.println("  已保存: " + output.getName() + "\n");
    }

    /**
     * 示例5: 包含乘除的算术验证码
     */
    private static void example5_ArithmeticWithMultiplyDivide(File outputDir) throws IOException {
        System.out.println("示例5: 包含乘除的算术验证码");

        CaptchaConfig config = CaptchaConfig.builder()
                .operatorType("ALL")
                .width(200)
                .height(60)
                .backgroundColor(Color.WHITE)
                .fontColor(Color.BLACK)
                .font(new Font("Arial", Font.BOLD, 28))
                .build();

        Captcha captcha = CaptchaFactory.create(CaptchaType.ARITHMETIC, config);

        System.out.println("  类型: " + captcha.getType().getDescription());
        System.out.println("  答案: " + captcha.getAnswer());

        File output = new File(outputDir, "example5-arithmetic-all-operators.png");
        ImageIO.write(captcha.getImage(), "PNG", output);
        System.out.println("  已保存: " + output.getName() + "\n");
    }

    /**
     * 示例6: 验证用户输入
     */
    private static void example6_VerifyUserInput() {
        System.out.println("示例6: 验证用户输入");

        // 生成验证码
        Captcha captcha = CaptchaFactory.create(CaptchaType.ALPHANUMERIC);
        String correctAnswer = captcha.getAnswer();

        System.out.println("  正确答案: " + correctAnswer);

        // 模拟用户输入
        String[] userInputs = {correctAnswer, correctAnswer.toUpperCase(), "wrongAnswer", ""};

        for (String input : userInputs) {
            boolean isValid = captcha.verify(input);
            System.out.println("  输入 '" + input + "' -> " + (isValid ? "验证通过" : "验证失败"));
        }
        System.out.println();
    }
}
