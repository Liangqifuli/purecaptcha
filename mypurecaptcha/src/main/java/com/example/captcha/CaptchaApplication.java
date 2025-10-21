package com.example.captcha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * PureCaptcha 验证码测试项目
 * 支持5种验证码类型：
 * 1. 字符验证码（ALPHANUMERIC）
 * 2. 算术验证码（ARITHMETIC）
 * 3. 中文验证码（CHINESE）
 * 4. GIF动图验证码（ANIMATED_GIF）
 * 5. 滑动拼图验证码（SLIDER）
 *
 * @author PureCaptcha Team
 * @since 2025-10-20
 */
@SpringBootApplication
public class CaptchaApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaptchaApplication.class, args);
        System.out.println("\n" +
                "═══════════════════════════════════════════════════════════════\n" +
                "  🎉 MyPureCaptcha 启动成功！\n" +
                "═══════════════════════════════════════════════════════════════\n" +
                "  访问地址：http://localhost:8080\n" +
                "  测试页面：http://localhost:8080/index.html\n" +
                "\n" +
                "  API接口：\n" +
                "  ├─ 生成验证码：GET  /api/captcha/generate?type={验证码类型}\n" +
                "  ├─ 验证答案：  POST /api/captcha/verify\n" +
                "  └─ 调试信息：  POST /api/captcha/debug\n" +
                "\n" +
                "  支持的验证码类型：\n" +
                "  ├─ ALPHANUMERIC  (字符验证码)\n" +
                "  ├─ ARITHMETIC    (算术验证码)\n" +
                "  ├─ CHINESE       (中文验证码)\n" +
                "  ├─ ANIMATED_GIF  (GIF动图验证码)\n" +
                "  └─ SLIDER        (滑动拼图验证码)\n" +
                "═══════════════════════════════════════════════════════════════\n");
    }
}

