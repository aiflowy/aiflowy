# OpenSearch 向量数据库

## 1. 安装OpenSearch

docker 安装参考地址

https://docs.opensearch.org/docs/latest/install-and-configure/install-opensearch/docker/#windows-settings

docker 使用如下如下命令一键设置密码并安装

```cmd
docker run -d -p 9200:9200 -p 9600:9600 -e "discovery.type=single-node" -e "OPENSEARCH_INITIAL_ADMIN_PASSWORD=yourPassword" opensearchproject/opensearch:2.19.1
```

浏览器向 9200 端口发送请求。默认用户名为 admin ，密码为您设置的密码。

出现以下内容则表示成功

![openSearch_2.png](../resource/openSearch_2.png)

## 2. OpenSearch 知识库配置样例

![openSearch_1.png](../resource/openSearch_1.png)
