# WebSDK 使用指南

## 简介

AIFlowy WebSDK 是一个轻量级的 JavaScript SDK，允许您在任何网站中快速嵌入 AI 对话组件。通过简单的配置，即可为您的网站添加 AI 助手能力。

## 安装

### 方式一：通过 npm 安装

```bash
npm install @aiflowy/websdk
# 或
pnpm add @aiflowy/websdk
# 或
yarn add @aiflowy/websdk
```

### 方式二：通过 CDN 引入

```html
<script src="https://unpkg.com/@aiflowy/websdk/dist/index.js"></script>
<!-- 或 -->
<script src="https://cdn.jsdelivr.net/npm/@aiflowy/websdk/dist/index.js"></script>
```

## 快速开始

### 基础用法

```javascript
import { initAiflowy } from '@aiflowy/websdk';

// 初始化 SDK
const sdk = initAiflowy({
  botId: 'your-bot-id',
  endpoint: 'https://your-aiflowy-domain.com',
});
```

### CDN 方式使用

```html
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>我的网站</title>
</head>
<body>
  <script src="https://unpkg.com/@aiflowy/websdk/dist/index.js"></script>
  <script>
    AiflowySDK.initAiflowy({
      botId: 'your-bot-id',
      endpoint: 'https://your-aiflowy-domain.com',
    });
  </script>
</body>
</html>
```

## 配置选项

### 自定义图标

```javascript
initAiflowy({
  botId: 'your-bot-id',
  endpoint: 'https://your-aiflowy-domain.com',
  launcher: {
    icon: '<img src="https://example.com/my-icon.svg" width="28" height="28">',
  },
});
```

### 完整配置示例

```javascript
import { initAiflowy } from '@aiflowy/websdk';

const sdk = initAiflowy({
  // 必填：智能体 ID
  botId: 'your-bot-id',
  
  // 必填：AIFlowy 服务地址
  endpoint: 'https://your-aiflowy-domain.com',
  
  // 浮动入口配置
  launcher: {
    placement: 'bottom-right',      // 位置：bottom-right | bottom-left | top-right | top-left
    label: '智能客服',               // 按钮标签
    icon: '',                        // 自定义图标（HTML 字符串）
    size: 60,                        // 按钮尺寸（px）
    backgroundColor: '#0066ff',      // 背景颜色
    textColor: '#FFFFFF',            // 文字颜色
  },
  
  // 聊天窗口配置
  panel: {
    title: '智能体对话',             // 窗口标题
    width: 1200,                     // 窗口宽度（px）
    height: 800,                     // 窗口高度（px）
    zIndex: 999999,                  // 层级
  },
  
  // 主题配置
  theme: {
    borderRadius: 12,                // 圆角（px）
    dark: false,                     // 是否深色模式
    primaryColor: '#0066ff',         // 主题色
  },
  
  // 生命周期回调
  callbacks: {
    onOpen: () => console.log('窗口打开'),
    onClose: () => console.log('窗口关闭'),
    onReady: () => console.log('SDK 就绪'),
  },
});
```

## 配置项说明

### 必填项

| 参数 | 类型 | 说明 |
|------|------|------|
| `botId` | `string` | 智能体 ID，在 AIFlowy 后台创建 Bot 后获取 |
| `endpoint` | `string` | AIFlowy 服务地址，例如 `https://your-domain.com` |

### launcher - 浮动入口配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `placement` | `string` | `'bottom-right'` | 按钮位置，支持 `bottom-right`、`bottom-left`、`top-right`、`top-left` |
| `label` | `string` | `'智能客服'` | 按钮的 aria-label 属性 |
| `icon` | `string` | `''` | 自定义图标，传入 HTML 字符串，为空时使用默认聊天气泡图标 |
| `size` | `number` | `60` | 按钮尺寸，单位 px |
| `backgroundColor` | `string` | `'#0066ff'` | 按钮背景色 |
| `textColor` | `string` | `'#FFFFFF'` | 按钮内文字/图标颜色 |

### panel - 聊天窗口配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `title` | `string` | `'智能体对话'` | 聊天窗口标题 |
| `width` | `number` | `1200` | 窗口宽度，单位 px |
| `height` | `number` | `800` | 窗口高度，单位 px |
| `zIndex` | `number` | `999999` | 窗口层级 |

### theme - 主题配置

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `borderRadius` | `number` | `12` | 窗口圆角，单位 px |
| `dark` | `boolean` | `false` | 是否启用深色模式 |
| `primaryColor` | `string` | `'#0066ff'` | 主题色，用于窗口头部等元素 |

### callbacks - 生命周期回调

| 参数 | 类型 | 说明 |
|------|------|------|
| `onOpen` | `() => void` | 聊天窗口打开时触发 |
| `onClose` | `() => void` | 聊天窗口关闭时触发 |
| `onReady` | `() => void` | SDK 初始化完成时触发 |

## API 方法

初始化后返回的 SDK 实例提供以下方法：

### show()

显示聊天窗口。

```javascript
sdk.show();
```

### hide()

隐藏聊天窗口。

```javascript
sdk.hide();
```

### toggle()

切换聊天窗口的显示/隐藏状态。

```javascript
sdk.toggle();
```

### destroy()

销毁 SDK 实例，移除所有注入的 DOM 元素和样式。

```javascript
sdk.destroy();
```

## 获取 botId

1. 登录 AIFlowy 后台管理系统
2. 进入「智能体」模块
3. 创建或选择一个已有的 Bot
4. 在 Bot 详情中获取 Bot ID