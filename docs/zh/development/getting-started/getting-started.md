
![banner.png](../../../assets/images/banner.png)

# 快速开始

## 前言

欢迎使用 AIFlowy ，在阅读文档的过程中，如果您对文档有任何的问题，可在 AIFlowy 的交流群里进行交流。

## 章节目的

本章节的目的，是为了帮助您快速的使用 AIFlowy，把 AIFlowy 运行起来。

## 环境准备

AIFlowy 后端使用 SpringBoot 2.x + Agents-Flex + MyBatis-Flex 开发。

前端使用 React 18.2 + ant design + Zustand 开发。

因此，要求您的电脑环境必须安装以下内容：
 - JDK 1.8 +
 - Maven 3.9+
 - Node v20+
 - NPM v10+
 - MySQL 8.x

## 运行 AIFlowy 后端部分

### 第一步：把项目导入到 IDEA 开发工具

打开 idea 开发工具，然后选择 file -> open 菜单，选择 AIFlowy 的目录即可。如下图所示：

![open_in_idea.png](resource/open_in_idea.png)

### 第二步：创建数据库以及初始化数据

在 AIFlowy 的根目录中，有一个叫sql的文件夹，里面包含了ddl语句和初始化数据。

在 MySql 中创建好数据库后，分别执行 `aiflowy-v2.ddl.sql` 和 `aiflowy-v2.data.sql` 即可。

### 第三步：修改配置文件

创建好数据库以及初始化数据后，我们需要修改项目的数据库配置文件 `application.yml`，如下图所示：
![db_config.png](resource/db_config.png)

内容如下：
```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/aiflowy?useInformationSchema=true&characterEncoding=utf-8
    username: root
    password: 123456
```

 - url： jdbc 链接数据库的 URL 内容，注意： url 参数需要添加 `useInformationSchema=true` 已保证 JDBC 能够正常获取表的 `comment` 信息。
 - username：数据库账号
 - password：数据库密码

**修改文件存储信息（如需要）**
- 本地存储
```yaml
spring:
  web:
    resources:
      # 示例：windows【file: C:\aiflowy\file】 linux【file: /www/aiflowy/file】
      static-locations: file:your_path
aiflowy: 
  storage:
    local:
      # 示例：windows【C:\aiflowy\file】 linux【file: /www/aiflowy/file】
      root: your_path
      # 后端接口地址
      prefix: 'http://localhost:8080/static'
```
- s3存储
```yml
aiflowy:
  storage:
    type: s3
    s3:
      access-key: access
      secret-key: secret
      endpoint: "http://xxx.xxx"
      region: "region"
      bucket-name: "your_bucket_name"
      access-policy: 2
      prefix: public
```
### 第四步：运行项目

在开始运行 AIFlowy 之前，建议在终端（Terminal）下执行 Maven 编译命令：`mvn clean package`，对项目进行编译，如下图所示：

![mvn.png](resource/mvn.png)

命令执行完毕后，正常情况下会出现编译成功的信息 BUILD SUCCESS，如下图所示：

![mvn_finished.png](resource/mvn_finished.png)
> 若过程中出现错误，有可能是 JDK 版本不正确，或者 Maven 安装不正确等问题，此时可以在 AIFlowy 的交流群里进行交流。

项目编译完成后，运行 `aiflowy-starter` 模块下的 `MainApplication.java` 类，即可。如图：

![run.png](resource/run.png)

若是遇到 `Error running MainApplication. Command line is too long.` 错误，修改一下运行配置，如下图所示：
![shorten.png](resource/shorten.png)

## 运行 AIFlowy 前端部分

### React 版本

在运行前端程序之前，需要您的电脑安装好 Node 环境，注意版本为 v20+ ，我们进入到 `aiflowy-ui-react` 目录下，通过执行 npm install 命令来安装前端所需的依赖。

若在执行 npm install 出现网络问题（400 Bad Request 等错误），可以通过尝试使用如下方案解决：
```shell
# 取消代理设置
npm config set proxy null
npm config set https-proxy null

# 清空缓存
npm cache clean --force

# 设置国内镜像
npm config set registry https://registry.npmmirror.com
```

安装完依赖后，我们通过执行 npm run dev 即可启动前端程序，如下图所示：

![npm_run_dev.png](resource/npm_run_dev.png)

启动完成后，我们通过浏览器访问控制台显示的地址：`http://localhost:8899` 即可访问到 AIFlowy 的程序，如下图所示：

![login_page.png](resource/login_page.png)

> 默认登录账号密码：admin/123456

到此，AIFlowy 正常启动，关于其更多的信息请阅读其他章节文档或者技术交流群里交流。

### Vue 版本

> 敬请期待...