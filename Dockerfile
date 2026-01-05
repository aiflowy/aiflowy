# Docker image for aiflowy application
# VERSION 2.0.0
# Author: Cennac

### 基础镜像 支持macos arm64
#FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/eclipse-temurin:17-jdk-alpine
#FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/eclipse-temurin:17-jdk
FROM eclipse-temurin:17-jdk

# 作者
MAINTAINER Cennac <cennac@163.com>

# docker build --build-arg VERSION=2.0.3 -t aiflowy:latest .
ARG VERSION=2.0.3
ARG BUILD_DATE=2026-1-5
ARG SERVICE_NAME=aiflowy-starter-all
ARG SERVICE_PORT=8080
ENV VERSION ${VERSION}
ENV SERVICE_NAME ${SERVICE_NAME}
ENV SERVICE_PORT ${SERVICE_PORT}

# 系统编码
ENV LANG=C.UTF-8 LC_ALL=C.UTF-8
# 运行参数
ENV JAVA_OPTS=""

# 声明一个挂载点，容器内此路径会对应宿主机的某个文件夹
VOLUME /tmp

ENV TZ=Asia/Shanghai \
    DEBIAN_FRONTEND=noninteractive

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    sed -i 's/archive.ubuntu.com/mirrors.aliyun.com/g' /etc/apt/sources.list && \
    sed -i 's/security.ubuntu.com/mirrors.aliyun.com/g' /etc/apt/sources.list && \
    apt-get update && \
    apt-get install -y --no-install-recommends fonts-dejavu-core fontconfig && \
    rm -rf /var/lib/apt/lists/*

# 验证
RUN date && fc-list | grep -i dejavu

# 应用构建成功后的jar文件被复制到镜像内，名字也改成了app.jar
COPY ./aiflowy-starter/aiflowy-starter-all/target/${SERVICE_NAME}-*.jar app.jar


# 声明运行时端口
EXPOSE ${SERVICE_PORT}

# 启动容器时的进程
ENTRYPOINT java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -jar /app.jar
