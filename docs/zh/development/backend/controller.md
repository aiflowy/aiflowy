# 控制器 Controller

## 前言

在 Spring 中，Controller 是用来接受 Web 的数据请求的。在 AIFlowy 中，我们开发了一个名称为 `@JsonBody` 的注解，用于增强对 Controller 的接收和解析前端传入的 JSON 数据。

---

## @JsonBody 示例代码

### 示例 1

**前端传入内容如下：**

```json
{
  "myId": 123
}
```

后端 Controller 可以通过如下的方式来接收：

```java
public Result(@JsonBody("myId") int a) {
    System.out.println(a);
}
```

以上代码中，a的值为 123。

```java
public Result(@JsonBody(value="myId",required = true) int a){
System.out.println(a);
}
```

若 JsonBody添加了 required = true的配置，而前端未传入 "myId" 时，则会报错并告知前端必须传入 myId。

示例2
前端传入内容如下：

```json
{
  "myId":123,
  "other":{
    "account":{
      "id":100,
      "name":"michael"
    }
  }
}
```

后端 Controller 可以通过如下的方式来接收：

```Java
public Result(@JsonBody("myId")int a,@JsonBody("other.account")Account account){

    System.out.println(a);
    System.out.println(account);
}
```

以上代码中，a的值为 123。account 的 id 为 100，name 为 “michael”。