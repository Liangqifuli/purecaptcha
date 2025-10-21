# Redis启动指南

## ⚠️ 当前问题

您的Redis服务器未运行，导致应用无法连接：
```
RedisConnectionFailureException: Unable to connect to Redis
Connection refused: no further information
```

---

## 🚀 启动Redis服务器

### 方法1：使用Windows服务启动

如果Redis已安装为Windows服务：

1. **打开服务管理器**：
   - 按 `Win + R`
   - 输入 `services.msc`
   - 按 `Enter`

2. **找到Redis服务**：
   - 查找名为 `Redis` 的服务
   - 右键点击 → 启动

3. **验证服务状态**：
   - 确认状态显示为"正在运行"

### 方法2：使用命令行启动

如果Redis未安装为服务，需要手动启动：

#### 步骤1：找到Redis安装目录

常见位置：
- `C:\Redis\`
- `C:\Program Files\Redis\`
- `D:\Redis\`
- 或您自定义的安装路径

#### 步骤2：启动Redis服务器

打开**PowerShell**或**CMD**，运行：

```bash
# 进入Redis安装目录
cd C:\Redis

# 启动Redis服务器（使用自定义配置）
redis-server.exe redis.windows.conf
```

**⚠️ 注意**：
- 保持此窗口打开
- 如果关闭窗口，Redis会停止运行
- 您可能需要修改配置文件指定端口16831

#### 步骤3：配置Redis端口和密码

如果您的Redis配置文件（`redis.windows.conf`）中端口不是16831：

1. **编辑配置文件**：
   ```bash
   notepad redis.windows.conf
   ```

2. **修改端口**：
   ```
   port 16831
   ```

3. **设置密码**：
   ```
   requirepass root@root
   ```

4. **保存并重启Redis**

---

## 🔍 验证Redis连接

### 检查Redis是否运行

```bash
# 方法1：使用redis-cli
redis-cli -p 16831 -a root@root ping

# 预期输出：PONG
```

如果提示 `redis-cli` 命令未找到，说明：
- Redis的bin目录未添加到系统PATH
- 或者Redis未安装

### 检查端口是否监听

```powershell
# 使用PowerShell检查端口
netstat -ano | findstr "16831"

# 如果看到 LISTENING，说明Redis正在运行
```

---

## 📋 常见问题

### 问题1：找不到redis-cli命令

**原因**：Redis的bin目录未添加到系统PATH

**解决方案**：
1. 找到Redis安装目录（例如 `C:\Redis`）
2. 直接使用完整路径运行：
   ```bash
   C:\Redis\redis-cli.exe -p 16831 -a root@root ping
   ```

### 问题2：端口被占用

**错误信息**：
```
Creating Server TCP listening socket *:16831: bind: No error
```

**解决方案**：
1. 检查哪个程序占用了端口：
   ```powershell
   netstat -ano | findstr "16831"
   ```
2. 终止占用端口的进程或更换端口

### 问题3：Redis服务启动失败

**可能原因**：
- 配置文件错误
- 权限不足
- 端口被占用

**解决方案**：
1. 以管理员身份运行PowerShell
2. 检查Redis日志文件
3. 验证配置文件格式

---

## 🎯 验证应用连接

### 启动Redis后测试

1. **启动Redis服务器**（见上述方法）

2. **验证连接**：
   ```bash
   redis-cli -p 16831 -a root@root ping
   ```
   
   **预期输出**：`PONG`

3. **重新启动应用**：
   ```bash
   cd D:\Desktop\PureCaptcha\mypurecaptcha
   mvn spring-boot:run
   ```

4. **检查启动日志**：
   - 应该没有 `RedisConnectionFailureException` 错误
   - 能够正常访问 http://localhost:8080

---

## 🔧 临时解决方案（如果无法启动Redis）

如果您暂时无法启动Redis，可以联系我修改为内存存储版本（不依赖Redis）。

---

## 📝 推荐配置

### redis.windows.conf 最小配置

```conf
# 端口配置
port 16831

# 密码配置
requirepass root@root

# 后台运行（Windows不支持）
# daemonize yes

# 持久化配置（可选）
save 900 1
save 300 10
save 60 10000

# 日志配置
loglevel notice
logfile "redis.log"

# 数据库数量
databases 16

# 最大内存（可选，例如512MB）
# maxmemory 512mb
# maxmemory-policy allkeys-lru
```

---

## ✅ 成功标志

当Redis正常运行且应用连接成功后，您会看到：

### 1. Redis服务器日志
```
Ready to accept connections
```

### 2. 应用启动日志
```
MyPureCaptcha 启动成功！
访问地址：http://localhost:8080
```

### 3. 生成验证码日志
```
📝 生成验证码 - SessionID: xxx, 类型: ARITHMETIC
  ├─ Redis Key: captcha:xxx:ARITHMETIC
  ├─ 过期时间: 3分钟
  └─ 验证码答案: 42
```

### 4. 无连接错误
- 不再出现 `RedisConnectionFailureException`
- 不再出现 `Connection refused`

---

**🎯 请先启动Redis，然后再重新启动应用！**

