import io.github.purecaptcha.CaptchaFactory;
import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.Captcha;
import io.github.purecaptcha.core.CaptchaType;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * 测试十位数算术验证码
 * 验证数字内部字符是否紧密连接
 */
public class TestDoubleDigitArithmetic {
    public static void main(String[] args) throws IOException {
        // 创建输出目录
        File outputDir = new File("examples/double-digit-output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║      测试十位数算术验证码 - 数字内部紧密连接              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        // 生成20个样本，确保有十位数出现
        for (int i = 1; i <= 20; i++) {
            CaptchaConfig config = CaptchaConfig.builder()
                    .width(200)
                    .height(80)
                    .operatorType("ALL") // 使用所有运算符，增加多样性
                    .build();

            Captcha captcha = CaptchaFactory.create(CaptchaType.ARITHMETIC, config);
            
            // 保存图片
            File outputFile = new File(outputDir, String.format("arithmetic_%02d.png", i));
            ImageIO.write(captcha.getImage(), "png", outputFile);
            
            System.out.printf("✅ [%02d] 表达式: %-15s | 答案: %-3s | 保存至: %s\n",
                    i,
                    captcha.getAnswer() + " = ?",
                    captcha.getAnswer(),
                    outputFile.getName());
        }

        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                     测试完成！                            ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("\n📁 输出目录: examples/double-digit-output/");
        System.out.println("🔍 请检查图片，确认十位数（如10、13等）内部字符紧密连接");
        System.out.println("\n✓ 正确效果：10 + 13 = ? （1和0挨在一起，1和3挨在一起）");
        System.out.println("✗ 错误效果：1 0 + 1 3 = ? （数字内部有间距）");
    }
}

