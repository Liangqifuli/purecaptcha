package io.github.purecaptcha;

import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.Captcha;
import io.github.purecaptcha.core.CaptchaType;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * 算术验证码测试
 *
 * @author PureCaptcha
 * @version 1.0.0
 */
public class ArithmeticCaptchaTest {

    @Test
    public void testGenerateDefaultCaptcha() {
        Captcha captcha = CaptchaFactory.create(CaptchaType.ARITHMETIC);

        assertNotNull("验证码对象不应为null", captcha);
        assertNotNull("图像不应为null", captcha.getImage());
        assertNotNull("答案不应为null", captcha.getAnswer());
        assertEquals("验证码类型应为ARITHMETIC", CaptchaType.ARITHMETIC, captcha.getType());
    }

    @Test
    public void testAnswerIsPositiveInteger() {
        // 生成100次验证码,确保答案都是正整数
        for (int i = 0; i < 100; i++) {
            Captcha captcha = CaptchaFactory.create(CaptchaType.ARITHMETIC);
            String answer = captcha.getAnswer();

            // 验证答案是数字
            assertTrue("答案应为数字", answer.matches("^\\d+$"));

            // 验证答案是正整数
            int result = Integer.parseInt(answer);
            assertTrue("答案应为正整数: " + result, result > 0);
        }
    }

    @Test
    public void testAddSubtractOnly() {
        CaptchaConfig config = CaptchaConfig.builder()
                .operatorType("ADD_SUBTRACT")
                .build();

        // 生成多次验证,虽然无法直接验证运算符,但可以确保正常生成
        for (int i = 0; i < 50; i++) {
            Captcha captcha = CaptchaFactory.create(CaptchaType.ARITHMETIC, config);
            assertNotNull("验证码应正常生成", captcha);
            assertTrue("答案应为正整数", Integer.parseInt(captcha.getAnswer()) > 0);
        }
    }

    @Test
    public void testAllOperators() {
        CaptchaConfig config = CaptchaConfig.builder()
                .operatorType("ALL")
                .build();

        // 生成多次验证
        for (int i = 0; i < 50; i++) {
            Captcha captcha = CaptchaFactory.create(CaptchaType.ARITHMETIC, config);
            assertNotNull("验证码应正常生成", captcha);
            assertTrue("答案应为正整数", Integer.parseInt(captcha.getAnswer()) > 0);
        }
    }

    @Test
    public void testVerify() {
        Captcha captcha = CaptchaFactory.create(CaptchaType.ARITHMETIC);
        String correctAnswer = captcha.getAnswer();

        // 正确答案应验证通过
        assertTrue("正确答案应验证通过", captcha.verify(correctAnswer));

        // 错误答案应验证失败
        assertFalse("错误答案应验证失败", captcha.verify("9999"));
        assertFalse("空字符串应验证失败", captcha.verify(""));
        assertFalse("null应验证失败", captcha.verify(null));
    }

    @Test
    public void testImageSize() {
        CaptchaConfig config = CaptchaConfig.builder()
                .width(200)
                .height(60)
                .build();

        Captcha captcha = CaptchaFactory.create(CaptchaType.ARITHMETIC, config);

        assertEquals("图像宽度应为200", 200, captcha.getWidth());
        assertEquals("图像高度应为60", 60, captcha.getHeight());
    }

    @Test
    public void testSaveImage() throws IOException {
        Captcha captcha = CaptchaFactory.create(CaptchaType.ARITHMETIC);

        // 创建输出目录
        File outputDir = new File("target/test-output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // 保存图像
        File outputFile = new File(outputDir, "arithmetic-captcha-test.png");
        ImageIO.write(captcha.getImage(), "PNG", outputFile);

        assertTrue("图像文件应存在", outputFile.exists());
        System.out.println("验证码图像已保存到: " + outputFile.getAbsolutePath());
        System.out.println("答案: " + captcha.getAnswer());
    }
}
