## 1. 创建MCP
创建插件请参考 [如何创建一个MCP](/zh/product/mcp/quick-start)
注意：挂载MCP 之前需要MCP处于启动，并且状态是 **绿色** 健康状态，并且可以查看到可用的工具，如下两张图所示
![mount_mcp_1.png](resource/mount_mcp_1.png)
有工具列表，方可挂载MCP
![create_mcp_3.png](../mcp/resource/create_mcp_3.png)
## 2. 挂载MCP
这里我已成成功的创建了一个**和风天气** 的插件，并且已经测试插件能成功返回数据<br/>
进入 **聊天助手** 详情页面，点击 **插件** 右上角的 **+** 按钮，选择我们创建好的 插件**获取城市id**，点击 **选择**， 这样就给我们的智能机器人挂上了插件。
![mount_plugin_tool_1.png](../plugin/resource/mount_plugin_tool_1.png)

## 3. 聊天助手挂载MCP测试
进入聊天助手配置页面，找到**MCP**模块，点击右上角的 **+**，按钮，会弹出选择MCP窗口，此时等待MCP加载工具列表，如果MCP加载失败，请进入MCP配置页面重新启动该MCP服务
![mount_mcp_2.png](resource/mount_mcp_2.png)
这里我们勾选上全部**12306购票综合查询**服务的MCP工具，然后点击**确定**按钮
## 4. 测试
测试成功如下图所示
![mount_mcp_5.png](resource/mount_mcp_5.png)
## 5. MCP调用过程解读
可以看到，我们这里最后调用的是get-tickets工具，
我们可以先查看这个工具具体需要传递哪些参数
![mount_mcp_4.png](resource/mount_mcp_4.png)
根据用户的提问调用工具查询用户想要查询的出发地和到达地的station_code
![mount_mcp_6.png](resource/mount_mcp_6.png)
余票查询
![mount_mcp_7.png](resource/mount_mcp_7.png)
AI整理消息返回结果
![mount_mcp_8.png](resource/mount_mcp_8.png)
最后我们用自己手机查询12306的余票,可以看到当天的车票信息和我们调用MCP查询返回的数据一致
![mount_mcp_9.jpg](resource/mount_mcp_9.jpg)
