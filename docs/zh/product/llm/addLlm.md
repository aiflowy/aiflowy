# 模型接入

## 添加大模型

我们添加一个模型作为示例。

比如，我们要添加一个`模力方舟`的 `deepseek v3` 模型，那么参数如下：

![llmConfig.png](resource/llmConfig.png)

因为`模力方舟`的大模型接口兼容了`openai`，所以我们可以直接使用兼容`openai` 的接口。

但是因为各家厂商的 `chatPath` 不一样，所以需要配置一下。

如果品牌列表支持的厂商，那么只需配置该厂商所需的参数即可。

比如星火大模型，除了 `apiKey`，还需要配置  `appId` 和 `apiSecret`，那么这些额外的参数也是要以 `property` 的形式来配置：
```
appId=xxx
apiSecret=xxx
```
> 需要注意的是，只有 `openai` 方式才支持配置chatPath，其他厂商 `path` 都是固定的。



