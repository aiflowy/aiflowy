![banner.png](docs/assets/images/banner.png)

# AIFlowy：企业级 AI 智能体开发平台

AIFlowy 是一个基于 Java 语言构建的**企业级开源 AI 应用（智能体）开发平台**，致力于为中国开发者与政企用户提供**高效、开放、本土化**的 AI 工具链与低门槛落地能力。

我们对标 Coze、Dify、腾讯元器等国际与国内主流产品，但在架构设计、功能模块与场景适配方面，**更聚焦于中国 ToB 市场的深度需求与合规实践**，支持从 Bot 构建、RAG 知识库、AI 工作流编排到模型管理的完整 AI 应用生命周期。



## ✨ AIFlowy 核心功能

### 🧠 AI 能力
- ✅ 智能 Bot 构建与发布
- ✅ 插件系统（支持自定义工具集成）
- ✅ RAG 知识库（支持文档上传、解析与检索）
- ✅ AI Workflow 编排（可视化工作流 + 条件/分支/循环）
- ✅ 素材中心（AI 自动生成图片、音频、视频等）
- ✅ 数据中枢（自定义数据表 + 工作流读写能力）
- ✅ 多模型管理（统一接入主流大模型）
- ✅ MCP 接入、执行、挂载到聊天助手、并自动调用


### 🔐 系统管理
- ✅ 用户 / 角色 / 部门 / 岗位 / 权限体系
- ✅ 菜单与系统配置
- ✅ 访问令牌（API Key）
- ✅ 定时任务 & 操作日志
- ✅ 用户反馈 & 外观定制
- ✅ 认证方式配置（支持多种登录策略）
- ✅ 国际化支持（目前已经完整支持中文和英文）


### 🔜 近期路线图
- [ ] 增强 RAG 检索精度与多模态支持
- [ ] 优化前端交互体验与性能
- [ ] 完善开发者文档与 SDK 生态

## 技术栈
- **后端**：JDK 17 + SpringBoot v3 + Agents Flex + MyBatis Flex + Redis  + Quartz
- **前端**：Vue 3 + pnpm +  Element Plus + Vue Router

## 后台管理截图

| 模块 | 截图 |
|------|------|
| 登录页 | ![login.png](docs/assets/images/login.png) |
| Bot 配置 | ![bot1.png](docs/assets/images/bot1.png) |
| 插件中心 | ![cj.png](docs/assets/images/cj.png) |
| 工作流编排 | ![gzl1.png](docs/assets/images/gzl1.png) |
| 知识库管理 | ![rag.png](docs/assets/images/rag.png) |
| 素材中心 | ![sck.png](docs/assets/images/sck.png) |
| 大模型管理 | ![llm.png](docs/assets/images/llm.png) |


## 用户中心截图


| 模块      | 截图                                                           |
|---------|--------------------------------------------------------------|
| 登录页     | ![login.png](docs/assets/images/usercenter/login.png)        |
| 对话      | ![login.png](docs/assets/images/usercenter/chat.png)         |
| 应用市场    | ![login.png](docs/assets/images/usercenter/market.png)       |
| 应用详情    | ![login.png](docs/assets/images/usercenter/market2.png)      |
| 智能体     | ![login.png](docs/assets/images/usercenter/agent.png)        |
| 智能体执行记录 | ![login.png](docs/assets/images/usercenter/agent_record.png) |


## 🚀 快速启动

```bash
# 克隆项目
git clone https://gitee.com/aiflowy/aiflowy.git
cd aiflowy

# 构建后端
mvn clean package

# 启动前端（React + pnpm）
cd aiflowy-ui-admin
pnpm install
pnpm dev
```

> 默认账号：`admin` / 密码：`123456`  
> 📚 **详细部署与开发指南**：[https://aiflowy.tech/zh/development/getting-started/getting-started.html](https://aiflowy.tech/zh/development/getting-started/getting-started.html)



## 📚 文档中心

完整产品与开发文档请访问：  

👉 [https://aiflowy.tech](https://aiflowy.tech)


## Star 用户专属交流群

![wechat-group.jpg](docs/assets/images/wechat-group.jpg)

> 🌟 **Star 本项目后，截图私信微信 `fuh99777`，加入 AIFlowy 技术交流群**，与核心开发者和企业用户共同探讨 AI 智能体落地场景、架构设计与最佳实践。



## 🏢 关于我们

AIFlowy 由一支深耕 AI 工程化与 Java 生态的技术团队打造。在 AIFlowy 之前，我们已成功开源多个广受社区认可的项目：

- **[Agents-Flex](https://gitee.com/agents-flex/agents-flex)**：轻量级 Java AI Agent 框架
- **[Tinyflow](https://gitee.com/tinyflow-ai/tinyflow)**：低代码 AI 工作流引擎
- **[AIEditor](https://gitee.com/aieditor-team/aieditor)**：开源 AI 内容编辑器

这些项目不仅被广泛应用于企业生产环境，还荣获 **Gitee GVP（最有价值开源项目）** 荣誉，体现了我们在 AI 基础设施领域的技术积累与工程能力。


## 🎯 愿景 · 使命 · 价值观

### Vision 愿景
> 成为中国最具影响力的人工智能品牌之一，引领核心技术自主创新，推动 AI 生态繁荣与科技自立。

### Mission 使命
> 为中国开发者与政企用户提供高效、开放、本土化的 AI 开发工具与解决方案，降低 AI 应用门槛，加速产业智能化。

### Values 价值观
- **专注务实**：聚焦垂直场景，打磨极致体验
- **自主创新**：构建自主可控的国产 AI 工具链
- **开放共享**：拥抱开源，共建开发者生态
- **责任担当**：坚持长期主义，践行技术理想


## ⚖️ 开源协议与使用声明

AIFlowy 采用 **Apache License 2.0** 开源协议发布，并附加以下**品牌使用条款**：

> 1. **不得删除、修改或隐藏** 产品中的 LOGO、版权信息、品牌标识及控制台署名；
> 2. 所有衍生版本或分发版本**必须完整保留**原始版权声明、LICENSE 文件及本附加条款；
> 3. 在遵守上述条款及国家法律法规的前提下，**允许用于商业用途**（包括企业内部使用、产品集成、SaaS 服务等）。
> 
> ⚠️ **特别提示**：  
> 您对 AIFlowy 的任何使用行为（下载、部署、修改、分发等）即视为您**已充分理解并接受**本协议及其附加条款。如不同意，请立即停止使用并删除所有相关资源。

我们坚定支持开源精神，同时也**尊重并保护项目品牌与开发者劳动成果**。对于恶意去标识、冒用品牌或违反协议的行为，我们将依法维权。


**🌟 欢迎 Star、Fork、贡献代码，一起打造中国人自己的 AI 智能体平台！**
