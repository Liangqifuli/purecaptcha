# 🎯 通用验证码工具类

---

## 📋 简介

这是一套完整的验证码工具类解决方案，包含**后端Java工具类**和**前端JavaScript工具类**，支持5种验证码类型，**开箱即用**。

---

## 📦 文件列表

| 文件 | 类型 | 说明 |
|-----|-----|------|
| `CaptchaUtil.java` | 后端工具类 | Java验证码工具类，自动管理验证码生命周期 |
| `captcha-util.js` | 前端工具类 | JavaScript验证码工具类，自动渲染UI |
| `CaptchaUtilExample.java` | 后端示例 | 命令行交互式示例 |
| `captcha-util-demo.html` | 前端示例 | 完整的HTML演示页面 |
| `快速参考.md` | 参考文档 | 快速查阅常用方法 |
| `README.md` | 说明文档 | 本文件 |

---

## 🚀 快速开始

### 后端使用（3步）

#### 1. 复制工具类

将 `CaptchaUtil.java` 复制到你的项目中：

```
你的项目/
└── src/main/java/
    └── utils/
        └── CaptchaUtil.java
```

#### 2. 生成验证码

```java
import utils.CaptchaUtil;
import java.util.Map;

// 生成算术验证码
Map<String, Object> result = CaptchaUtil.arithmetic();

// 获取验证码信息
String captchaId = (String) result.get("captchaId");
String imageBase64 = (String) result.get("imageBase64");
String answer = (String) result.get("answer");
```

#### 3. 验证答案

```java
boolean isValid = CaptchaUtil.verify(captchaId, userAnswer);
```

---

### 前端使用（3步）

#### 1. 引入工具类

```html
<script src="captcha-util.js"></script>
```

#### 2. 创建容器

```html
<div id="captcha-container"></div>
```

#### 3. 初始化并生成

```javascript
const captcha = new CaptchaUtil({
    container: '#captcha-container',
    apiBaseUrl: '/api/captcha',
    type: 'ARITHMETIC',
    onSuccess: (result) => {
        console.log('验证成功');
    }
});

captcha.generate();
```

---

## ✨ 特性

### 后端特性

- ✅ **5种验证码** - 字符、算术、中文、GIF、滑动拼图
- ✅ **自动管理** - 自动过期清理（5分钟）
- ✅ **线程安全** - 使用ConcurrentHashMap
- ✅ **简单易用** - 一行代码生成，一行代码验证
- ✅ **快捷方法** - 提供便捷的快捷生成方法

### 前端特性

- ✅ **自动渲染** - 自动生成完整的UI界面
- ✅ **交互处理** - 自动处理用户输入和拖动
- ✅ **响应式** - 支持PC和移动端
- ✅ **可定制** - 支持回调函数和样式定制
- ✅ **零依赖** - 不依赖任何第三方库

---

## 📝 支持的验证码类型

| 类型 | 常量 | 后端快捷方法 | 说明 |
|-----|------|-------------|------|
| 字符验证码 | `ALPHANUMERIC` | `CaptchaUtil.alphanumeric()` | 大小写字母+数字 |
| 算术验证码 | `ARITHMETIC` | `CaptchaUtil.arithmetic()` | 加减法运算 |
| 中文验证码 | `CHINESE` | `CaptchaUtil.chinese()` | 常用汉字 |
| GIF动图验证码 | `ANIMATED_GIF` | `CaptchaUtil.gif()` | 多帧动画效果 |
| 滑动拼图验证码 | `SLIDER` | `CaptchaUtil.slider()` | 智能验证算法 |

---

## 💡 使用示例

### 后端 - Spring Boot Controller

```java
import org.springframework.web.bind.annotation.*;
import utils.CaptchaUtil;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {
    
    @GetMapping("/generate")
    public Map<String, Object> generate(@RequestParam String type) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> data = CaptchaUtil.generate(type);
            response.put("success", true);
            response.put("data", data);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
    
    @PostMapping("/verify")
    public Map<String, Object> verify(@RequestBody Map<String, String> req) {
        String captchaId = req.get("captchaId");
        String answer = req.get("answer");
        boolean isValid = CaptchaUtil.verify(captchaId, answer);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", isValid);
        response.put("message", isValid ? "验证成功" : "验证失败");
        return response;
    }
}
```

### 前端 - 完整示例

```html
<!DOCTYPE html>
<html>
<head>
    <title>验证码示例</title>
</head>
<body>
    <div id="captcha-container"></div>
    
    <script src="captcha-util.js"></script>
    <script>
        const captcha = new CaptchaUtil({
            container: '#captcha-container',
            apiBaseUrl: '/api/captcha',
            type: 'ARITHMETIC',
            onSuccess: (result) => {
                alert('验证成功！');
            },
            onError: (error) => {
                alert('验证失败！');
            }
        });
        
        captcha.generate();
    </script>
</body>
</html>
```

---

## 🔧 后端API

### 生成验证码

