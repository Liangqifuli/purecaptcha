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
 * 中文验证码生成器
 * <p>
 * 支持常用汉字验证码生成
 *
 * @author PureCaptcha
 * @version 1.0.0
 */
public class ChineseCaptchaGenerator implements CaptchaGenerator {

    // 常用汉字字符集 (3500个常用汉字的子集)
    private static final String[] COMMON_CHINESE = {
        "的", "一", "是", "在", "不", "了", "有", "和", "人", "这",
        "中", "大", "为", "上", "个", "国", "我", "以", "要", "他",
        "时", "来", "用", "们", "生", "到", "作", "地", "于", "出",
        "就", "分", "对", "成", "会", "可", "主", "发", "年", "动",
        "同", "工", "也", "能", "下", "过", "子", "说", "产", "种",
        "面", "而", "方", "后", "多", "定", "行", "学", "法", "所",
        "民", "得", "经", "十", "三", "之", "进", "着", "等", "部",
        "度", "家", "电", "力", "里", "如", "水", "化", "高", "自",
        "二", "理", "起", "小", "物", "现", "实", "加", "量", "都",
        "两", "体", "制", "机", "当", "使", "点", "从", "业", "本",
        "去", "把", "性", "好", "应", "开", "它", "合", "还", "因",
        "由", "其", "些", "然", "前", "外", "天", "政", "四", "日",
        "那", "社", "义", "事", "平", "形", "相", "全", "表", "间",
        "样", "与", "关", "各", "重", "新", "线", "内", "数", "正",
        "心", "反", "你", "明", "看", "原", "又", "么", "利", "比",
        "或", "但", "质", "气", "第", "向", "道", "命", "此", "变",
        "条", "只", "没", "结", "解", "问", "意", "建", "月", "公",
        "无", "系", "军", "很", "情", "者", "最", "立", "代", "想",
        "已", "通", "并", "提", "直", "题", "党", "程", "展", "五",
        "果", "料", "象", "员", "革", "位", "入", "常", "文", "总",
        "次", "品", "式", "活", "设", "及", "管", "特", "件", "长",
        "求", "老", "头", "基", "资", "边", "流", "路", "级", "少",
        "图", "山", "统", "接", "知", "较", "将", "组", "见", "计",
        "别", "她", "手", "角", "期", "根", "论", "运", "农", "指",
        "几", "九", "区", "强", "放", "决", "西", "被", "干", "做",
        "必", "战", "先", "回", "则", "任", "取", "据", "处", "队",
        "南", "给", "色", "光", "门", "即", "保", "治", "北", "造",
        "百", "规", "热", "领", "七", "海", "口", "东", "导", "器",
        "压", "志", "世", "金", "增", "争", "济", "阶", "油", "思",
        "术", "极", "交", "受", "联", "什", "认", "六", "共", "权",
        "收", "证", "改", "清", "美", "再", "采", "转", "更", "单",
        "风", "切", "打", "白", "教", "速", "花", "带", "安", "场",
        "身", "车", "例", "真", "务", "具", "万", "每", "目", "至",
        "达", "走", "积", "示", "议", "声", "报", "斗", "完", "类",
        "八", "离", "华", "名", "确", "才", "科", "张", "信", "马",
        "节", "话", "米", "整", "空", "元", "况", "今", "集", "温",
        "传", "土", "许", "步", "群", "广", "石", "记", "需", "段",
        "研", "界", "拉", "林", "律", "叫", "且", "究", "观", "越",
        "织", "装", "影", "算", "低", "持", "音", "众", "书", "布",
        "复", "容", "儿", "须", "际", "商", "非", "验", "连", "断",
        "深", "难", "近", "矿", "千", "周", "委", "素", "技", "备"
    };

    @Override
    public Captcha generate(CaptchaConfig config) {
        // 生成验证码文本
        String captchaText = generateCaptchaText(config);

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

        // 绘制风格化中文字符
        drawStyledChineseCharacters(g2d, captchaText, config);

        // 绘制少量噪点
        ImageUtil.drawNoisePoints(g2d, config.getWidth(), config.getHeight(),
                                 config.getNoisePointCount() / 3);

        g2d.dispose();

        // 中文验证码不区分大小写
        return new CaptchaResult(CaptchaType.CHINESE, image, captchaText, false);
    }

    @Override
    public Captcha generate() {
        return generate(CaptchaConfig.builder()
                .charLength(4)  // 中文验证码默认4个字
                .build());
    }

    @Override
    public CaptchaType getSupportedType() {
        return CaptchaType.CHINESE;
    }

