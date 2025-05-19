# API Key

apiKey 的作用是 用于身份认证，通过 apiKey 第三方可以携带对应的参数访问到 AIFlowy 对应的Bot进行对话。

## 创建 apiKey
- 在系统管理的`apiKey`界面，通过“新增”按钮，创建 `apiKey`。
![addApiKeyButton.png](resource/addApiKeyButton.png)
- 在弹出的确认框中点击确认，即可创建 `apiKey`
![addApiKey.png](resource/addApiKey.png)
## 通过 apiKey 调用 bot
### 第三方接入 Bot 聊天地址：
请求方式： POST
请求地址： http://127.0.0.1:8080/api/v1/aiBot/externalChat

### 默认请求方式以 JSON格式返回
请求头：

Headers: {
Authorization: apiKey
}

请求格式参数说明：
```
messages： 消息体
botId： 第三方想要请求的 BotId
```

请求示例：
```json
{   
    "messages": [

        {
            "role": "user",
            "content": "你好"
        },
          {
            "role": "assistant",
            "content": "你好我是科大讯飞模型"
        },
         {
            "role": "user",
            "content": "帮我翻译 what's your name ?"
        }
    ],
    "botId": "267848016181075968"
   
    
}
```



响应参数说明：

```
status: END 表示本次对话正常结束
created： 创建时间
message： 消息体

```

请求成功示例：
```json
{
    "status": "END",
    "usage": {
        "completionTokens": 3,
        "promptTokens": 22,
        "totalTokens": 25
    },
    "created": 1745205152874,
    "choices": {
        "index": 0,
        "message": {
            "content": "你叫什么名字？",
            "role": "assistant"
        }
    }
}
```

### stream 格式返回

参数说明：

```
stream: true 表示以流式返回
```

请求示例：
```json
{   
    "stream": true,
    "messages": [

        {
            "role": "user",
            "content": "你好"
        },
          {
            "role": "assistant",
            "content": "你好我是科大讯飞模型"
        },
         {
            "role": "user",
            "content": "你叫什么名字?"
        }
    ],
    "botId": "267848016181075968"
   
    
}
```

返回参数说明：
```
status: START 表示本次对话开始
status: MIDDLE 表示本次对话进行中
status: END 表示本次对话正常结束
```

请求成功示例：

```json
{
    "status": "START",
    "created": 1745205750472,
    "choices": {
        "delta": {
            "content": "我的名字叫讯",
            "role": "assistant"
        },
        "index": 0
    }
}

```

```json
{
    "status": "MIDDLE",
    "created": 1745205750750,
    "choices": {
        "delta": {
            "content": "认知大模型，很高兴",
            "role": "assistant"
        },
        "index": 0
    }
}
```

```json
{
    "status": "END",
    "created": 1745205750901,
    "choices": {
        "delta": {
            "content": "为你服务。",
            "role": "assistant"
        },
        "index": 0
    }
}
```


## 通过 apiKey 调用工作流