package io.github.purecaptcha.generator;

import io.github.purecaptcha.config.CaptchaConfig;
import io.github.purecaptcha.core.Captcha;
import io.github.purecaptcha.core.CaptchaGenerator;
import io.github.purecaptcha.core.CaptchaType;
import io.github.purecaptcha.model.CaptchaResult;
import io.github.purecaptcha.util.ColorUtil;
import io.github.purecaptcha.util.ImageUtil;
import io.github.purecaptcha.util.RandomUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 算术验证码生成器
 * <p>
 * 生成简单的四则运算表达式,确保结果为正整数
 *
 * @author PureCaptcha
 * @version 1.0.0
 */
public class ArithmeticCaptchaGenerator implements CaptchaGenerator {

    // 运算符
    private static final char[] ADD_SUBTRACT_OPERATORS = {'+', '-'};
    private static final char[] ALL_OPERATORS = {'+', '-', '×', '÷'};

    @Override
    public Captcha generate(CaptchaConfig config) {
        // 生成算术表达式和答案
        ArithmeticExpression expression = generateExpression(config);

        // 创建图像
        BufferedImage image = ImageUtil.createImage(config.getWidth(), config.getHeight());
        Graphics2D g2d = ImageUtil.getGraphics(image);

        // 绘制背景
        ImageUtil.drawBackground(g2d, config.getWidth(), config.getHeight(),
                                config.getBackgroundColor());

        // 绘制装饰性圆圈
        ImageUtil.drawDecorativeCircles(g2d, config.getWidth(), config.getHeight(), 8);

        // 绘制现代化干扰线
        ImageUtil.drawModernInterferenceLines(g2d, config.getWidth(), config.getHeight(),
                                             config.getInterferenceLineCount());

        // 绘制风格化表达式
        drawStyledExpression(g2d, expression.text, config);

        // 绘制少量噪点
        ImageUtil.drawNoisePoints(g2d, config.getWidth(), config.getHeight(),
                                 config.getNoisePointCount() / 3);

        g2d.dispose();

        return new CaptchaResult(CaptchaType.ARITHMETIC, image,
                               String.valueOf(expression.answer), true);
    }

    @Override
    public Captcha generate() {
        return generate(CaptchaConfig.defaultConfig());
    }

    @Override
    public CaptchaType getSupportedType() {
        return CaptchaType.ARITHMETIC;
    }

    /**
     * 生成算术表达式
     *
     * @param config 配置
     * @return 表达式对象
     */
    private ArithmeticExpression generateExpression(CaptchaConfig config) {
        char[] operators = "ALL".equals(config.getOperatorType())
                ? ALL_OPERATORS
                : ADD_SUBTRACT_OPERATORS;

        // 最多尝试100次生成有效表达式
        for (int i = 0; i < 100; i++) {
            char operator = operators[RandomUtil.randomInt(operators.length)];
            int a, b, result;

            switch (operator) {
                case '+':
                    a = RandomUtil.randomInt(1, 20);
                    b = RandomUtil.randomInt(1, 20);
                    result = a + b;
                    if (result > 0) {
                        return new ArithmeticExpression(a + " + " + b + " = ?", result);
                    }
                    break;

                case '-':
                    // 确保结果为正数
                    a = RandomUtil.randomInt(2, 20);
                    b = RandomUtil.randomInt(1, a - 1);
                    result = a - b;
                    if (result > 0) {
                        return new ArithmeticExpression(a + " - " + b + " = ?", result);
                    }
                    break;

                case '×':
                    a = RandomUtil.randomInt(1, 10);
                    b = RandomUtil.randomInt(1, 10);
                    result = a * b;
                    if (result > 0) {
                        return new ArithmeticExpression(a + " × " + b + " = ?", result);
                    }
                    break;

                case '÷':
                    // 确保整除
                    b = RandomUtil.randomInt(1, 10);
                    result = RandomUtil.randomInt(1, 10);
                    a = b * result;
                    if (result > 0) {
                        return new ArithmeticExpression(a + " ÷ " + b + " = ?", result);
                    }
                    break;
            }
        }

        // 如果无法生成有效表达式,返回默认加法
        return new ArithmeticExpression("1 + 1 = ?", 2);
    }

