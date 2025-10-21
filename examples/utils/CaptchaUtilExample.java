package utils;

import java.util.Map;
import java.util.Scanner;

/**
 * CaptchaUtil 工具类使用示例
 * 
 * 演示如何使用 CaptchaUtil 快速生成和验证各种类型的验证码
 * 
 * @author PureCaptcha
 */
public class CaptchaUtilExample {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("╔═══════════════════════════════════════════════════╗");
        System.out.println("║                                                   ║");
        System.out.println("║          CaptchaUtil 工具类使用示例               ║");
        System.out.println("║                                                   ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
        System.out.println();
        
        while (true) {
            System.out.println("请选择验证码类型：");
            System.out.println("  1. 字符验证码 (ALPHANUMERIC)");
            System.out.println("  2. 算术验证码 (ARITHMETIC)");
            System.out.println("  3. 算术验证码-加减乘除 (ARITHMETIC-ALL)");
            System.out.println("  4. 中文验证码 (CHINESE)");
            System.out.println("  5. GIF动图验证码 (ANIMATED_GIF)");
            System.out.println("  6. 滑动拼图验证码 (SLIDER)");
            System.out.println("  7. 查看验证码数量");
            System.out.println("  8. 清理过期验证码");
            System.out.println("  0. 退出");
            System.out.print("请输入选项 (0-8): ");
            
            String choice = scanner.nextLine().trim();
            System.out.println();
            
            switch (choice) {
                case "1":
                    testAlphanumeric(scanner);
                    break;
                case "2":
                    testArithmetic(scanner, false);
                    break;
                case "3":
                    testArithmetic(scanner, true);
                    break;
                case "4":
                    testChinese(scanner);
                    break;
                case "5":
                    testGif(scanner);
                    break;
                case "6":
                    testSlider(scanner);
                    break;
                case "7":
                    showStats();
                    break;
                case "8":
                    cleanExpired();
                    break;
                case "0":
                    System.out.println("感谢使用！再见！");
                    scanner.close();
                    System.exit(0);
                    return;
                default:
                    System.out.println("❌ 无效的选项，请重新输入\n");
            }
        }
    }
    
    /**
     * 测试字符验证码
     */
    private static void testAlphanumeric(Scanner scanner) {
        System.out.println("═══════════════════════════════════════");
        System.out.println("  字符验证码测试");
        System.out.println("═══════════════════════════════════════");
        
        // 生成验证码
        Map<String, Object> result = CaptchaUtil.alphanumeric();
        
        String captchaId = (String) result.get("captchaId");
        String answer = (String) result.get("answer");
        
        System.out.println("✓ 验证码已生成");
        System.out.println("  ID: " + captchaId);
        System.out.println("  答案: " + answer + " (开发调试)");
        System.out.println("  Base64图片长度: " + ((String) result.get("imageBase64")).length());
        System.out.println();
        
        // 验证
        System.out.print("请输入验证码答案: ");
        String userAnswer = scanner.nextLine().trim();
        
        boolean isValid = CaptchaUtil.verify(captchaId, userAnswer);
        
        if (isValid) {
            System.out.println("✓ 验证成功！");
        } else {
            System.out.println("✗ 验证失败！");
        }
        System.out.println();
    }
    
    /**
     * 测试算术验证码
     */
    private static void testArithmetic(Scanner scanner, boolean useAll) {
        System.out.println("═══════════════════════════════════════");
        System.out.println("  算术验证码测试" + (useAll ? "（加减乘除）" : "（加减）"));
        System.out.println("═══════════════════════════════════════");
        
        // 生成验证码
        Map<String, Object> result = useAll 
            ? CaptchaUtil.arithmetic(true)
            : CaptchaUtil.arithmetic();
        
        String captchaId = (String) result.get("captchaId");
        String answer = (String) result.get("answer");
        
        System.out.println("✓ 验证码已生成");
        System.out.println("  ID: " + captchaId);
        System.out.println("  答案: " + answer + " (开发调试)");
        System.out.println();
        
        // 验证
        System.out.print("请输入计算结果: ");
        String userAnswer = scanner.nextLine().trim();
        
        boolean isValid = CaptchaUtil.verify(captchaId, userAnswer);
        
        if (isValid) {
            System.out.println("✓ 验证成功！");
        } else {
            System.out.println("✗ 验证失败！正确答案是: " + answer);
        }
        System.out.println();
    }
    
