Here is the English translation of your document:


![banner.png](docs/assets/images/banner_en.png)

# AIFlowy: Enterprise-Grade AI Agent Development Platform


AIFlowy is an **enterprise-grade, open-source AI application (agent) development platform** built with Java, designed to empower developers and organizations with an **efficient, open, and locally adaptable** AI toolchain and low-barrier capabilities for deploying AI solutions.

While AIFlowy competes with mainstream platforms such as Coze, and Dify, it distinguishes itself through a **strong focus on real-world enterprise needs and regulatory considerations**—reflected in its architecture, feature set, and scenario-driven design. AIFlowy supports the full lifecycle of AI applications, from bot creation and RAG knowledge bases to AI workflow orchestration and model management.


## ✨ AIFlowy Core Features

### 🧠 AI Capabilities
- ✅ Intelligent Bot Building and Deployment
- ✅ Plugin System (Supports custom tool integration)
- ✅ RAG Knowledge Base (Supports document upload, parsing, and retrieval)
- ✅ AI Workflow Orchestration (Visual workflow + conditions/branches/loops)
- ✅ Media Center (AI-generated images, audio, videos, etc.)
- ✅ Data Hub (Custom data tables + workflow read/write capabilities)
- ✅ Multi-Model Management (Unified access to mainstream large language models)
- ✅ MCP integration, execution, mounting to chat assistant, and automatic invocation


### 🔐 System Management
- ✅ User/Role/Department/Position/Permission System
- ✅ Menu and System Configuration
- ✅ Access Tokens (API Keys)
- ✅ Scheduled Tasks & Operation Logs
- ✅ User Feedback & Appearance Customization
- ✅ Authentication Method Configuration (Supports multiple login strategies)
- ✅ Internationalization Support (Currently fully supports Chinese and English)


### 🔜 Upcoming Roadmap
- [ ] Enhanced RAG retrieval accuracy and multimodal support
- [ ] Improved frontend interaction experience and performance
- [ ] Comprehensive developer documentation and SDK ecosystem


## Tech Stack
- **Backend**: JDK 17 + Spring Boot v3 + Agents-Flex + MyBatis-Flex + Redis + Quartz
- **Frontend**: Vue 3 + pnpm + Element Plus + Vue Router


## Admin Console Screenshots

| Module               | Screenshot |
|----------------------|------------|
| Login Page           | ![login.png](docs/assets/images/login.png) |
| Bot Configuration    | ![bot1.png](docs/assets/images/bot1.png) |
| Plugin Center        | ![cj.png](docs/assets/images/cj.png) |
| Workflow Orchestration | ![gzl1.png](docs/assets/images/gzl1.png) |
| Knowledge Base       | ![rag.png](docs/assets/images/rag.png) |
| Media Center         | ![sck.png](docs/assets/images/sck.png) |
| LLM Management       | ![llm.png](docs/assets/images/llm.png) |


## User Center Screenshots

| Module              | Screenshot |
|---------------------|------------|
| Login Page          | ![login.png](docs/assets/images/usercenter/login.png) |
| Chat                | ![login.png](docs/assets/images/usercenter/chat.png) |
| App Marketplace     | ![login.png](docs/assets/images/usercenter/market.png) |
| App Details         | ![login.png](docs/assets/images/usercenter/market2.png) |
| AI Agent            | ![login.png](docs/assets/images/usercenter/agent.png) |
| Agent Execution Log | ![login.png](docs/assets/images/usercenter/agent_record.png) |


## 🚀 Quick Start

```bash
# Clone the repository
git clone https://gitee.com/aiflowy/aiflowy.git  
cd aiflowy

# Build the backend
mvn clean package

# Start the frontend (Vue + pnpm)
cd aiflowy-ui-admin
pnpm install
pnpm dev
```

> Default credentials: `admin` / `123456`  
> 📚 **Detailed deployment & development guide**: [https://aiflowy.tech/zh/development/getting-started/getting-started.html](https://aiflowy.tech/zh/development/getting-started/getting-started.html)


## 📚 Documentation Center

Full product and development documentation is available at:  
👉 [https://aiflowy.tech](https://aiflowy.tech)



## 🏢 About Us

AIFlowy is developed by a team deeply experienced in AI engineering and the Java ecosystem. Prior to AIFlowy, we’ve successfully open-sourced several widely recognized community projects:

- **[Agents-Flex](https://gitee.com/agents-flex/agents-flex)**: Lightweight Java AI Agent framework
- **[Tinyflow](https://gitee.com/tinyflow-ai/tinyflow)**: Low-code AI workflow engine
- **[AIEditor](https://gitee.com/aieditor-team/aieditor)**: Open-source AI content editor

These projects are widely used in enterprise production environments and have been awarded **Gitee GVP (Gitee Most Valuable Open Source Project)**, reflecting our strong technical foundation and engineering capabilities in AI infrastructure.


## ⚖️ License and Usage Terms

AIFlowy is released under the **Apache License 2.0**, with the following **additional brand usage terms**:

> 1. You **must not remove, alter, or obscure** any logos, copyright notices, brand identifiers, or console attributions in the product.
> 2. All derivative or redistributed versions **must fully retain** the original copyright notice, LICENSE file, and these additional terms.
> 3. **Commercial use is permitted** (including internal enterprise use, product integration, SaaS offerings, etc.), provided the above terms and applicable laws are followed.
>
> ⚠️ **Important Notice**:  
> Any use of AIFlowy (including downloading, deploying, modifying, or distributing) constitutes your **full understanding and acceptance** of this license and its附加条款. If you do not agree, please immediately cease use and delete all related materials.

We strongly support the open-source spirit while also **respecting and protecting the project’s brand and the developers’ efforts**. We reserve the right to pursue legal action against malicious de-branding, unauthorized branding, or license violations.
