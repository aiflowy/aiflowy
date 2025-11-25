# @aiflowy/hooks

用于多个 `app` 公用的 hook，继承了 `@aiflowy/hooks` 的所有能力。业务上有通用 hooks 可以放在这里。

## 用法

### 添加依赖

```bash
# 进入目标应用目录，例如 apps/xxxx-app
# cd apps/xxxx-app
pnpm add @aiflowy/hooks
```

### 使用

```ts
import { useNamespace } from '@aiflowy/hooks';
```
