package com.example.captcha.controller;

import com.example.captcha.service.CaptchaService;
import io.github.purecaptcha.core.CaptchaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import com.example.captcha.model.CaptchaResponse;
import com.example.captcha.model.VerifyRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 验证码控制器
 * <p>
 * 提供RESTful API接口，用于验证码的生成、验证和调试
 * 
 * <h3>API接口列表</h3>
 * <ul>
 *   <li><b>生成验证码</b>：
 *     <ul>
 *       <li>GET /api/captcha/generate?type={type} - 通用生成接口</li>
 *       <li>GET /api/captcha/alphanumeric - 字符验证码</li>
 *       <li>GET /api/captcha/arithmetic - 算术验证码</li>
 *       <li>GET /api/captcha/chinese - 中文验证码</li>
 *       <li>GET /api/captcha/gif - GIF验证码</li>
 *       <li>GET /api/captcha/slider - 滑动拼图验证码</li>
 *     </ul>
 *   </li>
 *   <li><b>验证接口</b>：
 *     <ul>
 *       <li>POST /api/captcha/verify - 验证用户输入</li>
 *       <li>POST /api/captcha/debug - 获取验证详情（调试用）</li>
 *     </ul>
 *   </li>
 *   <li><b>监控接口</b>：
 *     <ul>
 *       <li>GET /api/captcha/health - 健康检查</li>
 *       <li>GET /api/captcha/stats - 统计信息</li>
 *     </ul>
 *   </li>
 * </ul>
 * 
 * <h3>跨域配置</h3>
 * <p>
 * 当前配置允许所有域名跨域访问（{@code @CrossOrigin(origins = "*")}）
 * <br><b>生产环境建议：</b>配置具体的域名，例如：
 * <pre>
 * {@code @CrossOrigin(origins = {"https://example.com", "https://api.example.com"})}
 * </pre>
 * 
 * <h3>使用示例</h3>
 * <pre>
 * // 前端调用示例（JavaScript）
 * 
 * // 1. 生成验证码
 * const response = await fetch('/api/captcha/slider');
 * const result = await response.json();
 * console.log(result.data.backgroundImage); // Base64图片
 * 
 * // 2. 验证验证码
 * const verifyResponse = await fetch('/api/captcha/verify', {
 *     method: 'POST',
 *     headers: { 'Content-Type': 'application/json' },
 *     body: JSON.stringify({ userX: '254' })
 * });
 * const verifyResult = await verifyResponse.json();
 * console.log(verifyResult.success); // true/false
 * </pre>
 *
 * @author PureCaptcha Team
 * @version 1.0.0
 * @since 2025-10-20
 */
@RestController
@RequestMapping("/api/captcha")
@CrossOrigin(origins = "*") // 允许跨域请求（生产环境建议配置具体域名）
public class CaptchaController {

    /**
     * 验证码服务
     * <p>
     * 自动注入，处理验证码生成、验证等业务逻辑
     */
    @Autowired
    private CaptchaService captchaService;

