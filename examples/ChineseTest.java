import io.github.purecaptcha.CaptchaFactory;
import io.github.purecaptcha.core.Captcha;
import io.github.purecaptcha.core.CaptchaType;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * 中文验证码测试
 */
public class ChineseTest {

    public static void main(String[] args) {
        System.out.println("=== 中文验证码测试 ===\n");

        // 创建输出目录
        File outputDir = new File("examples/chinese-test");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        try {
            // 生成10张中文验证码
            for (int i = 1; i <= 10; i++) {
                Captcha captcha = CaptchaFactory.create(CaptchaType.CHINESE);
                
                String filename = String.format("chinese_test_%02d.png", i);
                File file = new File(outputDir, filename);
                
                ImageIO.write(captcha.getImage(), "PNG", file);
                
                System.out.println(String.format("✓ %s - 答案: %s", 
                    file.getAbsolutePath(), captcha.getAnswer()));
            }

            System.out.println("\n✓ 所有中文验证码已生成到: " + outputDir.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("生成验证码失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

