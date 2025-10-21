import io.github.purecaptcha.CaptchaFactory;
import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.Captcha;
import io.github.purecaptcha.core.CaptchaType;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * 算术验证码运算符测试
 * 演示加减乘除四种运算符
 */
public class TestArithmeticOperators {

    public static void main(String[] args) throws IOException {
        System.out.println("==========================================");
        System.out.println("    算术验证码运算符测试");
        System.out.println("==========================================\n");

        // 创建输出目录
        File outputDir = new File("examples/arithmetic-output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // 测试1: 仅加减运算（默认模式）
        System.out.println("【测试1】仅加减运算（默认模式）");
        System.out.println("------------------------------------------");
        testAddSubtractOnly(outputDir);

        System.out.println();

        // 测试2: 加减乘除全部运算
        System.out.println("【测试2】加减乘除全部运算");
        System.out.println("------------------------------------------");
        testAllOperators(outputDir);

        System.out.println("\n==========================================");
        System.out.println("测试完成！");
        System.out.println("输出目录：" + outputDir.getAbsolutePath());
        System.out.println("==========================================");
    }

    /**
     * 测试仅加减运算
     */
    private static void testAddSubtractOnly(File outputDir) throws IOException {
        CaptchaConfig config = CaptchaConfig.builder()
                .width(200)
                .height(80)
                .operatorType("ADD_SUBTRACT")  // 仅加减
                .build();

        System.out.println("配置：operatorType = \"ADD_SUBTRACT\"");
        System.out.println("支持运算：+ -");
        System.out.println();

        // 生成5个示例
        for (int i = 1; i <= 5; i++) {
            Captcha captcha = CaptchaFactory.create(CaptchaType.ARITHMETIC, config);
            
            String filename = String.format("add_subtract_%02d.png", i);
            File outputFile = new File(outputDir, filename);
            ImageIO.write(captcha.getImage(), "png", outputFile);
            
            System.out.println(String.format("  %d. 答案: %s → %s", 
                i, captcha.getAnswer(), filename));
        }
    }

    /**
     * 测试加减乘除全部运算
     */
    private static void testAllOperators(File outputDir) throws IOException {
        CaptchaConfig config = CaptchaConfig.builder()
                .width(200)
                .height(80)
                .operatorType("ALL")  // 加减乘除全部
                .build();

        System.out.println("配置：operatorType = \"ALL\"");
        System.out.println("支持运算：+ - × ÷");
        System.out.println();

        // 生成10个示例（更多样本以展示所有运算符）
        for (int i = 1; i <= 10; i++) {
            Captcha captcha = CaptchaFactory.create(CaptchaType.ARITHMETIC, config);
            
            String filename = String.format("all_operators_%02d.png", i);
            File outputFile = new File(outputDir, filename);
            ImageIO.write(captcha.getImage(), "png", outputFile);
            
            System.out.println(String.format("  %d. 答案: %s → %s", 
                i, captcha.getAnswer(), filename));
        }
    }
}