    /**
     * 测试中文验证码
     */
    private static void testChinese(Scanner scanner) {
        System.out.println("═══════════════════════════════════════");
        System.out.println("  中文验证码测试");
        System.out.println("═══════════════════════════════════════");
        
        // 生成验证码
        Map<String, Object> result = CaptchaUtil.chinese();
        
        String captchaId = (String) result.get("captchaId");
        String answer = (String) result.get("answer");
        
        System.out.println("✓ 验证码已生成");
        System.out.println("  ID: " + captchaId);
        System.out.println("  答案: " + answer + " (开发调试)");
        System.out.println();
        
        // 验证
        System.out.print("请输入验证码答案: ");
        String userAnswer = scanner.nextLine().trim();
        
        boolean isValid = CaptchaUtil.verify(captchaId, userAnswer);
        
        if (isValid) {
            System.out.println("✓ 验证成功！");
        } else {
            System.out.println("✗ 验证失败！");
        }
        System.out.println();
    }
    
    /**
     * 测试GIF动图验证码
     */
    private static void testGif(Scanner scanner) {
        System.out.println("═══════════════════════════════════════");
        System.out.println("  GIF动图验证码测试");
        System.out.println("═══════════════════════════════════════");
        
        // 生成验证码
        Map<String, Object> result = CaptchaUtil.gif();
        
        String captchaId = (String) result.get("captchaId");
        String answer = (String) result.get("answer");
        
        System.out.println("✓ 验证码已生成");
        System.out.println("  ID: " + captchaId);
        System.out.println("  答案: " + answer + " (开发调试)");
        System.out.println("  GIF图片长度: " + ((String) result.get("imageBase64")).length());
        System.out.println();
        
        // 验证
        System.out.print("请输入验证码答案: ");
        String userAnswer = scanner.nextLine().trim();
        
        boolean isValid = CaptchaUtil.verify(captchaId, userAnswer);
        
        if (isValid) {
            System.out.println("✓ 验证成功！");
        } else {
            System.out.println("✗ 验证失败！");
        }
        System.out.println();
    }
    
    /**
     * 测试滑动拼图验证码
     */
    private static void testSlider(Scanner scanner) {
        System.out.println("═══════════════════════════════════════");
        System.out.println("  滑动拼图验证码测试");
        System.out.println("═══════════════════════════════════════");
        
        // 生成验证码
        Map<String, Object> result = CaptchaUtil.slider();
        
        String captchaId = (String) result.get("captchaId");
        Integer sliderY = (Integer) result.get("sliderY");
        
        System.out.println("✓ 验证码已生成");
        System.out.println("  ID: " + captchaId);
        System.out.println("  滑块Y坐标: " + sliderY);
        System.out.println("  背景图片长度: " + ((String) result.get("imageBase64")).length());
        System.out.println("  滑块图片长度: " + ((String) result.get("sliderImageBase64")).length());
        System.out.println();
        
        // 获取调试信息
        Map<String, Object> debugInfo = CaptchaUtil.getSliderDebugInfo(captchaId);
        Integer correctX = (Integer) debugInfo.get("correctPosition");
        Integer tolerance = (Integer) debugInfo.get("tolerance");
        
        System.out.println("调试信息：");
        System.out.println("  正确X坐标: " + correctX);
        System.out.println("  容差范围: ±" + tolerance + "px");
        System.out.println("  可接受范围: " + (correctX - tolerance) + " ~ " + (correctX + tolerance));
        System.out.println();
        
        // 验证
        System.out.print("请输入滑动的X坐标 (提示：" + correctX + "): ");
        String input = scanner.nextLine().trim();
        
        try {
            int userX = Integer.parseInt(input);
            boolean isValid = CaptchaUtil.verifySlider(captchaId, userX);
            
            if (isValid) {
                System.out.println("✓ 验证成功！");
            } else {
                System.out.println("✗ 验证失败！");
                System.out.println("  您的位置: " + userX);
                System.out.println("  正确位置: " + correctX);
                System.out.println("  偏差: " + Math.abs(userX - correctX) + "px");
            }
        } catch (NumberFormatException e) {
            System.out.println("✗ 无效的输入！");
        }
        System.out.println();
    }
    
    /**
     * 显示统计信息
     */
    private static void showStats() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("  验证码统计信息");
        System.out.println("═══════════════════════════════════════");
        System.out.println("  当前验证码数量: " + CaptchaUtil.getCount());
        System.out.println("  过期时间: 5分钟");
        System.out.println();
    }
    
    /**
     * 清理过期验证码
     */
    private static void cleanExpired() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("  清理过期验证码");
        System.out.println("═══════════════════════════════════════");
        int cleaned = CaptchaUtil.cleanExpired();
        System.out.println("✓ 清理了 " + cleaned + " 个过期验证码");
        System.out.println("  剩余验证码: " + CaptchaUtil.getCount());
        System.out.println();
    }
}

