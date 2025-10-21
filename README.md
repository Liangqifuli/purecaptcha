# PureCaptcha

**纯 Java 图形验证码工具库 - 零依赖、全平台兼容、五类验证码一体化支持**

## 特性

- ✅ **零依赖** - 仅使用 JDK 标准库,无任何第三方依赖
- 🚀 **全平台兼容** - 支持 Java 8+,适用于任何 Java 环境
- 🎨 **五类验证码** - 字符、算术、中文、GIF动画、滑动拼图
- 🔒 **线程安全** - 无状态设计,支持高并发场景
- 📦 **开箱即用** - 简洁的 API,无需复杂配置

## 快速开始

### Maven 依赖

```xml
<dependency>
    <groupId>io.github.purecaptcha</groupId>
    <artifactId>pure-captcha</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 基本使用

```java
// 生成字符验证码
Captcha captcha = CaptchaGenerator.create(CaptchaType.ALPHANUMERIC);
BufferedImage image = captcha.getImage();
String answer = captcha.getAnswer();

// 输出到 HTTP 响应
ImageIO.write(image, "PNG", response.getOutputStream());
```

## 支持的验证码类型

1. **字符验证码** - 数字、字母或混合字符

2. **算术验证码** - 简单四则运算

3. **中文验证码** - 成语、诗词、常用词

4. **GIF动画验证码** - 多帧动态干扰

5. **滑动拼图验证码** - 行为式验证

   ![images](./images/images.png)

## 许可证

Apache License 2.0

## 项目状态

🚧 正在开发中...
