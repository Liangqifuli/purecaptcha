package io.github.purecaptcha.core;

import java.awt.image.BufferedImage;

/**
 * 验证码结果接口
 * <p>
 * 封装验证码图像和答案,支持静态图片和动态GIF
 *
 * @author PureCaptcha
 * @version 1.0.0
 */
public interface Captcha {

    /**
     * 获取验证码类型
     *
     * @return 验证码类型
     */
    CaptchaType getType();

    /**
     * 获取验证码图像(用于静态图片)
     *
     * @return BufferedImage对象,如果是GIF类型则返回null
     */
    BufferedImage getImage();

    /**
     * 获取验证码图像数据(用于GIF动画)
     *
     * @return 图像字节数组,如果是静态图片则返回null
     */
    byte[] getImageData();

    /**
     * 获取验证码答案
     *
     * @return 正确答案字符串
     */
    String getAnswer();

    /**
     * 验证用户输入
     *
     * @param userInput 用户输入的答案
     * @return true-验证通过, false-验证失败
     */
    boolean verify(String userInput);

    /**
     * 获取验证码宽度
     *
     * @return 宽度(像素)
     */
    int getWidth();

    /**
     * 获取验证码高度
     *
     * @return 高度(像素)
     */
    int getHeight();
}
