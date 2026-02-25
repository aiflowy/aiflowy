# MCP 配置详情


## 1. 前置环境要求

在配置 MCP 服务之前，请确保宿主机器满足以下基础环境要求，否则服务将无法启动。

### 1.1 核心依赖
- **Node.js**: 建议版本 v18+ (需包含 `npm`, `npx`)
- **Python**: 建议版本 v3.10+ (如需运行 Python 类服务)
- **环境变量**: 确保 `node`, `npm`, `npx`, `python` 可在终端全局调用。

### 1.2 宝塔面板特别配置 (Linux)
如果您在宝塔面板安装 Node.js，通常需要手动创建全局软链接，否则 MCP 无法找到命令。

```bash
# 请将 v24.11.1 替换为您实际安装的版本号
VER="v24.11.1" 

# 1. 创建全局软链接
ln -sf /www/server/nodejs/$VER/bin/node /usr/bin/node
ln -sf /www/server/nodejs/$VER/bin/npm /usr/bin/npm
ln -sf /www/server/nodejs/$VER/bin/npx /usr/bin/npx

# 2. 赋予执行权限
chmod +x /usr/bin/node /usr/bin/npm /usr/bin/npx

# 3. 验证环境 (必须均输出版本号)
node -v && npm -v && npx -v
```


## 2. 配置文件结构

MCP 配置文件通常为 **JSON** 格式。根对象包含 `mcpServers` 字段，每个子项代表一个服务实例。

### 2.1 标准结构
```JSON
{
  "mcpServers": {
    "<服务唯一标识>": {
      "command": "<启动命令>",
      "args": ["<参数 1>", "<参数 2>"],
      "env": {
        "<环境变量名>": "<环境变量值>"
      },
      "cwd": "<工作目录>",
      "type": "<传输类型>",
      "url": "<远程地址>",
      "headers": { ... }
    }
  }
}
```

### 2.2 核心参数详解

| 参数名 | 类型 | 必填 | 适用模式 | 说明 |
| :--- | :--- | :--- | :--- | :--- |
| **command** | String | ✅ (STDIO) | STDIO | 启动命令 (如 `node`, `npx`, `python`, `docker`)。 |
| **args** | Array | ❌ | STDIO | 传递给命令的参数列表。 |
| **env** | Object | ❌ | 全部 | 环境变量。**敏感信息 (API Key) 请在此配置**。 |
| **cwd** | String | ❌ | STDIO | 命令执行的工作目录。 |
| **type** | String | ❌ | 远程 | 传输协议类型 (`sse`, `http`)。部分客户端可自动识别。 |
| **url** | String | ✅ (远程) | 远程 | 远程服务地址 (如 `http://localhost:8080/sse`)。 |
| **headers** | Object | ❌ | 远程 | HTTP 请求头，常用于携带认证 Token。 |




## 3. 传输协议模式详解

MCP 支持三种主要传输模式，请根据部署场景选择。

### 3.1 STDIO 模式 (本地进程)
**适用场景**：服务与客户端在同一台机器，追求最低延迟，配置最简单。
**原理**：通过标准输入输出 (stdin/stdout) 进行通信。

```JSON
{
  "mcpServers": {
    "local-files": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-filesystem", "/data"]
    }
  }
}
```
> **⚠️ 注意**：
> - **Linux/Mac**: 命令使用 `npx`
> - **Windows**: 命令使用 `npx.cmd`

### 3.2 SSE 模式 (Server-Sent Events)
**适用场景**：服务部署在远程服务器、Docker 容器或独立进程中。
**原理**：客户端通过 HTTP 长连接接收事件，通过 POST 发送消息。

```JSON
{
  "mcpServers": {
    "remote-weather": {
      "type": "http-sse", // 或者  “ssehttp” 或者 "sse"
      "url": "http://192.168.1.100:8080/sse",
      "headers": {
        "Authorization": "Bearer YOUR_TOKEN"
      }
    }
  }
}
```

### 3.3 Streamable HTTP 模式 (流式 HTTP)
**适用场景**：高性能远程调用，双向流式通信，新兴标准。
**原理**：基于 HTTP 协议的双向流，效率优于传统 SSE。

```JSON
{
  "mcpServers": {
    "stream-service": {
      "type": "http-stream", // 或者 "streamablehttp" 或者 "http" 
      "url": "http://192.168.1.100:8080/mcp"
    }
  }
}
```

### 3.4 混合配置示例
您可以在一个文件中同时配置本地工具和远程服务。

```JSON
{
  "mcpServers": {
    "local-files": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-filesystem", "/home/docs"]
    },
    "remote-db": {
      "type": "http-sse",
      "url": "http://10.0.0.5:3000/sse"
    },
    "cloud-tool": {
      "type": "http-stream",
      "url": "https://api.cloud.com/mcp"
    }
  }
}
```

## 4. 常见场景配置模板

