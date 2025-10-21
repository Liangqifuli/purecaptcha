import io.github.purecaptcha.CaptchaFactory;
import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.Captcha;
import io.github.purecaptcha.core.CaptchaType;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 模拟mypurecaptcha项目的完整验证流程
 * 测试是否存在验证失败的bug
 */
public class TestMypurecaptchaIntegration {
    
    // 模拟Session存储
    private static Map<String, CaptchaWrapper> captchaStore = new HashMap<>();
    
    public static void main(String[] args) throws IOException {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║      模拟 mypurecaptcha 项目 - 完整验证流程测试               ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        // 创建输出目录
        File outputDir = new File("examples/integration-test-output");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // 测试20次算术验证码生成和验证
        int successCount = 0;
        int failCount = 0;
        
        for (int i = 1; i <= 20; i++) {
            String sessionId = "TEST_SESSION_" + i;
            
            // ========== 1. 模拟生成验证码 ==========
            CaptchaConfig config = CaptchaConfig.builder()
                    .width(200)
                    .height(80)
                    .operatorType("ALL")
                    .build();
            
            Captcha captcha = CaptchaFactory.create(CaptchaType.ARITHMETIC, config);
            String correctAnswer = captcha.getAnswer();
            
            // 保存到存储（模拟Session）
            captchaStore.put(sessionId, new CaptchaWrapper(captcha, CaptchaType.ARITHMETIC));
            
            // 保存图片
            File outputFile = new File(outputDir, String.format("arithmetic_%02d.png", i));
            ImageIO.write(captcha.getImage(), "png", outputFile);
            
            System.out.println("📝 [" + i + "] 生成验证码 - SessionID: " + sessionId);
            System.out.println("  └─ 正确答案: " + correctAnswer);
            
            // ========== 2. 模拟用户输入答案 ==========
            String userAnswer = correctAnswer; // 用户输入正确答案
            
            // ========== 3. 模拟验证流程 ==========
            boolean passed = verifyCaptcha(sessionId, userAnswer);
            
            if (passed) {
                successCount++;
                System.out.println("  ✅ 验证通过\n");
            } else {
                failCount++;
                System.out.println("  ❌ 验证失败 ⚠️  BUG发现！\n");
            }
        }

        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                       测试总结                                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.printf("\n总计: %d | 成功: %d | 失败: %d\n", 20, successCount, failCount);
        
        if (failCount > 0) {
            System.out.println("\n⚠️  发现验证BUG！有" + failCount + "次验证失败！");
        } else {
            System.out.println("\n✅ 所有验证均通过！验证逻辑正常！");
        }
    }
    
    /**
     * 模拟CaptchaService的verifyCaptcha方法
     */
    private static boolean verifyCaptcha(String sessionId, String userAnswer) {
        CaptchaWrapper wrapper = captchaStore.get(sessionId);
        if (wrapper == null) {
            System.out.println("❌ 验证失败: SessionId " + sessionId + " 对应的验证码不存在或已过期。");
            return false;
        }

        Captcha captcha = wrapper.captcha;
        CaptchaType type = wrapper.type;
        if (captcha == null) {
            System.out.println("❌ 验证失败: SessionId " + sessionId + " 对应的Captcha对象为空。");
            return false;
        }

        // 其他类型验证码（大小写不敏感）
        String correctAnswer = captcha.getAnswer();
        boolean passed = correctAnswer.equalsIgnoreCase(userAnswer);
        
        System.out.println("🎯 " + type + " 验证 - SessionID: " + sessionId);
        System.out.println("  ├─ 正确答案: " + correctAnswer);
        System.out.println("  ├─ 用户输入: " + userAnswer);
        System.out.println("  ├─ 答案长度: correctAnswer=" + correctAnswer.length() + ", userAnswer=" + userAnswer.length());
        System.out.println("  ├─ 答案字节: correctAnswer=" + bytesToHex(correctAnswer.getBytes()) + 
                           ", userAnswer=" + bytesToHex(userAnswer.getBytes()));
        System.out.println("  ├─ equals比较: " + correctAnswer.equals(userAnswer));
        System.out.println("  ├─ equalsIgnoreCase比较: " + correctAnswer.equalsIgnoreCase(userAnswer));
        System.out.println("  └─ 最终结果: " + (passed ? "通过 ✅" : "失败 ❌"));
        
        // 验证后删除，防止重复使用（模拟真实流程）
        captchaStore.remove(sessionId);
        
        return passed;
    }
    
    /**
     * 辅助方法：将字节数组转换为十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
    
    /**
     * 模拟CaptchaWrapper
     */
    private static class CaptchaWrapper {
        Captcha captcha;
        CaptchaType type;
        long createTime;
        
        CaptchaWrapper(Captcha captcha, CaptchaType type) {
            this.captcha = captcha;
            this.type = type;
            this.createTime = System.currentTimeMillis();
        }
    }
}