```java
// 通用方法
Map<String, Object> result = CaptchaUtil.generate("ARITHMETIC");

// 快捷方法
Map<String, Object> result1 = CaptchaUtil.alphanumeric();     // 字符
Map<String, Object> result2 = CaptchaUtil.arithmetic();       // 算术
Map<String, Object> result3 = CaptchaUtil.arithmetic(true);   // 算术（加减乘除）
Map<String, Object> result4 = CaptchaUtil.chinese();          // 中文
Map<String, Object> result5 = CaptchaUtil.gif();              // GIF
Map<String, Object> result6 = CaptchaUtil.slider();           // 滑动
```

### 验证答案

```java
// 普通验证码
boolean isValid = CaptchaUtil.verify(captchaId, userAnswer);

// 滑动验证码
boolean isValid = CaptchaUtil.verifySlider(captchaId, userX);
```

### 管理方法

```java
int count = CaptchaUtil.getCount();           // 获取验证码数量
int cleaned = CaptchaUtil.cleanExpired();     // 清理过期验证码
CaptchaUtil.remove(captchaId);                // 删除指定验证码
CaptchaUtil.clear();                          // 清空所有验证码
```

---

## 🎨 前端API

### 初始化

```javascript
const captcha = new CaptchaUtil({
    container: '#captcha-container',  // 必填：容器选择器
    apiBaseUrl: '/api/captcha',       // 可选：API地址
    type: 'ARITHMETIC',               // 可选：验证码类型
    width: 200,                       // 可选：宽度
    height: 80,                       // 可选：高度
    onSuccess: (result) => {},        // 可选：成功回调
    onError: (error) => {},           // 可选：失败回调
    onGenerate: (data) => {}          // 可选：生成回调
});
```

### 方法

```javascript
captcha.generate()          // 生成验证码
captcha.getCaptchaId()      // 获取验证码ID
captcha.getCaptchaData()    // 获取验证码数据
captcha.destroy()           // 销毁实例
```

---

## 📊 返回数据格式

### 普通验证码

```json
{
    "captchaId": "abc123def456...",
    "type": "ARITHMETIC",
    "imageBase64": "iVBORw0KGgoAAAANSUhEUgAA...",
    "answer": "15"
}
```

### 滑动验证码

```json
{
    "captchaId": "abc123def456...",
    "type": "SLIDER",
    "imageBase64": "iVBORw0KGgoAAAANSUhEUgAA...",
    "sliderImageBase64": "iVBORw0KGgoAAAANSUhEUgAA...",
    "sliderY": 120,
    "answer": "250"
}
```

---

## 🎓 运行示例

### 后端示例

```bash
cd examples
javac -encoding UTF-8 -cp "../target/pure-captcha-1.0.0.jar" utils/CaptchaUtilExample.java
java -cp "../target/pure-captcha-1.0.0.jar;." utils.CaptchaUtilExample
```

### 前端示例

在浏览器中打开 `captcha-util-demo.html`

---

## 📚 完整文档

查看 `通用验证码工具类使用指南.md` 了解更多详细信息。

---

## ⚙️ 配置

### 修改过期时间

在 `CaptchaUtil.java` 中修改：

```java
private static final long EXPIRATION_TIME = 5 * 60 * 1000; // 5分钟
```

### 生产环境

删除返回答案的代码：

```java
// 注释这行
// result.put("answer", captcha.getAnswer());
```

---

## 🔒 安全建议

1. **HTTPS** - 生产环境使用HTTPS
2. **隐藏答案** - 生产环境不返回答案
3. **验证次数限制** - 添加IP限制，防止暴力破解
4. **过期时间** - 根据需要调整过期时间

---

## 💡 最佳实践

### 1. 按需生成

不要在页面加载时就生成验证码，而是在用户需要时再生成。

### 2. 失败重试

验证失败后自动刷新验证码：

```javascript
const captcha = new CaptchaUtil({
    onError: () => {
        setTimeout(() => captcha.generate(), 1500);
    }
});
```

### 3. 分布式部署

如果需要分布式部署，建议使用Redis存储验证码：

```java
// 将 CaptchaUtil 中的 ConcurrentHashMap 替换为 Redis
```

---

## 🐛 故障排除

### Q: 编译失败？

A: 确保已经安装 PureCaptcha 库：

```bash
cd D:\Desktop\PureCaptcha
mvn clean install
```

### Q: 验证码不显示？

A: 检查：
1. API接口是否正常返回数据
2. 浏览器控制台是否有错误
3. `apiBaseUrl`配置是否正确

### Q: 验证总是失败？

A: 检查：
1. 验证码是否过期（默认5分钟）
2. `captchaId`是否正确传递
3. 答案格式是否正确

---

## ✅ 检查清单

使用前请确认：

- [x] 已安装PureCaptcha库
- [x] 已复制`CaptchaUtil.java`到项目
- [x] 已复制`captcha-util.js`到项目
- [x] 已创建Controller提供API
- [x] 已配置正确的`apiBaseUrl`
- [x] 已测试所有验证码类型

---

## 📄 许可证

本工具类是PureCaptcha项目的一部分。

---

## 📞 支持

- 查看完整文档：`通用验证码工具类使用指南.md`
- 查看快速参考：`快速参考.md`
- 查看示例项目：`mypurecaptcha/`

---

**版本**: 1.0.0  
**最后更新**: 2025-10-20  
**作者**: PureCaptcha  

═══════════════════════════════════════════════════════════════

