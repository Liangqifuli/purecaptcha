import io.github.purecaptcha.CaptchaFactory;
import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.Captcha;
import io.github.purecaptcha.core.CaptchaType;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.io.File;

/**
 * PureCaptcha 快速示例
 */
public class QuickStart {

    public static void main(String[] args) throws Exception {
        // 创建输出目录
        File outputDir = new File("output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        System.out.println("=== PureCaptcha 快速示例 ===\n");

        // 示例1: 默认字符验证码
        System.out.println("1. 生成默认字符验证码...");
        Captcha captcha1 = CaptchaFactory.create(CaptchaType.ALPHANUMERIC);
        System.out.println("   答案: " + captcha1.getAnswer());
        ImageIO.write(captcha1.getImage(), "PNG", new File(outputDir, "alphanumeric.png"));

        // 示例2: 自定义字符验证码
        System.out.println("\n2. 生成自定义字符验证码...");
        CaptchaConfig config = CaptchaConfig.builder()
                .width(200)
                .height(60)
                .charLength(6)
                .charType("UPPERCASE")
                .backgroundColor(new Color(240, 248, 255))
                .build();
        Captcha captcha2 = CaptchaFactory.create(CaptchaType.ALPHANUMERIC, config);
        System.out.println("   答案: " + captcha2.getAnswer());
        ImageIO.write(captcha2.getImage(), "PNG", new File(outputDir, "alphanumeric-custom.png"));

        // 示例3: 算术验证码
        System.out.println("\n3. 生成算术验证码...");
        Captcha captcha3 = CaptchaFactory.create(CaptchaType.ARITHMETIC);
        System.out.println("   答案: " + captcha3.getAnswer());
        ImageIO.write(captcha3.getImage(), "PNG", new File(outputDir, "arithmetic.png"));

        // 示例4: 验证用户输入
        System.out.println("\n4. 验证用户输入:");
        System.out.println("   正确答案 '" + captcha1.getAnswer() + "' -> " + captcha1.verify(captcha1.getAnswer()));
        System.out.println("   错误答案 'wrong' -> " + captcha1.verify("wrong"));

        System.out.println("\n所有验证码已生成到 output/ 目录");
    }
}
