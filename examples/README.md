# PureCaptcha 使用示例

本目录包含 PureCaptcha 的各种使用示例代码。

## 运行示例

### 编译项目
```bash
mvn clean install
```

### 运行示例代码
```bash
# PowerShell
javac -cp "..\target\pure-captcha-1.0.0-SNAPSHOT.jar" CaptchaExample.java
java -cp ".;..\target\pure-captcha-1.0.0-SNAPSHOT.jar" io.github.purecaptcha.examples.CaptchaExample

# Bash
javac -cp "../target/pure-captcha-1.0.0-SNAPSHOT.jar" CaptchaExample.java
java -cp ".:../target/pure-captcha-1.0.0-SNAPSHOT.jar" io.github.purecaptcha.examples.CaptchaExample
```

## 示例说明

### 示例1: 默认字符验证码
使用默认配置生成最基本的字符验证码。

### 示例2: 自定义配置的字符验证码
展示如何通过 Builder 模式自定义各种参数,包括:
- 图像尺寸
- 字符长度和类型
- 颜色配置
- 字体设置
- 干扰线和噪点

### 示例3: 纯数字验证码
生成仅包含数字的验证码,适合手机短信验证场景。

### 示例4: 默认算术验证码
使用默认配置生成加减法算术验证码。

### 示例5: 包含乘除的算术验证码
生成包含四则运算的算术验证码,增强安全性。

### 示例6: 验证用户输入
演示如何验证用户输入的答案,包括大小写处理。

## 输出结果

所有生成的验证码图片会保存在 `examples/output/` 目录中。