    /**
     * 绘制表达式
     *
     * @param g2d Graphics2D对象
     * @param expression 表达式文本
     * @param config 配置
     */
    private void drawExpression(Graphics2D g2d, String expression, CaptchaConfig config) {
        int width = config.getWidth();
        int height = config.getHeight();

        // 设置字体
        g2d.setFont(config.getFont());

        // 获取字体度量信息
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(expression);
        int textHeight = fm.getHeight();

        // 计算居中位置
        int x = (width - textWidth) / 2;
        int y = (height + textHeight) / 2 - fm.getDescent();

        // 设置颜色
        Color color = config.getFontColor() != null
                ? config.getFontColor()
                : ColorUtil.randomDarkColor();
        g2d.setColor(color);

        // 添加轻微的随机偏移和旋转
        x += RandomUtil.randomInt(-10, 10);
        y += RandomUtil.randomInt(-5, 5);
        double angle = Math.toRadians(RandomUtil.randomInt(-5, 5));

        // 绘制表达式
        ImageUtil.drawRotatedText(g2d, expression, x, y, angle);
    }

    /**
     * 绘制风格化表达式（数字作为整体，十位数紧密连接）
     *
     * @param g2d Graphics2D对象
     * @param expression 表达式文本
     * @param config 配置
     */
    private void drawStyledExpression(Graphics2D g2d, String expression, CaptchaConfig config) {
        int width = config.getWidth();
        int height = config.getHeight();

        // 使用清晰的字体，字号稍微减小
        Font styledFont = new Font("Arial", Font.BOLD, 
                                   Math.max(24, config.getFont().getSize() - 4));
        g2d.setFont(styledFont);

        // 获取字体度量信息
        FontMetrics fm = g2d.getFontMetrics(styledFont);
        int ascent = fm.getAscent();
        
        // 将表达式分割为token（数字、运算符、空格等）
        java.util.List<String> tokens = tokenizeExpression(expression);
        
        // 计算总宽度（token间距只在非空格token之间）
        int tokenSpacing = 12; // token之间的间距
        int totalWidth = 0;
        for (String token : tokens) {
            if (!token.equals(" ")) {
                totalWidth += fm.stringWidth(token);
            }
        }
        // 添加token之间的间距（去除空格token）
        int nonSpaceTokenCount = 0;
        for (String token : tokens) {
            if (!token.equals(" ")) {
                nonSpaceTokenCount++;
            }
        }
        if (nonSpaceTokenCount > 1) {
            totalWidth += (nonSpaceTokenCount - 1) * tokenSpacing;
        }

        // 计算起始位置（水平居中）
        int startX = (width - totalWidth) / 2;
        
        // 垂直居中位置
        int baseY = (height + ascent) / 2;

        int currentX = startX;
        for (String token : tokens) {
            // 跳过空格
            if (token.equals(" ")) {
                continue;
            }
            
            // 为整个token选择颜色
            Color tokenColor;
            if (token.chars().allMatch(Character::isDigit)) {
                // 数字使用鲜艳颜色
                tokenColor = config.getFontColor() != null
                        ? config.getFontColor()
                        : ColorUtil.randomVibrantColor();
            } else {
                // 运算符和其他符号使用鲜艳颜色
                tokenColor = ColorUtil.randomVibrantColor();
            }

            // 整个token轻微的随机旋转和偏移
            double tokenAngle = Math.toRadians(RandomUtil.randomInt(-8, 8));
            int tokenY = baseY + RandomUtil.randomInt(-2, 2);

            // 绘制整个token（数字内部字符紧密连接）
            ImageUtil.drawHollowText(g2d, token, currentX, tokenY, tokenAngle, tokenColor);

            // 移动到下一个token位置
            currentX += fm.stringWidth(token) + tokenSpacing;
        }
    }
    
    /**
     * 将表达式分割为token（数字、运算符等）
     * 例如: "10 + 13 = ?" -> ["10", " ", "+", " ", "13", " ", "=", " ", "?"]
     * 
     * @param expression 表达式
     * @return token列表
     */
    private java.util.List<String> tokenizeExpression(String expression) {
        java.util.List<String> tokens = new java.util.ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        
        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            
            if (Character.isDigit(ch)) {
                // 数字：继续累积到当前token
                currentToken.append(ch);
            } else {
                // 非数字：保存之前的数字token（如果有）
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken = new StringBuilder();
                }
                // 添加当前字符作为独立token
                tokens.add(String.valueOf(ch));
            }
        }
        
        // 添加最后一个token（如果有）
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }
        
        return tokens;
    }

    /**
     * 算术表达式内部类
     */
    private static class ArithmeticExpression {
        String text;
        int answer;

        ArithmeticExpression(String text, int answer) {
            this.text = text;
            this.answer = answer;
        }
    }
}
