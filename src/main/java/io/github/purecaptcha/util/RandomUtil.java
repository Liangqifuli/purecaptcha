package io.github.purecaptcha.util;

import java.security.SecureRandom;
import java.util.Random;

/**
 * 随机数工具类
 * <p>
 * 基于SecureRandom实现,确保验证码的安全性
 *
 * @author PureCaptcha
 * @version 1.0.0
 */
public class RandomUtil {

    private static final Random RANDOM = new SecureRandom();

    /**
     * 生成指定范围内的随机整数 [min, max]
     *
     * @param min 最小值(包含)
     * @param max 最大值(包含)
     * @return 随机整数
     */
    public static int randomInt(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("min不能大于max");
        }
        return RANDOM.nextInt(max - min + 1) + min;
    }

    /**
     * 生成随机整数 [0, bound)
     *
     * @param bound 上界(不包含)
     * @return 随机整数
     */
    public static int randomInt(int bound) {
        return RANDOM.nextInt(bound);
    }

    /**
     * 从字符串中随机选择一个字符
     *
     * @param chars 字符串
     * @return 随机字符
     */
    public static char randomChar(String chars) {
        if (chars == null || chars.isEmpty()) {
            throw new IllegalArgumentException("字符串不能为空");
        }
        return chars.charAt(RANDOM.nextInt(chars.length()));
    }

    /**
     * 从数组中随机选择一个元素
     *
     * @param array 数组
     * @param <T> 元素类型
     * @return 随机元素
     */
    public static <T> T randomElement(T[] array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("数组不能为空");
        }
        return array[RANDOM.nextInt(array.length)];
    }

    /**
     * 生成随机浮点数 [0.0, 1.0)
     *
     * @return 随机浮点数
     */
    public static float randomFloat() {
        return RANDOM.nextFloat();
    }

    /**
     * 生成随机布尔值
     *
     * @return 随机布尔值
     */
    public static boolean randomBoolean() {
        return RANDOM.nextBoolean();
    }
}
