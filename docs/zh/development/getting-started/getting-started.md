
![banner.png](../../../assets/images/banner.png)

# 快速开始

## 欢迎使用 AIFlowy

感谢您选择 AIFlowy！本指南将帮助您在几分钟内完成本地环境搭建并成功运行 AIFlowy。  
如在使用过程中遇到问题，欢迎加入 "AIFlowy 技术交流群" 进行讨论。



## 本章目标

- 快速完成 AIFlowy 后端与前端的本地部署
- 验证系统是否正常运行
- 为后续开发与定制打下基础



## 环境要求

AIFlowy 采用现代化技术栈，需确保您的开发环境满足以下要求：

### 后端（Java）
- **JDK 17+**（推荐 OpenJDK 17）
- **Maven 3.9+**
- **MySQL 8.x**

### 前端（Web）
- **Node.js v22+**
- **pnpm v10+**

> 💡 提示：可通过 `java -version`、`mvn -v`、`node -v`、`pnpm -v` 等命令验证版本。



## 1. 启动后端服务

### 1.1 导入项目到 IDEA

1. 打开 IntelliJ IDEA
2. 选择 **File → Open**，定位到 AIFlowy 项目根目录并打开  
   ![open_in_idea.png](resource/open_in_idea.png)

### 1.2 初始化数据库

1. 在 MySQL 中创建数据库（例如 `aiflowy`）
2. 执行项目根目录 `/sql` 下的两个脚本：
    - `aiflowy-v2.ddl.sql`（建表）
    - `aiflowy-v2.data.sql`（初始数据）

> ✅ 建议使用 `utf8mb4` 字符集和 `utf8mb4_unicode_ci` 排序规则。

### 1.3 配置数据库连接

编辑 `aiflowy-starter/aiflowy-starter-all/src/main/resources/application.yml`，更新以下内容：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/aiflowy?useInformationSchema=true&characterEncoding=utf-8
    username: root
    password: 123456
```

> ⚠️ **注意**：`useInformationSchema=true` 是必须的，用于支持 MyBatis-Flex 正确读取表注释。

#### （可选）配置文件存储方式

##### 本地存储（默认）
```yaml
spring:
  web:
    resources:
      # 例如：file:/www/aiflowy/file（Linux）或 file:C:/aiflowy/file（Windows）
      static-locations: file:/your/local/path  
  mvc:
    # ！！！ 注意，这里要和下面的 aiflowy.storage.local.prefix 的后面路径保持一致！
    static-path-pattern: /attachment/**
aiflowy:
   storage:
      type: local # xFileStorage / local
      # 本地文件存储配置
      local:
         # 示例：windows【C:\aiflowy\attachment】 linux【/www/aiflowy/attachment】
         root: /your/local/path
         # 后端接口地址，用于拼接完整 url
         prefix: http://localhost:8080/attachment
```

##### 第三方存储（如阿里云 OSS）
```yaml
# xFileStorage存储文件配置
dromara:
   x-file-storage: #文件存储配置
      default-platform: aliyun-oss-1 #默认使用的存储平台
      aliyun-oss:
         - platform: aliyun-oss-1 # 存储平台标识
           enable-storage: true  # 启用存储
           access-key: yourAccessKeyId
           secret-key: yourAccessKeySecret
           end-point: yourEndpoint # 示例：https://oss-cn-beijing.aliyuncs.com
           bucket-name: yourBucketName
           domain: yourDomain # 访问域名，注意“/”结尾，例如：https://bucketname.oss-cn-shanghai.aliyuncs.com/
           base-path: attachment # 基础路径
```

### 1.4 编译并启动应用

1. 在项目根目录执行 Maven 编译：
   ```bash
   mvn clean package -DskipTests
   ```
   ![mvn.png](resource/mvn.png)

2. 编译成功后，运行主启动类：
    - 模块：`aiflowy-starter -->  aiflowy-starter-all`
    - 类路径：`com.aiflowy.starter.MainApplication`

   ![run.png](resource/run.png)

> 🔧 **常见问题**：若提示 `Command line is too long`，请在 IDEA 运行配置中将 `Shorten command line` 改为 **JAR manifest**  
> ![shorten.png](resource/shorten.png)



## 2. 运行前端部分

在运行前端程序之前，需要您的电脑安装好 Node 环境，注意版本为 `v22+` 。

后台管理目录：`aiflowy-ui-admin`

用户中心目录：`aiflowy-ui-usercenter`

### 2.1 安装依赖

进入前端目录并安装依赖：

```bash
cd aiflowy-ui-admin
# or
cd aiflowy-ui-usercenter
pnpm install
```

> 🌐 **网络问题处理**（如 400/500 错误）：
> ```bash
> npm config set proxy null
> npm config set https-proxy null
> npm cache clean --force
> npm config set registry https://registry.npmmirror.com
> ```

### 2.2 启动开发服务器

启动前先检查开发环境配置，配置文件：`app/.env.development`

```bash
pnpm dev
```

成功启动后，终端将显示访问地址：

后台管理默认：`http://localhost:5090`，用户中心默认：`http://localhost:5091`

![pnpm_dev.png](resource/pnpm_dev.png)

打开浏览器访问该地址，看到登录页即表示前端启动成功：  
![login_page.png](resource/login_page.png)

> 🔑 **默认账号**：`admin` / `123456`



## 下一步

🎉 恭喜！您已成功运行 AIFlowy。  
接下来，您可以：
- 阅读 [目录结构](directory-structure.md) 了解架构设计
- 加入社区讨论定制化需求与最佳实践

> 📣 如遇问题，请优先查阅 [常见问题 FAQ](questions.md) 或联系社区支持。