    /**
     * 生成验证码文本
     *
     * @param config 配置
     * @return 验证码文本
     */
    private String generateCaptchaText(CaptchaConfig config) {
        StringBuilder text = new StringBuilder();

        for (int i = 0; i < config.getCharLength(); i++) {
            text.append(COMMON_CHINESE[RandomUtil.randomInt(COMMON_CHINESE.length)]);
        }

        return text.toString();
    }

    /**
     * 绘制中文字符
     *
     * @param g2d Graphics2D对象
     * @param text 验证码文本
     * @param config 配置
     */
    private void drawChineseCharacters(Graphics2D g2d, String text, CaptchaConfig config) {
        int width = config.getWidth();
        int height = config.getHeight();
        int charCount = text.length();

        // 计算每个字符的宽度
        int charWidth = width / charCount;

        // 使用支持中文的字体
        Font chineseFont = new Font("宋体", Font.BOLD, 32);
        g2d.setFont(chineseFont);

        for (int i = 0; i < charCount; i++) {
            String c = String.valueOf(text.charAt(i));

            // 设置颜色(如果配置了固定颜色则使用,否则随机)
            Color color = config.getFontColor() != null
                    ? config.getFontColor()
                    : ColorUtil.randomDarkColor();
            g2d.setColor(color);

            // 计算字符位置(添加随机偏移)
            int x = charWidth * i + RandomUtil.randomInt(charWidth / 4, charWidth / 2);
            int y = height / 2 + RandomUtil.randomInt(-height / 8, height / 6);

            // 随机旋转角度(-20度到20度,中文字符旋转角度小一些)
            double angle = Math.toRadians(RandomUtil.randomInt(-20, 20));

            // 绘制旋转的字符
            ImageUtil.drawRotatedText(g2d, c, x, y, angle);
        }
    }

    /**
     * 绘制风格化中文字符（带描边和阴影）
     * 完全不旋转，确保不超界
     *
     * @param g2d Graphics2D对象
     * @param text 验证码文本
     * @param config 配置
     */
    private void drawStyledChineseCharacters(Graphics2D g2d, String text, CaptchaConfig config) {
        int width = config.getWidth();
        int height = config.getHeight();
        int charCount = text.length();

        // 使用清晰的中文字体，字号稍微减小
        Font styledFont = new Font("Microsoft YaHei UI", Font.BOLD, 
                                   Math.max(28, config.getFont().getSize() - 2));
        g2d.setFont(styledFont);

        // 获取字体度量信息
        FontMetrics fm = g2d.getFontMetrics(styledFont);
        int ascent = fm.getAscent();
        
        // 计算所有字符的实际宽度
        int totalCharsWidth = 0;
        for (int i = 0; i < charCount; i++) {
            totalCharsWidth += fm.stringWidth(String.valueOf(text.charAt(i)));
        }
        
        // 左右各留10px安全空间（镂空需要更多空间）
        int padding = 10;
        
        // 可用宽度
        int availableWidth = width - padding * 2;
        
        // 增加字符间距
        int charSpacing = 8;
        int totalSpacing = (charCount - 1) * charSpacing;
        
        // 如果空间不够，减小间距
        if (totalCharsWidth + totalSpacing > availableWidth) {
            charSpacing = Math.max(3, (availableWidth - totalCharsWidth) / (charCount - 1));
            totalSpacing = (charCount - 1) * charSpacing;
        }
        
        // 计算起始位置（居中）
        int contentWidth = totalCharsWidth + totalSpacing;
        int startX = (width - contentWidth) / 2;
        
        // Y轴居中位置
        int baseY = (height + ascent) / 2;

        int currentX = startX;
        for (int i = 0; i < charCount; i++) {
            String c = String.valueOf(text.charAt(i));
            int charWidth = fm.stringWidth(c);

            // 每个字符使用鲜艳的随机颜色
            Color charColor = config.getFontColor() != null
                    ? config.getFontColor()
                    : ColorUtil.randomVibrantColor();

            // 计算字符绘制位置（字符中心点）
            int x = currentX + charWidth / 2;
            int y = baseY + RandomUtil.randomInt(-2, 2);

            // 轻微旋转
            double angle = Math.toRadians(RandomUtil.randomInt(-8, 8));

            // 使用镂空字体绘制
            ImageUtil.drawHollowText(g2d, c, x, y, angle, charColor);
            
            // 移动到下一个字符位置
            currentX += charWidth + charSpacing;
        }
    }
}
