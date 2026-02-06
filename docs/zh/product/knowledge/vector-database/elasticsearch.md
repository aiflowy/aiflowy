# Elasticsearch 8.15.0 Docker 安装文档
## 一、安装前准备
### 1. 环境要求
- 已安装 Docker 环境（建议 Docker 版本 ≥ 20.10）
- 服务器至少 2GB 可用内存（ES 默认 JVM 堆内存 1G，需预留系统内存）
- 服务器防火墙开放 9200/9300 端口（或关闭防火墙/配置安全组）

### 2. 核心参数说明（提前理解命令中参数）
| 参数 | 作用 |
|------|------|
| `-d` | 后台运行容器 |
| `--name es-8.15.0` | 定义容器名称，方便管理 |
| `-p 9200:9200/9300:9300` | 端口映射（主机端口:容器端口），9200 为 HTTP 端口，9300 为集群通信端口 |
| `discovery.type=single-node` | 单节点模式（非集群），适合测试/单机部署 |
| `xpack.security.enabled=true` | 开启 ES 安全认证（8.x 默认开启，需手动指定密码） |
| `ELASTIC_PASSWORD=YourPassword` | 设置 elastic 超级用户的密码（需替换为自定义密码） |
| `xpack.security.http.ssl.enabled=false` | 关闭 HTTP 接口 SSL（测试环境简化配置，生产建议开启） |
| `ES_JAVA_OPTS=-Xms1g -Xmx1g` | 设置 JVM 堆内存（建议不超过物理内存的 50%，且不超过 32G） |
| `-v es-latest-data:/usr/share/elasticsearch/data` | 数据卷挂载（持久化 ES 数据，避免容器删除后数据丢失） |
| `--restart=always` | 容器随 Docker 启动自动重启 |

## 二、安装步骤
### 1. 执行安装命令
将命令中的 `YourPassword` 替换为你自定义的密码（建议包含大小写、数字、特殊字符），然后执行：
```java
docker run -d --name es-8.15.0 -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" -e "xpack.security.enabled=true" -e "ELASTIC_PASSWORD=YourPassword" -e "xpack.security.http.ssl.enabled=false" -e "xpack.security.transport.ssl.enabled=false" -e "xpack.security.enrollment.enabled=false" -e "ES_JAVA_OPTS=-Xms1g -Xmx1g" -v es-latest-data:/usr/share/elasticsearch/data --restart=always elasticsearch:8.15.0
```

### 2. 检查容器运行状态
执行以下命令，查看容器是否正常启动（状态为 `Up` 则表示成功）：
```bash
docker ps | grep es-8.15.0
```
若状态为 `Exited`，执行 `docker logs es-8.15.0` 查看报错信息（常见原因：内存不足、端口被占用）。

### 3. 验证 ES 服务可用性
#### 方式 1：curl 命令验证（服务器本地）
```bash
# 替换为你的密码
curl -u elastic:你的自定义密码 http://localhost:9200
```
正常返回示例（包含版本、名称等信息）：
```json
{
  "name" : "xxxxxx",
  "cluster_name" : "docker-cluster",
  "cluster_uuid" : "xxxxxx",
  "version" : {
    "number" : "8.15.0",
    "build_flavor" : "default",
    "build_type" : "docker",
    ...
  },
  "tagline" : "You Know, for Search"
}
```

#### 方式 2：浏览器访问（远程）
在浏览器输入 `http://服务器IP:9200`，会弹出登录框，输入用户名 `elastic` 和你设置的密码，登录后即可看到上述 JSON 信息。

## 三、常用运维命令
### 1. 启动/停止/重启容器
```bash
# 停止
docker stop es-8.15.0
# 启动
docker start es-8.15.0
# 重启
docker restart es-8.15.0
```

### 2. 查看 ES 日志
```bash
# 实时查看日志
docker logs -f es-8.15.0
# 查看最后100行日志
docker logs --tail=100 es-8.15.0
```

### 3. 进入容器内部
```bash
docker exec -it es-8.15.0 /bin/bash
```

### 4. 删除容器（谨慎操作，数据卷需单独删除）
```bash
# 先停止容器
docker stop es-8.15.0
# 删除容器
docker rm es-8.15.0
# 如需删除数据卷（数据会丢失）
docker volume rm es-latest-data
```

## 四、常见问题解决
### 1. 容器启动失败，日志提示内存不足
- 解决方案：调整 `ES_JAVA_OPTS` 参数，降低堆内存（如 `-Xms512m -Xmx512m`），或增加服务器内存。

### 2. 9200/9300 端口被占用
- 查看占用端口的进程：`netstat -tulpn | grep 9200`
- 解决方案：要么停止占用进程，要么修改端口映射（如 `-p 9201:9200`）。

### 3. 访问 ES 时提示认证失败
- 检查密码是否输入正确；
- 若忘记密码，进入容器重置：
  ```bash
  docker exec -it es-8.15.0 /bin/bash
  # 执行重置命令，按提示输入新密码
  bin/elasticsearch-reset-password -u elastic
  ```

### 4. 数据卷挂载权限问题
- 日志提示 `permission denied`，执行以下命令赋予权限：
  ```bash
  docker exec -it es-8.15.0 chmod 777 /usr/share/elasticsearch/data
  ```

---

### 总结
1. 核心安装命令需替换 `ELASTIC_PASSWORD` 为自定义密码，确保端口、内存参数适配服务器环境；
2. 验证服务可用性的关键是通过 `curl` 或浏览器访问 9200 端口，能正常返回 JSON 且认证通过即为安装成功；
3. 数据卷 `-v es-latest-data:/usr/share/elasticsearch/data` 是数据持久化的核心，删除容器前需确认是否保留数据卷。