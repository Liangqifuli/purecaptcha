package io.github.purecaptcha.model;

import io.github.purecaptcha.core.Captcha;
import io.github.purecaptcha.core.CaptchaType;

import java.awt.image.BufferedImage;

/**
 * 验证码基础实现类
 * <p>
 * 提供验证码接口的通用实现
 *
 * @author PureCaptcha
 * @version 1.0.0
 */
public class CaptchaResult implements Captcha {

    private final CaptchaType type;
    private final BufferedImage image;
    private final byte[] imageData;
    private final String answer;
    private final int width;
    private final int height;
    private final boolean caseSensitive;

    /**
     * 构造静态图片验证码
     *
     * @param type 验证码类型
     * @param image 图像对象
     * @param answer 正确答案
     * @param caseSensitive 是否区分大小写
     */
    public CaptchaResult(CaptchaType type, BufferedImage image, String answer, boolean caseSensitive) {
        this.type = type;
        this.image = image;
        this.imageData = null;
        this.answer = answer;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.caseSensitive = caseSensitive;
    }

    /**
     * 构造动态GIF验证码
     *
     * @param type 验证码类型
     * @param imageData GIF数据
     * @param answer 正确答案
     * @param width 宽度
     * @param height 高度
     * @param caseSensitive 是否区分大小写
     */
    public CaptchaResult(CaptchaType type, byte[] imageData, String answer,
                         int width, int height, boolean caseSensitive) {
        this.type = type;
        this.image = null;
        this.imageData = imageData;
        this.answer = answer;
        this.width = width;
        this.height = height;
        this.caseSensitive = caseSensitive;
    }

    @Override
    public CaptchaType getType() {
        return type;
    }

    @Override
    public BufferedImage getImage() {
        return image;
    }

    @Override
    public byte[] getImageData() {
        return imageData;
    }

    @Override
    public String getAnswer() {
        return answer;
    }

    @Override
    public boolean verify(String userInput) {
        if (userInput == null || answer == null) {
            return false;
        }

        if (caseSensitive) {
            return answer.equals(userInput);
        } else {
            return answer.equalsIgnoreCase(userInput);
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
