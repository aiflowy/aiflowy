# 插件

## 什么是插件？

在聊天机器人 **Bot** 中，插件（Plugin） 是一种扩展功能模块，允许Bot通过集成第三方工具或服务来增强其能力。插件本质上是为Bot添加新技能的“附加组件”，使其能够完成原本无法独立完成的任务（如实时信息查询、数据库操作、调用外部API等）。

## Bot 怎么调用插件的？ 

在AIFlowy中，所有的插件都是转换成了一个 Function， 将用户的提问和插件的 Function 传递给大模型，大模型理解之后会判断当前用户的问题是否需要调用用户传递的Function 这个插件，
， 如果大模型判断调用这个插件，大模型就会调用这个 Function 查询出插件中将要查询出的数据，然后数据再经过大模型的处理，大模型再返回给用户。

以下完整代码在 **AiBotController** 这个类中，请求地址为 **/api/v1/aiBot/chat**

```java
HumanMessage humanMessage = new HumanMessage(prompt);

        // 添加插件相关的function calling
appendPluginFunctions(botId, humanMessage);

private void appendPluginFunctions(BigInteger botId, HumanMessage humanMessage) {
    QueryWrapper queryWrapper = QueryWrapper.create().eq(AiBotPlugins::getBotId, botId);
    List<AiBotPlugins> aiBotPlugins = aiBotPluginsService.getMapper().selectListWithRelationsByQuery(queryWrapper);
        for (AiBotPlugins aiBotPlugin : aiBotPlugins) {
            Function function = aiBotPlugin.getAiPlugins().toFunction();
            humanMessage.addFunction(function);
        }
}
```

![plugin.png](resource/plugin.png)

## 自定义插件

![add_plugin_first.png](resource/add_plugin_first.png)

![add_plugin_second.png](resource/add_plugin_second.png)

![add_plugin_third.png](resource/add_plugin_third.png)

![add_plugin_four.png](resource/add_plugin_four.png)

![add_plugin_five.png](resource/add_plugin_five.png)

![add_plugin_six.png](resource/add_plugin_six.png)