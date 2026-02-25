# MCP 快速开始

## 1. 环境 prerequisites
启动 MCP 服务前，请确保服务器已安装以下基础环境，并正确配置了全局环境变量：
- **Node.js** (含 npm, npx)
- **Python**
- **环境变量**：确保 `node`, `npm`, `npx` 命令可在终端直接调用。

> **⚠️ 注意**：若环境变量未配置，MCP 服务将无法启动。

## 2. 宝塔面板环境配置 (Linux)
如果您在宝塔面板上安装了 Node.js，需手动创建全局软链接以确保命令可被系统识别。
*请将其中的版本号 `v24.11.1` 替换为您实际安装的版本。*

```bash
# 1. 创建 node 全局软链接
ln -sf /www/server/nodejs/<您的版本号>/bin/node /usr/bin/node

# 2. 创建 npm 全局软链接
ln -sf /www/server/nodejs/<您的版本号>/bin/npm /usr/bin/npm

# 3. 创建 npx 全局软链接
ln -sf /www/server/nodejs/<您的版本号>/bin/npx /usr/bin/npx

# 4. 赋予执行权限
chmod +x /usr/bin/node /usr/bin/npm /usr/bin/npx

# 5. 验证环境 (需均输出版本号)
node -v && npm -v && npx -v
```

## 3. 创建 MCP 服务
以 **12306 购票综合查询** 服务为例：

1. **新建服务**：在管理界面点击创建，命名服务（如：`12306-mcp`）。
   ![创建服务界面](resource/create_mcp_1.png)

2. **配置 JSON**：填入以下配置信息。
   > **注意**：Linux 环境下 command 请使用 `npx`，Windows 环境下请使用 `npx.cmd`。

   ```JSON
   {
       "mcpServers": {
           "12306-mcp": {
               "command": "npx",
               "args": [
                   "-y",
                   "12306-mcp"
               ]
           }
       }
   }
   ```

3. **启动服务**：配置完成后点击启动按钮。
   ![启动服务](resource/create_mcp_2.png)

## 4. 验证与调试
启动成功后，点击 **编辑** 按钮，进入 **工具 (Tools)** 菜单，即可查看该 MCP 服务暴露出的可用工具列表。
![查看工具列表](resource/create_mcp_3.png)

