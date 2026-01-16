# 基础概念

## 包

包指的是一个独立的模块，可以是一个组件、一个工具、一个库等。包可以被多个应用引用，也可以被其他包引用。包都被放置在 `packages` 目录下。

对于这些包，你可以把它看作是一个独立的 `npm` 包，使用方式与 `npm` 包一样。

### 包引入

在 `package.json` 中引入包：

```json {3}
{
  "dependencies": {
    "@aiflowy/utils": "workspace:*"
  }
}
```

### 包使用

在代码中引入包：

```ts
import { isString } from '@aiflowy/utils';
```

## 别名

在项目中，你可以看到一些 `#` 开头的路径，例如： `#/api`、`#/views`, 这些路径都是别名，用于快速定位到某个目录。它不是通过 `vite` 的 `alias` 实现的，而是通过 `Node.js` 本身的 [subpath imports](https://nodejs.org/api/packages.html#subpath-imports) 原理。只需要在 `package.json` 中配置 `imports` 字段即可。

```json {3}
{
  "imports": {
    "#/*": "./src/*"
  }
}
```

为了 IDE 能够识别这些别名，我们还需要在`tsconfig.json`内配置：

```json {5}
{
  "compilerOptions": {
    "baseUrl": ".",
    "paths": {
      "#/*": ["src/*"]
    }
  }
}
```

这样，你就可以在代码中使用别名了。
