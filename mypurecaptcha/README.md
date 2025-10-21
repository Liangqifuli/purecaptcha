# 🎯 MyPureCaptcha - 验证码测试项目

一个基于 **Spring Boot + Maven** 的完整验证码测试平台，集成 **PureCaptcha** 验证码库，支持 5 种验证码类型。

---

## ✨ 功能特性

### 支持的验证码类型

| 类型 | 说明 | API端点 |
|-----|------|---------|
| 📝 **字符验证码** | 随机字母数字组合 | `/api/captcha/alphanumeric` |
| 🔢 **算术验证码** | 简单算术题（加减乘除） | `/api/captcha/arithmetic` |
| 🈳 **中文验证码** | 随机中文字符 | `/api/captcha/chinese` |
| 🎬 **GIF动图验证码** | 动态字符验证码 | `/api/captcha/gif` |
| 🧩 **滑动拼图验证码** | 滑动拼图验证（智能算法） | `/api/captcha/slider` |

### 核心特点

- ✅ **完全集成** - Spring Boot + PureCaptcha 开箱即用
- ✅ **5种验证码** - 字符、算术、中文、GIF、滑动拼图
- ✅ **智能验证** - 滑动验证码采用图像相似度算法
- ✅ **美观界面** - 现代化Web UI，响应式设计
- ✅ **RESTful API** - 标准REST接口，易于集成
- ✅ **自动清理** - 验证码自动过期和清理机制
- ✅ **开发友好** - 支持热部署，详细日志输出

---

## 📦 项目结构

```
mypurecaptcha/
├── src/
│   ├── main/
│   │   ├── java/com/example/captcha/
│   │   │   ├── CaptchaApplication.java          # Spring Boot 主启动类
│   │   │   ├── controller/
│   │   │   │   └── CaptchaController.java       # 控制器层（API）
│   │   │   └── service/
│   │   │       └── CaptchaService.java          # 服务层（业务逻辑）
│   │   └── resources/
│   │       ├── application.properties            # 配置文件
│   │       └── static/
│   │           └── index.html                    # 前端测试页面
├── pom.xml                                       # Maven 依赖配置
└── README.md                                     # 项目说明文档
```

---

## 🚀 快速开始

### 1. 前置条件

- ✅ **JDK 17+** （已升级到Java 17，利用record、switch表达式等新特性）
- ✅ **Maven 3.8+**
- ✅ **PureCaptcha 1.0.0** 已安装到本地 Maven 仓库

> **提示**：PureCaptcha 需要先安装到本地 Maven 仓库，路径为：
> `D:\JavaFile\apache-maven-3.8.6\repository_boot\io\github\purecaptcha\pure-captcha\1.0.0\`

---

### 2. 启动项目

#### 方式一：使用 Maven 命令（推荐）

```bash
# 进入项目目录
cd mypurecaptcha

# 启动项目
mvn spring-boot:run
```

#### 方式二：使用 IDE

1. 在 **IntelliJ IDEA** 或 **Eclipse** 中打开项目
2. 运行 `CaptchaApplication.java` 主类
3. 等待启动完成

---

### 3. 访问测试页面

启动成功后，访问：

```
http://localhost:8080
```

你将看到一个包含所有 5 种验证码的测试页面！

---

## 📡 API 接口文档

### 基础信息

- **Base URL**: `http://localhost:8080/api/captcha`
- **Content-Type**: `application/json`

---

### 1. 生成验证码

#### 通用接口

```http
GET /api/captcha/generate?type={验证码类型}
```

**参数**：
- `type`: 验证码类型（必填）
  - `ALPHANUMERIC` - 字符验证码
  - `ARITHMETIC` - 算术验证码
  - `CHINESE` - 中文验证码
  - `ANIMATED_GIF` - GIF动图验证码
  - `SLIDER` - 滑动拼图验证码

**响应示例**（字符验证码）：
```json
{
  "success": true,
  "data": {
    "image": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUg...",
    "sessionId": "F5A8E3B4...",
    "type": "ALPHANUMERIC"
  },
  "message": "验证码生成成功"
}
```

**响应示例**（滑动验证码）：
```json
{
  "success": true,
  "data": {
    "backgroundImage": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUg...",
    "sliderImage": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUg...",
    "sliderY": 80,
    "sessionId": "F5A8E3B4...",
    "type": "SLIDER"
  },
  "message": "验证码生成成功"
}
```

---

#### 专用接口

```http
# 字符验证码
GET /api/captcha/alphanumeric

# 算术验证码
GET /api/captcha/arithmetic

# 中文验证码
GET /api/captcha/chinese

# GIF验证码
GET /api/captcha/gif

# 滑动验证码
GET /api/captcha/slider
```

---

### 2. 验证验证码

```http
POST /api/captcha/verify
```

**请求体**：

字符/算术/中文/GIF验证码：
```json
{
  "answer": "用户输入的答案"
}
```

滑动验证码：
```json
{
  "userX": "254"
}
```

**响应示例**：
```json
{
  "success": true,
  "message": "验证通过 ✅"
}
```

---

### 3. 调试接口

```http
POST /api/captcha/debug
```

**请求体**：
```json
{
  "userX": "254"  // 或 "answer": "abc123"
}
```

**响应示例**：
```json
{
  "success": true,
  "details": "验证详情：\n坐标偏差: 2px\n图像相似度: 92.5%\n验证结果: 通过 ✅"
}
```

---

### 4. 健康检查

```http
GET /api/captcha/health
```

