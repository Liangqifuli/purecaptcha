package io.github.purecaptcha.util;

/**
 * 内置滑动验证码背景图片枚举
 * <p>
 * 提供多种预设背景图片,打包在jar包中,随处可用
 *
 * @author PureCaptcha
 * @version 1.0.0
 */
public enum BuiltinSliderBackground {

    /**
     * 蓝色渐变 + 几何图形
     */
    BLUE_GRADIENT("slider-backgrounds/bg1_blue_gradient.png"),

    /**
     * 绿色自然 + 波浪
     */
    GREEN_NATURE("slider-backgrounds/bg2_green_nature.png"),

    /**
     * 紫色梦幻 + 星星
     */
    PURPLE_DREAM("slider-backgrounds/bg3_purple_dream.png"),

    /**
     * 橙色温暖 + 几何拼接
     */
    ORANGE_WARM("slider-backgrounds/bg4_orange_warm.png"),

    /**
     * 粉色浪漫 + 气泡
     */
    PINK_ROMANTIC("slider-backgrounds/bg5_pink_romantic.png"),

    /**
     * 红色渐变 + 圆圈
     */
    RED_GRADIENT("slider-backgrounds/custom_sample_1.png"),

    /**
     * 青色渐变 + 方块
     */
    CYAN_GRADIENT("slider-backgrounds/custom_sample_2.png"),

    /**
     * 黄色渐变 + 三角
     */
    YELLOW_GRADIENT("slider-backgrounds/custom_sample_3.png"),

    /**
     * 紫色梦幻照片1（高清）
     */
    PHOTO_PURPLE_1("slider-backgrounds/bg6_purple_dream.jpg"),

    /**
     * 紫色梦幻照片2（高清）
     */
    PHOTO_PURPLE_2("slider-backgrounds/bg7_purple_dream.jpg"),

    /**
     * 紫色梦幻照片3（高清）
     */
    PHOTO_PURPLE_3("slider-backgrounds/bg8_purple_dream.jpg");

    private final String resourcePath;

    BuiltinSliderBackground(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    /**
     * 获取资源路径
     */
    public String getResourcePath() {
        return resourcePath;
    }

    /**
     * 随机获取一个内置背景
     */
    public static BuiltinSliderBackground random() {
        BuiltinSliderBackground[] values = values();
        int index = RandomUtil.randomInt(values.length);
        return values[index];
    }
}

