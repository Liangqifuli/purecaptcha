package com.example.captcha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 启动类
 * 
 * @author PureCaptcha
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("\n========================================");
        System.out.println("  验证码服务启动成功！");
        System.out.println("  测试地址: http://localhost:8080");
        System.out.println("  API文档: /api/captcha/slider");
        System.out.println("========================================\n");
    }
}