    /**
     * 生成验证码接口（通用）
     * <p>
     * 根据type参数生成指定类型的验证码，支持所有5种验证码类型
     * 
     * <p><b>接口信息：</b>
     * <ul>
     *   <li><b>请求方式</b>：GET</li>
     *   <li><b>路径</b>：/api/captcha/generate</li>
     *   <li><b>参数</b>：type（可选，默认SLIDER）</li>
     * </ul>
     * 
     * <p><b>支持的type值：</b>
     * <ul>
     *   <li>ALPHANUMERIC - 字符验证码</li>
     *   <li>ARITHMETIC - 算术验证码</li>
     *   <li>CHINESE - 中文验证码</li>
     *   <li>ANIMATED_GIF - GIF动图验证码</li>
     *   <li>SLIDER - 滑动拼图验证码（默认）</li>
     * </ul>
     * 
     * <p><b>返回数据格式：</b>
     * <pre>
     * // 普通验证码（字符/算术/中文/GIF）
     * {
     *   "success": true,
     *   "data": {
     *     "image": "data:image/png;base64,...",
     *     "sessionId": "xxx",
     *     "type": "ALPHANUMERIC"
     *   },
     *   "message": "验证码生成成功"
     * }
     * 
     * // 滑动验证码
     * {
     *   "success": true,
     *   "data": {
     *     "backgroundImage": "data:image/png;base64,...",
     *     "sliderImage": "data:image/png;base64,...",
     *     "sliderY": 80,
     *     "sessionId": "xxx",
     *     "type": "SLIDER"
     *   },
     *   "message": "验证码生成成功"
     * }
     * </pre>
     * 
     * <p><b>使用示例：</b>
     * <pre>
     * // 生成字符验证码
     * GET /api/captcha/generate?type=ALPHANUMERIC
     * 
     * // 生成滑动验证码（默认）
     * GET /api/captcha/generate
     * </pre>
     *
     * @param session HttpSession 用于生成和存储验证码的会话标识
     * @param type 验证码类型，支持：ALPHANUMERIC, ARITHMETIC, CHINESE, ANIMATED_GIF, SLIDER（默认SLIDER）
     * @return ResponseEntity 包含验证码数据的响应对象
     *         <ul>
     *           <li>成功：200 OK，包含验证码图片的Base64数据</li>
     *           <li>参数错误：400 Bad Request，返回错误信息和可用类型</li>
     *           <li>服务器错误：500 Internal Server Error，返回错误信息</li>
     *         </ul>
     */
    @GetMapping("/generate")
    public ResponseEntity<CaptchaResponse> generateCaptcha(
            HttpSession session,
            @RequestParam(defaultValue = "SLIDER") String type) {
        try {
            CaptchaType captchaType = CaptchaType.valueOf(type.toUpperCase());
            Object captchaData = captchaService.generateCaptcha(session.getId(), captchaType);

            // 使用record简化响应
            var response = CaptchaResponse.success(captchaData, "验证码生成成功");
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            // 使用text block和Map.of简化代码
            var availableTypes = new String[]{
                "ALPHANUMERIC", "ARITHMETIC", "CHINESE", "ANIMATED_GIF", "SLIDER"
            };
            var errorData = Map.of("availableTypes", availableTypes);
            var response = CaptchaResponse.failure(errorData, "无效的验证码类型: " + type);
            return ResponseEntity.badRequest().body(response);
            
        } catch (IOException e) {
            e.printStackTrace();
            var response = CaptchaResponse.failure("生成验证码失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 专门为滑动验证码提供生成接口，方便前端调用
     *
     * @param session HttpSession 用于存储验证码答案
     * @return 滑动验证码的背景图和滑块图
     */
    @GetMapping("/slider")
    public ResponseEntity<CaptchaResponse> generateSliderCaptcha(HttpSession session) {
        return generateCaptchaByType(session, CaptchaType.SLIDER);
    }

    /**
     * 字符验证码生成接口
     */
    @GetMapping("/alphanumeric")
    public ResponseEntity<CaptchaResponse> generateAlphanumericCaptcha(HttpSession session) {
        return generateCaptchaByType(session, CaptchaType.ALPHANUMERIC);
    }

    /**
     * 算术验证码生成接口
     */
    @GetMapping("/arithmetic")
    public ResponseEntity<CaptchaResponse> generateArithmeticCaptcha(HttpSession session) {
        return generateCaptchaByType(session, CaptchaType.ARITHMETIC);
    }

    /**
     * 中文验证码生成接口
     */
    @GetMapping("/chinese")
    public ResponseEntity<CaptchaResponse> generateChineseCaptcha(HttpSession session) {
        return generateCaptchaByType(session, CaptchaType.CHINESE);
    }

    /**
     * GIF动图验证码生成接口
     */
    @GetMapping("/gif")
    public ResponseEntity<CaptchaResponse> generateGifCaptcha(HttpSession session) {
        return generateCaptchaByType(session, CaptchaType.ANIMATED_GIF);
    }

    /**
     * 通用生成方法（使用Java 17简化）
     */
    private ResponseEntity<CaptchaResponse> generateCaptchaByType(HttpSession session, CaptchaType type) {
        try {
            var captchaData = captchaService.generateCaptcha(session.getId(), type);
            var response = CaptchaResponse.success(captchaData, type.name() + " 验证码生成成功");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            var response = CaptchaResponse.failure("生成验证码失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 验证码验证接口
     * <p>
     * 验证用户输入的验证码答案是否正确
     * 
     * <p><b>接口信息：</b>
     * <ul>
     *   <li><b>请求方式</b>：POST</li>
     *   <li><b>路径</b>：/api/captcha/verify</li>
     *   <li><b>Content-Type</b>：application/json</li>
     * </ul>
     * 
     * <p><b>请求体格式：</b>
     * <pre>
     * // 字符/算术/中文/GIF验证码
     * {
     *   "answer": "ABC123"
     * }
     * 
     * // 滑动拼图验证码
     * {
     *   "userX": "254"
     * }
     * </pre>
     * 
     * <p><b>返回数据格式：</b>
     * <pre>
     * // 验证成功
     * {
     *   "success": true,
     *   "message": "验证通过 ✅"
     * }
     * 
     * // 验证失败
     * {
     *   "success": false,
     *   "message": "验证失败，请重试 ❌"
     * }
     * </pre>
     * 
     * <p><b>验证规则：</b>
     * <ul>
     *   <li><b>字符/算术/中文/GIF</b>：大小写不敏感的字符串比较</li>
     *   <li><b>滑动拼图</b>：智能图像相似度算法（88%阈值 + 12px容差）</li>
     * </ul>
     * 
     * <p><b>注意事项：</b>
     * <ul>
     *   <li>验证后验证码会被自动删除，不能重复验证</li>
     *   <li>验证码有效期5分钟，超时后自动失效</li>
     *   <li>必须使用生成验证码时的同一Session</li>
     * </ul>
     * 
     * <p><b>使用示例：</b>
     * <pre>
     * // JavaScript调用
     * const response = await fetch('/api/captcha/verify', {
     *     method: 'POST',
     *     headers: { 'Content-Type': 'application/json' },
     *     body: JSON.stringify({ answer: 'ABC123' }) // 或 { userX: '254' }
     * });
     * const result = await response.json();
     * console.log(result.success); // true/false
     * </pre>
     *
     * @param session HttpSession 用于获取之前存储的验证码答案
     * @param requestBody 包含用户答案的JSON对象
     *                    <ul>
     *                      <li>普通验证码：{"answer": "用户输入"}</li>
     *                      <li>滑动验证码：{"userX": "254"}</li>
     *                    </ul>
     * @return ResponseEntity 包含验证结果的响应对象
     *         <ul>
     *           <li>成功：200 OK，success=true</li>
     *           <li>失败：200 OK，success=false</li>
     *           <li>参数错误：400 Bad Request</li>
     *         </ul>
     */
    @PostMapping("/verify")
    public ResponseEntity<CaptchaResponse> verifyCaptcha(
            HttpSession session,
            @RequestBody VerifyRequest request) {
        
        // ⭐ 检查必需参数
        if (!request.hasValidAnswer()) {
            var response = CaptchaResponse.failure("缺少验证答案或滑动坐标");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (request.type() == null || request.type().isEmpty()) {
            var response = CaptchaResponse.failure("缺少验证码类型(type)参数");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            var sessionId = session.getId();
            var answerToVerify = request.getAnswerValue();
            var captchaType = CaptchaType.valueOf(request.type().toUpperCase());
            
            // ⭐ 使用type参数定位Redis中的验证码
            var passed = captchaService.verifyCaptcha(sessionId, captchaType, answerToVerify);

            // 使用text block优化消息
            var message = passed ? "验证通过 ✅" : "验证失败，请重试 ❌";
            var response = new CaptchaResponse(passed, null, message);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            var response = CaptchaResponse.failure("无效的验证码类型: " + request.type());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ⭐ Redis版本：调试方法已移除，使用Redis命令行工具查看验证码
    // 查看所有验证码key：redis-cli -p 16831 -a root@root KEYS "captcha:*"
    // 查看特定验证码：redis-cli -p 16831 -a root@root GET "captcha:{sessionId}:{type}"

    /**
     * 健康检查接口
     * <p>
     * 用于监控服务是否正常运行，返回服务状态和支持的验证码类型
     * 
     * <p><b>接口信息：</b>
     * <ul>
     *   <li><b>请求方式</b>：GET</li>
     *   <li><b>路径</b>：/api/captcha/health</li>
     *   <li><b>用途</b>：健康检查、服务监控</li>
     * </ul>
     * 
     * <p><b>返回数据格式：</b>
     * <pre>
     * {
     *   "status": "UP",
     *   "service": "CaptchaService",
     *   "supportedTypes": [
     *     "ALPHANUMERIC",
     *     "ARITHMETIC",
     *     "CHINESE",
     *     "ANIMATED_GIF",
     *     "SLIDER"
     *   ]
     * }
     * </pre>
     * 
     * <p><b>使用场景：</b>
     * <ul>
     *   <li>Kubernetes/Docker健康探针</li>
     *   <li>负载均衡器健康检查</li>
     *   <li>监控系统集成</li>
     * </ul>
     *
     * @return ResponseEntity 包含服务状态的响应对象（200 OK）
     */
    @GetMapping("/health")
    public ResponseEntity<CaptchaResponse> health() {
        var healthData = Map.of(
            "status", "UP",
            "service", "CaptchaService",
            "supportedTypes", new String[]{
                "ALPHANUMERIC", "ARITHMETIC", "CHINESE", "ANIMATED_GIF", "SLIDER"
            }
        );
        var response = CaptchaResponse.success(healthData, "服务正常运行");
        return ResponseEntity.ok(response);
    }
}

