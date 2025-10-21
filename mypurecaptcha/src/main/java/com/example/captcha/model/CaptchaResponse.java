package com.example.captcha.model;

import java.io.Serializable;

/**
 * 验证码响应 Record
 * <p>
 * 使用Java 17的record特性，提供不可变的验证码响应数据结构
 * 
 * <h3>优势：</h3>
 * <ul>
 *   <li>自动生成构造函数、getter、equals、hashCode、toString</li>
 *   <li>不可变对象，线程安全</li>
 *   <li>代码简洁，减少样板代码</li>
 * </ul>
 *
 * @param success 操作是否成功
 * @param data 验证码数据（可以是String、Map等）
 * @param message 响应消息
 * @author PureCaptcha Team
 * @version 2.0.0
 * @since 2025-10-20 (Java 17)
 */
public record CaptchaResponse(
        boolean success,
        Object data,
        String message
) implements Serializable {
    
    /**
     * 创建成功响应
     * 
     * @param data 验证码数据
     * @param message 成功消息
     * @return CaptchaResponse对象
     */
    public static CaptchaResponse success(Object data, String message) {
        return new CaptchaResponse(true, data, message);
    }
    
    /**
     * 创建失败响应
     * 
     * @param message 失败消息
     * @return CaptchaResponse对象
     */
    public static CaptchaResponse failure(String message) {
        return new CaptchaResponse(false, null, message);
    }
    
    /**
     * 创建失败响应（带数据）
     * 
     * @param data 额外数据（如可用类型列表）
     * @param message 失败消息
     * @return CaptchaResponse对象
     */
    public static CaptchaResponse failure(Object data, String message) {
        return new CaptchaResponse(false, data, message);
    }
}

