# 项目部署

## 后端项目编译

编译后端程序，需要您配置好 java 以及 maven 环境，然后进入到项目的根目录，执行 `mvn clean package` 即可编译。

编译完成后，在 `aiflowy-starter/target` 下会生成名称为 `aiflowy-starter-x.x.x.jar` 的文件。
我们复制该文件到服务器，即可通过 java -jar 命令进行启动。

## 后端项目部署配置

在项目编译成 jar，并进行部署时，需要注意以下内容：

### 1、取消 Log 的日志打印

启动时，注意添加 `--spring.profiles.active=prod` 配置。

```shell
java -jar aiflowy-starter-x.x.x.jar --spring.profiles.active=prod
```

### 2、配置文件上传路径

**本地储存**
```yml
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
**s3存储**
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
## 前端项目编译

我们进入到项目的 `aiflowy-ui-react` 目录，修改 `.env.production` 文件下的 `VITE_APP_SERVER_ENDPOINT` 为你的后端项目的访问域名，如下图所示：

![server_endpoint.png](resource/server_endpoint.png)

修改完毕后，在 `aiflowy-ui-react` 目录下执行 `npm run build` 命令，即可在 `aiflowy-ui-react/dist` 目录下生成可以部署的静态文件。

我们复制静态文件到 nginx 目录下即可运行。 或者亦可以直接使用阿里云 OSS 来部署我们的静态文件内容。

## 前端部署到 Nginx

> 需先安装 nginx

 Nginx 配置：
```shell
server {
   ...
   
   location / {
      root /www/aiflowy/page;
      try_files $uri $uri/ /index.html;
    }

    # 本地文件访问，如果不是本地存储可以不配
    location /attachment {
      # 转发到后端
	  proxy_pass http://127.0.0.1:8080/static/attachment ;
	  # 或者可以直接转发到存储的目录
    }
    
    ...
}
```
以上示例中，`/www/aiflowy/page` 目录用来存放上面编译好的 `aiflowy-ui-react/dist` 目录下的所有文件。

## 前端部署到阿里云 OSS

通过阿里云 OSS 部署前端应用，是一种可选的方案，作者已经通过这种方式运行多年，不需要额外的搭建前端服务器，而且还可以使用 OSS CDN 进行分发，加快静态资源访问速度。

我们在阿里云 OSS Bucket 列表中 https://oss.console.aliyun.com/bucket 创建一个新的 Bucket。

![ali_bucket.png](resource/ali_bucket.png)

在创建 Bucket 时，其读写权限一定要选择 “公共读”，其他保存默认即可，如下图所示：

![ali_bucket_read_public.png](resource/ali_bucket_read_public.png)

开通完成后，我们进入 Bucket 设置，在 “静态页面” 中开通静态页面加载功能，在域名管理中绑定自己的访问域名，最后在文件列表中，上传 dist 目录下的文件即可。

![ali_bucket_set.png](resource/ali_bucket_set.png)