**响应示例**：
```json
{
  "status": "UP",
  "service": "CaptchaService",
  "supportedTypes": ["ALPHANUMERIC", "ARITHMETIC", "CHINESE", "ANIMATED_GIF", "SLIDER"]
}
```

---

### 5. 统计信息

```http
GET /api/captcha/stats
```

**响应示例**：
```json
{
  "success": true,
  "captchaCount": 5,
  "message": "当前存储的验证码数量: 5"
}
```

---

## 🎨 前端集成示例

### JavaScript 示例

#### 1. 生成字符验证码

```javascript
async function generateCaptcha() {
    const response = await fetch('/api/captcha/alphanumeric');
    const result = await response.json();
    
    if (result.success) {
        document.getElementById('captcha-img').src = result.data.image;
    }
}
```

#### 2. 验证字符验证码

```javascript
async function verifyCaptcha() {
    const userAnswer = document.getElementById('captcha-input').value;
    
    const response = await fetch('/api/captcha/verify', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ answer: userAnswer })
    });
    
    const result = await response.json();
    alert(result.message);
}
```

#### 3. 滑动验证码（完整示例）

```javascript
// 生成滑动验证码
async function generateSlider() {
    const response = await fetch('/api/captcha/slider');
    const result = await response.json();
    
    if (result.success) {
        document.getElementById('bg-img').src = result.data.backgroundImage;
        document.getElementById('slider-img').src = result.data.sliderImage;
        document.getElementById('slider-img').style.top = result.data.sliderY + 'px';
    }
}

// 验证滑动位置
async function verifySlider(userX) {
    const response = await fetch('/api/captcha/verify', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ userX: userX.toString() })
    });
    
    const result = await response.json();
    alert(result.message);
}
```

---

## ⚙️ 配置说明

### application.properties

```properties
# 服务器端口
server.port=8080

# Session 过期时间
server.servlet.session.timeout=30m

# 验证码过期时间（分钟）
captcha.expire.minutes=5

# 验证码存储上限
captcha.storage.limit=10000
```

### Maven 依赖（pom.xml）

```xml
<dependencies>
    <!-- Spring Boot Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- PureCaptcha 验证码库 -->
    <dependency>
        <groupId>io.github.purecaptcha</groupId>
        <artifactId>pure-captcha</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

---

## 🔧 常见问题

### 1. 依赖找不到：pure-captcha

**问题**：
```
Could not find artifact io.github.purecaptcha:pure-captcha:jar:1.0.0
```

**解决**：
确保 PureCaptcha 已安装到本地 Maven 仓库。如果没有，请先在 PureCaptcha 项目中运行：
```bash
mvn clean install
```

---

### 2. 端口 8080 被占用

**问题**：
```
Port 8080 was already in use.
```

**解决**：
修改 `application.properties` 中的端口：
```properties
server.port=8081
```

---

### 3. 滑动验证码总是失败

**问题**：
明明滑到正确位置，但验证总是失败。

**解决**：
- 当前验证算法已优化，容差为 **12px**，相似度阈值为 **88%**
- 查看控制台日志，确认实际偏差和相似度
- 如需调试，使用 `/api/captcha/debug` 接口查看详情

---

### 4. GIF 验证码不动

**问题**：
GIF 验证码显示但不动。

**解决**：
- 确认浏览器支持 GIF 动画
- 查看网络请求，确认接收到的是 `image/gif` 格式
- 尝试刷新页面

---

## 📊 性能优化建议

### 1. Redis 集成（可选）

如果需要支持分布式部署，可以将验证码存储到 Redis：

```java
// 添加 Redis 依赖
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

// 修改 CaptchaService 使用 RedisTemplate
@Autowired
private RedisTemplate<String, Captcha> redisTemplate;
```

### 2. 验证码缓存

```java
// 可以添加缓存，避免短时间内重复生成
@Cacheable(value = "captchas", key = "#sessionId")
public Captcha generateCaptcha(String sessionId, CaptchaType type) {
    // ...
}
```

### 3. 异步生成

```java
// 使用异步方式生成验证码，提高响应速度
@Async
public CompletableFuture<Captcha> generateCaptchaAsync(CaptchaType type) {
    return CompletableFuture.completedFuture(CaptchaFactory.create(type, config));
}
```

---

## 📝 开发日志

### 控制台输出示例

```
📝 生成验证码 - SessionID: CAECFE6216F99636D3D5D7851448A994, 类型: SLIDER
  ├─ 滑动验证码位置: X=254, Y=80
  
🎯 滑动验证码验证 - SessionID: CAECFE6216F99636D3D5D7851448A994
   用户位置: 256
   正确位置: 254
   结果: 通过 ✅

🧹 清理过期验证码，当前剩余: 3
```

---

## 🎯 下一步计划

- [ ] 添加更多验证码类型（点选文字、旋转图片等）
- [ ] 支持自定义验证码样式和颜色
- [ ] 添加验证码难度等级配置
- [ ] 提供 Vue/React 组件封装
- [ ] 添加验证码统计分析功能
- [ ] 支持多语言国际化

---

## 📄 开源协议

本项目基于 **MIT License** 开源。

---

## 🙏 致谢

- [PureCaptcha](https://github.com/purecaptcha/pure-captcha) - 强大的验证码库
- [Spring Boot](https://spring.io/projects/spring-boot) - 优秀的 Java 框架

---

## 📧 联系方式

如有问题或建议，欢迎提交 Issue 或 Pull Request！

---

**祝你使用愉快！🎉**

═══════════════════════════════════════════════════════════════

