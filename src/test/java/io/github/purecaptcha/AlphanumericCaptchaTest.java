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
 * 字符验证码测试
 *
 * @author PureCaptcha
 * @version 1.0.0
 */
public class AlphanumericCaptchaTest {

    @Test
    public void testGenerateDefaultCaptcha() {
        Captcha captcha = CaptchaFactory.create(CaptchaType.ALPHANUMERIC);

        assertNotNull("验证码对象不应为null", captcha);
        assertNotNull("图像不应为null", captcha.getImage());
        assertNotNull("答案不应为null", captcha.getAnswer());
        assertEquals("验证码类型应为ALPHANUMERIC", CaptchaType.ALPHANUMERIC, captcha.getType());
        assertEquals("默认答案长度应为5", 5, captcha.getAnswer().length());
    }

    @Test
    public void testCustomLength() {
        CaptchaConfig config = CaptchaConfig.builder()
                .charLength(8)
                .build();

        Captcha captcha = CaptchaFactory.create(CaptchaType.ALPHANUMERIC, config);

        assertEquals("答案长度应为8", 8, captcha.getAnswer().length());
    }

    @Test
    public void testNumericOnly() {
        CaptchaConfig config = CaptchaConfig.builder()
                .charType("NUMERIC")
                .charLength(6)
                .build();

        Captcha captcha = CaptchaFactory.create(CaptchaType.ALPHANUMERIC, config);

        // 验证答案只包含数字
        assertTrue("答案应只包含数字", captcha.getAnswer().matches("^[0-9]+$"));
    }

    @Test
    public void testExcludeConfusingChars() {
        CaptchaConfig config = CaptchaConfig.builder()
                .excludeConfusingChars(true)
                .charLength(50) // 生成多个字符以验证过滤效果
                .caseSensitive(true) // 区分大小写以便检查原始答案
                .build();

        // 多次测试以确保稳定性
        for (int i = 0; i < 10; i++) {
            Captcha captcha = CaptchaFactory.create(CaptchaType.ALPHANUMERIC, config);
            String answer = captcha.getAnswer();

            // 验证不包含易混淆字符: 0 O 1 l I 2 Z 5 S 8 B
            String confusingChars = "0O1lI2Z5S8B";
            for (char c : answer.toCharArray()) {
                assertFalse("答案不应包含易混淆字符: " + c, confusingChars.indexOf(c) >= 0);
            }
        }
    }

    @Test
    public void testCaseSensitive() {
        CaptchaConfig config = CaptchaConfig.builder()
                .caseSensitive(true)
                .build();

        Captcha captcha = CaptchaFactory.create(CaptchaType.ALPHANUMERIC, config);

        // 测试区分大小写
        String answer = captcha.getAnswer();
        assertTrue("区分大小写时应验证通过", captcha.verify(answer));

        // 如果答案包含大写字母,小写应验证失败
        if (!answer.equals(answer.toLowerCase())) {
            assertFalse("区分大小写时应验证失败", captcha.verify(answer.toLowerCase()));
        }
    }

    @Test
    public void testCaseInsensitive() {
        CaptchaConfig config = CaptchaConfig.builder()
                .caseSensitive(false)
                .build();

        Captcha captcha = CaptchaFactory.create(CaptchaType.ALPHANUMERIC, config);
        String answer = captcha.getAnswer();

        // 不区分大小写
        assertTrue("不区分大小写时应验证通过", captcha.verify(answer.toUpperCase()));
        assertTrue("不区分大小写时应验证通过", captcha.verify(answer.toLowerCase()));
    }

    @Test
    public void testImageSize() {
        CaptchaConfig config = CaptchaConfig.builder()
                .width(200)
                .height(60)
                .build();

        Captcha captcha = CaptchaFactory.create(CaptchaType.ALPHANUMERIC, config);

        assertEquals("图像宽度应为200", 200, captcha.getWidth());
        assertEquals("图像高度应为60", 60, captcha.getHeight());
    }

    @Test
    public void testSaveImage() throws IOException {
        Captcha captcha = CaptchaFactory.create(CaptchaType.ALPHANUMERIC);

        // 创建输出目录
        File outputDir = new File("target/test-output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // 保存图像
        File outputFile = new File(outputDir, "alphanumeric-captcha-test.png");
        ImageIO.write(captcha.getImage(), "PNG", outputFile);

        assertTrue("图像文件应存在", outputFile.exists());
        System.out.println("验证码图像已保存到: " + outputFile.getAbsolutePath());
        System.out.println("答案: " + captcha.getAnswer());
    }
}