### 4.1 运行 NPX 包 (最常用)
```JSON
{
  "mcpServers": {
    "memory": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-memory"]
    }
  }
}
```

### 4.2 运行 Python 脚本
```JSON
{
  "mcpServers": {
    "python-bot": {
      "command": "python",
      "args": ["-m", "my_mcp_server"],
      "env": {
        "API_KEY": "sk-xxxx",
        "DEBUG": "true"
      }
    }
  }
}
```

### 4.3 运行 Docker 容器
```JSON
{
  "mcpServers": {
    "docker-mcp": {
      "command": "docker",
      "args": [
        "run", "-i", "--rm",
        "-e", "API_KEY=xxxx",
        "mcp/image-name:latest"
      ]
    }
  }
}
```


## 5. 安全与网络最佳实践

### 5.1 敏感信息管理
- ✅ **推荐**：使用 `env` 字段注入 API Key。
- ✅ **推荐**：生产环境使用 `.env` 文件加载变量。
- ❌ **禁止**：将密码硬编码在 `args` 中（可能泄露于进程列表）。
- ❌ **禁止**：将含密钥的配置文件提交至公共 Git 仓库。

### 5.2 文件系统权限
配置文件系统服务时，务必限制访问目录，避免开放根目录。
```JSON
// ✅ 安全
"args": ["/home/user/project/data"]
// ❌ 危险
"args": ["/"]
```

### 5.3 远程服务网络安全 (SSE/HTTP)
- **HTTPS**: 生产环境务必使用 `https://` 加密传输。
- **防火墙**: 确保客户端能访问服务端端口 (如 8080, 3000)。
- **Nginx 反向代理**: 若使用 Nginx 代理 SSE 服务，需关闭缓冲。
  ```nginx
  location /sse {
      proxy_pass http://localhost:8080/sse;
      proxy_http_version 1.1;
      proxy_set_header Connection "";
      proxy_buffering off;  # 关键配置
      proxy_cache off;
  }
  ```


## 6. 调试与故障排查

### 6.1 连通性测试
在配置到客户端前，先用命令行测试服务端。
```bash
# 测试 STDIO 命令是否可执行
npx -y 12306-mcp --help

# 测试 SSE 端点 (应挂起等待事件)
curl -v http://192.168.1.100:8080/sse

# 测试 HTTP 端点
curl -X POST http://192.168.1.100:8080/mcp -H "Content-Type: application/json" -d '{}'
```

### 6.2 常见错误代码表

| 错误现象 | 可能原因 | 解决方案 |
| :--- | :--- | :--- |
| `Command not found` | 环境变量未配置 | 检查 PATH，参考本文第 1 节配置软链接。 |
| `Connection refused` | 端口未开放/服务未起 | 检查服务端进程，检查防火墙/安全组。 |
| `401 Unauthorized` | 认证失败 | 检查 `headers` 中的 Token 是否正确。 |
| `Permission denied` | 文件无执行权限 | 运行 `chmod +x` 赋予权限。 |
| `Exit Code 1` | 程序内部报错 | 查看服务端日志，通常缺少必要 `env` 参数。 |
| `Unknown transport` | 客户端不支持协议 | 确认客户端版本是否支持 `sse` 或 `http`。 |

### 6.3 日志查看
- **服务端**: 查看启动控制台输出或 Docker 日志 (`docker logs`)。
- **客户端**: 在 MCP 管理界面查看“运行日志”或“连接日志”。


## 7. 客户端兼容性说明

不同 MCP 客户端对配置 JSON 的解析存在差异：

| 客户端 | 配置特点 | 注意事项 |
| :--- | :--- | :--- |
| **Claude Desktop** | 支持 `command` 和 `url` | 通常只需填 `url` 即自动识别为 SSE，无需显式 `type`。 |
| **VS Code 插件** | 支持显式 `type` | 建议显式声明 `"type": "sse"` 或 `"stdio"`。 |
| **自定义开发** | 基于 SDK | 需实例化对应的 Transport 类 (`SSEClientTransport` 等)。 |

> **提示**：任何配置修改后，请**重启客户端**或**重新连接服务**以生效。


## 8. 附录：推荐 MCP 服务包

| 服务名称 | 命令/包名 | 功能描述 |
| :--- | :--- | :--- |
| **文件系统** | `@modelcontextprotocol/server-filesystem` | 读取/写入指定目录文件 |
| **Git 操作** | `@modelcontextprotocol/server-git` | 执行 git 命令、查看历史 |
| **数据库** | `@modelcontextprotocol/server-postgres` | 连接 PostgreSQL 查询 |
| **记忆服务** | `@modelcontextprotocol/server-memory` | 提供长期记忆存储 |
| **12306 查询** | `12306-mcp` | 购票综合查询 (示例) |

## 9. MCP 社区
- [GitHub](https://github.com/modelcontextprotocol)
- [MCP Servers](https://mcpservers.org/)
- [MCP World](https://www.mcpworld.com/)