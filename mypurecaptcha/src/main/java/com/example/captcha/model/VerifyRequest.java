package com.example.captcha.model;

/**
 * 验证请求 Record（Redis版本）
 * <p>
 * 使用Java 17的record特性，封装验证请求数据
 * 
 * <h3>使用场景：</h3>
 * <ul>
 *   <li>字符/算术/中文/GIF验证码：使用answer字段 + type字段</li>
 *   <li>滑动拼图验证码：使用userX字段 + type字段</li>
 * </ul>
 * 
 * <h3>使用示例：</h3>
 * <pre>
 * // 前端JSON请求体
 * {
 *   "answer": "ABC123",
 *   "type": "ALPHANUMERIC"  // ⭐ 新增：必须指定验证码类型
 * }
 * 
 * // 或滑动验证码
 * {
 *   "userX": "254",
 *   "type": "SLIDER"
 * }
 * </pre>
 *
 * @param answer 用户输入的答案（字符/算术/中文/GIF验证码）
 * @param userX 滑动位置的X坐标（滑动验证码）
 * @param type 验证码类型（必需，用于Redis key定位）
 * @author PureCaptcha Team
 * @version 2.0.0 (Redis版本)
 * @since 2025-10-21
 */
public record VerifyRequest(
        String answer,
        String userX,
        String type  // ⭐ 新增：验证码类型
) {
    
    /**
     * 获取实际的验证答案
     * <p>
     * 优先使用userX（滑动验证码），如果为空则使用answer
     * 
     * @return 验证答案字符串
     */
    public String getAnswerValue() {
        return (userX != null && !userX.isEmpty()) ? userX : answer;
    }
    
    /**
     * 检查是否有有效的答案
     * 
     * @return true - 有有效答案，false - 无有效答案
     */
    public boolean hasValidAnswer() {
        String answerValue = getAnswerValue();
        return answerValue != null && !answerValue.isEmpty();
    }
}

