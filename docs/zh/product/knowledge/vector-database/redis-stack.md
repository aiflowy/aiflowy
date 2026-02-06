
---

# Redis 向量数据库使用指南
本文档详细介绍 Redis 向量数据库的快速部署、知识库配置、文件导入及检索测试全流程，适用于基于 Redis Stack 搭建向量检索能力的场景。

## 1. 部署 Redis 向量数据库
Redis 向量数据库依赖 Redis Stack（内置 RedisSearch 模块）实现向量存储与检索能力，推荐使用 Docker 容器化部署，快速完成环境搭建。

### 1.1 部署命令
执行以下 Docker 命令启动 Redis Stack 容器，默认开启 6379 端口并设置密码：
```java
docker run --name  redis_stack  -e REDIS_ARGS="--requirepass Test2025L" -p 6379:6379  -d --restart=always redis/redis-stack-server:latest
```

### 1.2 命令说明
| 参数/选项 | 作用 |
|-----------|------|
| --name redis_stack | 自定义容器名称，便于管理 |
| -e REDIS_ARGS="--requirepass Test2025L" | 设置 Redis 访问密码为 Test2025L |
| -p 6379:6379 | 端口映射，宿主机 6379 端口映射到容器 6379 端口 |
| -d --restart=always | 后台运行容器，且服务器重启时自动启动该容器 |
| redis/redis-stack-server:latest | 使用最新版 Redis Stack 镜像（内置向量检索功能） |

## 2. 知识库页面配置
部署完成后，在系统知识库页面配置 Redis 向量数据库连接信息，步骤如下：

### 2.1 核心配置项
| 配置项     | 取值示例 | 说明 |
|---------|----------|------|
| 向量数据库类型 | Redis | 选择 Redis 作为向量存储介质 |
| 向量数据库配置 | uri = redis://:Test2025L@127.0.0.1:6379 | Redis 连接地址，格式为 `redis://:密码@IP:端口` |
| 向量数据库集合 | redisKnowledge | 自定义集合名称（建议使用英文缩写/命名），用于存储该知识库的向量数据 |
| 向量模型    | （选择大模型菜单中能力为 Embedding 的模型） | 用于将文本转换为向量的模型，需提前配置可用的 Embedding 模型 |

### 2.2 扩展配置（可选）
在「向量数据库配置」中可补充以下参数，优化使用体验：
```properties
# 向量数据存储前缀，便于区分不同业务的向量数据
storePrefix = docs:
# 默认集合名称，配置后新增/编辑知识库时「向量数据库集合」可留空
defaultCollectionName = documents
```

### 2.3 配置示例截图
![create_document_collection_2.png](../resource/create_document_collection_2.png)
