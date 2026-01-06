# 项目目录结构

AIFlowy 采用模块化设计，前后端分离架构，便于扩展与维护。以下是完整的项目结构说明。

## 📁 后端结构（Java）

### 🧩 核心模块分层

```
aiflowy-api/                # 接口定义层（按业务域拆分）
├── aiflowy-api-admin        # 后台管理 API（受权限保护）
├── aiflowy-api-mcp          # MCP（Model Control Protocol）服务 API
├── aiflowy-api-public       # 公开 API（供第三方或前端调用）
├── aiflowy-api-usercenter   # 用户中心 API
└── pom.xml
```

```
aiflowy-commons/            # 通用工具与共享组件
├── aiflowy-common-ai            # AI 相关通用能力（如 LLM 工具封装）
├── aiflowy-common-all           # 聚合所有 commons 子模块（用于 starter）
├── aiflowy-common-audio         # 音频处理支持
├── aiflowy-common-base          # 基础工具类（日期、字符串、加密等）
├── aiflowy-common-captcha       # 图形/滑块验证码
├── aiflowy-common-cache         # 缓存抽象（支持 Redis 等）
├── aiflowy-common-chat-protocol # AIFlowy 对话协议模型定义（与 aiflowy-chat-protocol.md 对应）
├── aiflowy-common-file-storage  # 文件存储抽象（本地 / S3 / MinIO）
├── aiflowy-common-options       # 系统配置读取与管理
├── aiflowy-common-satoken       # Sa-Token 认证集成
├── aiflowy-common-sms           # 短信服务封装
├── aiflowy-common-web           # Web 层通用组件（拦截器、异常处理器、响应封装等）
└── pom.xml
```

```
aiflowy-modules/            # 业务功能实现模块
├── aiflowy-module-ai            # AI 核心逻辑（智能体、流程编排等）
├── aiflowy-module-autoconfig    # Spring Boot 自动配置
├── aiflowy-module-datacenter    # 数据中心（知识库、向量存储等）
├── aiflowy-module-job           # 异步任务与调度
├── aiflowy-module-log           # 操作日志与审计
├── aiflowy-module-system        # 系统管理（用户、角色、菜单、权限）
└── pom.xml
```

### 🚀 启动入口（Starter）

```
aiflowy-starter/            # 应用启动模块（按场景组合）
├── aiflowy-starter-admin        # 仅启动后台管理服务
├── aiflowy-starter-all          # 启动全部功能（开发/测试环境推荐）
├── aiflowy-starter-codegen      # 代码生成器（基于 MyBatis-Flex）
├── aiflowy-starter-public       # 仅启动公开 API 服务
├── aiflowy-starter-usercenter   # 仅启动用户中心服务
└── pom.xml
```

> 💡 后端使用 **Spring Boot 3.x + Agents-Flex + MyBatis-Flex**，模块间通过 Maven 依赖解耦，支持按需组合部署。



## 🌐 前端结构（Vue 3 + TypeScript）

```
aiflowy-ui-admin/           # 后台管理系统（基于 Element Plus）
├── app/                     # 核心业务代码（pages, components, stores 等）
├── package.json
└── packages/                # 内部共享 UI 组件或业务包
```

```
aiflowy-ui-usercenter/      # 用户中心前端
├── app/
├── package.json
└── packages/
```

```
aiflowy-ui-websdk/          # Web 嵌入式 SDK（供第三方网站集成对话能力）
├── src/                     # SDK 源码（Vue + TypeScript）
├── public/                  # 静态资源
├── index.html
├── package.json
├── tsconfig.json
└── readme.md                # SDK 使用说明
```

> 💡 前端统一使用 **pnpm** 管理依赖，支持 Monorepo 风格开发。



## 📚 文档与资源

```
docs/                       # 项目文档（基于静态站点生成器，如 VitePress）
├── zh/                      # 中文文档
├── assets/                  # 文档图片等资源
├── index.md                 # 文档首页
└── package.json
```

```
sql/                        # 数据库初始化脚本
├── aiflowy-v2.ddl.sql       # 建表语句（含注释）
└── aiflowy-v2.data.sql      # 初始数据（管理员账号、系统配置等）
```

```
aiflowy-chat-protocol.md    # AIFlowy AI 对话传输协议规范（JSON Schema + 字段说明）
```



## 🧭 快速定位建议

| 场景          | 推荐入口 |
|-------------|--|
| 本地全量开发      | `aiflowy-starter-all` + `aiflowy-ui-admin` |
| 仅调试公开 API   | `aiflowy-starter-public` |
| 集成对话能力      | `aiflowy-ui-websdk` + `aiflowy-api-public` |
| 扩展 AI 能力    | `aiflowy-module-ai` + `aiflowy-common-ai` |



此结构设计遵循 **高内聚、低耦合** 原则，便于团队协作、功能扩展与多环境部署。如需自定义模块组合，请参考各 `pom.xml` 依赖声明。