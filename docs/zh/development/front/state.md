# 状态管理

## 前言

在 Vue 中，状态管理非常重要，好用的状态管理框架能在大型的应用中，
轻松的在组件之间进行数据传递，无需进行 props 穿透。同时还有简化组件之间的代码逻辑，提高代码性能。

## Pinia

在 AIFlowy 中，我们是使用 [Pinia](https://github.com/vuejs/pinia) 进行状态管理的，
Pinia 是一个轻量、灵活，与Vue 3 Composition API深度集成的状态管理库。目前已经有 14.4k 的 star。

## 示例代码

### 通过 Pinia 创建 Store

```typescript
// store/counter.ts
import { defineStore } from 'pinia'

export const useCounterStore = defineStore('counter', {
  state: () => {
    return { count: 0 }
  },
  // 也可以这样定义
  // state: () => ({ count: 0 })
  actions: {
    increment() {
      this.count++
    },
  },
})

// 在 store/index.ts 中导出
export * from './counter';
```

然后你就可以在一个组件中使用该 store 了：

```typescript
<script setup lang="ts">
import { useCounterStore } from '#/store'

const counter = useCounterStore()

counter.count++
// 自动补全！ ✨
counter.$patch({ count: counter.count + 1 })
// 或使用 action 代替
counter.increment()
</script>

<template>
  <!-- 直接从 store 中访问 state -->
  <div>Current Count: {{ counter.count }}</div>
</template>
```































