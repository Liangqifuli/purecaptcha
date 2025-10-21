package com.example.captcha.controller;

import com.example.captcha.service.CaptchaService;
import io.github.purecaptcha.core.CaptchaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 验证码控制器
 * 
 * 提供REST API接口：
 * - 生成各类验证码
 * - 验证用户输入
 * - 调试接口
 * 
 * @author PureCaptcha
 */
@RestController
@RequestMapping("/api/captcha")
@CrossOrigin(origins = "*") // 允许跨域（测试用，生产环境需要限制）
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    /**
     * 生成滑动验证码（推荐）
     * 
     * GET /api/captcha/slider
     * 
     * 响应：
     * {
     *   "success": true,
     *   "data": {
     *     "backgroundImage": "data:image/png;base64,...",
     *     "sliderImage": "data:image/png;base64,...",
     *     "sliderY": 80
     *   }
     * }
     */
    @GetMapping("/slider")
    public ResponseEntity<Map<String, Object>> generateSlider(HttpSession session) {
        try {
            String sessionId = session.getId();
            
            // 生成滑动验证码（350x200，使用内置背景）
            Object captchaData = captchaService.generateSliderCaptcha(sessionId, 350, 200, true);
            
            return success(captchaData);
            
        } catch (IOException e) {
            e.printStackTrace();
            return error("生成验证码失败: " + e.getMessage());
        }
    }

    /**
     * 生成滑动验证码（自定义尺寸）
     * 
     * GET /api/captcha/slider/custom?width=400&height=250
     */
    @GetMapping("/slider/custom")
    public ResponseEntity<Map<String, Object>> generateSliderCustom(
            HttpSession session,
            @RequestParam(defaultValue = "350") int width,
            @RequestParam(defaultValue = "200") int height,
            @RequestParam(defaultValue = "true") boolean useBuiltinBg) {
        try {
            String sessionId = session.getId();
            Object captchaData = captchaService.generateSliderCaptcha(sessionId, width, height, useBuiltinBg);
            return success(captchaData);
        } catch (IOException e) {
            return error("生成验证码失败: " + e.getMessage());
        }
    }

    /**
     * 生成字符验证码
     * 
     * GET /api/captcha/alphanumeric
     * 
     * 响应：
     * {
     *   "success": true,
     *   "data": "data:image/png;base64,..."
     * }
     */
    @GetMapping("/alphanumeric")
    public ResponseEntity<Map<String, Object>> generateAlphanumeric(HttpSession session) {
        try {
            String sessionId = session.getId();
            Object captchaData = captchaService.generateCaptcha(sessionId, CaptchaType.ALPHANUMERIC);
            return success(captchaData);
        } catch (IOException e) {
            return error("生成验证码失败: " + e.getMessage());
        }
    }

    /**
     * 生成算术验证码
     * 
     * GET /api/captcha/arithmetic
     */
    @GetMapping("/arithmetic")
    public ResponseEntity<Map<String, Object>> generateArithmetic(HttpSession session) {
        try {
            String sessionId = session.getId();
            Object captchaData = captchaService.generateCaptcha(sessionId, CaptchaType.ARITHMETIC);
            return success(captchaData);
        } catch (IOException e) {
            return error("生成验证码失败: " + e.getMessage());
        }
    }

    /**
     * 生成中文验证码
     * 
     * GET /api/captcha/chinese
     */
    @GetMapping("/chinese")
    public ResponseEntity<Map<String, Object>> generateChinese(HttpSession session) {
        try {
            String sessionId = session.getId();
            Object captchaData = captchaService.generateCaptcha(sessionId, CaptchaType.CHINESE);
            return success(captchaData);
        } catch (IOException e) {
            return error("生成验证码失败: " + e.getMessage());
        }
    }

    /**
     * 生成GIF动画验证码
     * 
     * GET /api/captcha/gif
     */
    @GetMapping("/gif")
    public ResponseEntity<Map<String, Object>> generateGif(HttpSession session) {
        try {
            String sessionId = session.getId();
            Object captchaData = captchaService.generateCaptcha(sessionId, CaptchaType.ANIMATED_GIF);
            return success(captchaData);
        } catch (IOException e) {
            return error("生成验证码失败: " + e.getMessage());
        }
    }

    /**
     * 验证用户输入（通用接口）
     * 
     * POST /api/captcha/verify
     * Body: { "answer": "150" }  (对于滑动验证码，answer是X坐标)
     * 
     * 响应：
     * {
     *   "success": true/false,
     *   "message": "验证通过" / "验证失败，请重试"
     * }
     */
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verify(
            HttpSession session,
            @RequestBody Map<String, String> request) {
        
        String sessionId = session.getId();
        String userAnswer = request.get("answer");
        
        if (userAnswer == null || userAnswer.trim().isEmpty()) {
            return error("答案不能为空");
        }
        
        boolean passed = captchaService.verifyCaptcha(sessionId, userAnswer);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", passed);
        response.put("message", passed ? "验证通过！" : "验证失败，请重试");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 验证滑动位置（简化接口）
     * 
     * POST /api/captcha/verify/slider?x=150
     */
    @PostMapping("/verify/slider")
    public ResponseEntity<Map<String, Object>> verifySlider(
            HttpSession session,
            @RequestParam("x") String userX) {
        
        String sessionId = session.getId();
        boolean passed = captchaService.verifyCaptcha(sessionId, userX);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", passed);
        response.put("message", passed ? "验证通过！" : "验证失败，请重试");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取详细验证信息（调试接口）
     * 
     * POST /api/captcha/debug
     * Body: { "answer": "150" }
     * 
     * 响应：详细的验证报告（包含相似度等信息）
     */
    @PostMapping("/debug")
    public ResponseEntity<Map<String, Object>> debug(
            HttpSession session,
            @RequestBody Map<String, String> request) {
        
        String sessionId = session.getId();
        String userAnswer = request.get("answer");
        
        if (userAnswer == null || userAnswer.trim().isEmpty()) {
            return error("答案不能为空");
        }
        
        String details = captchaService.getVerifyDetails(sessionId, userAnswer);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("details", details);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取验证码数量（监控接口）
     * 
     * GET /api/captcha/count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getCount() {
        int count = captchaService.getCaptchaCount();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", count);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 清理所有验证码（测试接口）
     * 
     * DELETE /api/captcha/clear
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clear() {
        captchaService.clearAll();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "已清理所有验证码");
        
        return ResponseEntity.ok(response);
    }

    // ==================== 辅助方法 ====================

    /**
     * 成功响应
     */
    private ResponseEntity<Map<String, Object>> success(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    /**
     * 错误响应
     */
    private ResponseEntity<Map<String, Object>> error(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

