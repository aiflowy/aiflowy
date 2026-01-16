# 组件使用

## CardList 组件

CardList 用于卡片类型展示分页数据，一般配合 PageData 组件使用。

### 使用方法

`CardList` 的参数类型：

```typescript
interface ActionButton {
  // 按钮图标
  icon: any;
  // 按钮文本
  text: string;
  // 自定义类名
  className: string;
  // 权限
  permission: string;
  // 点击事件
  onClick: (row: any) => void;
}

interface CardListProps {
  // 图标的 key 名
  iconField?: string;
  // 标题的 key 名
  titleField?: string;
  // 描述的 key 名
  descField?: string;
  // 操作按钮列表
  actions?: ActionButton[];
  // 默认头像图标（iconField 不存在时使用）
  defaultIcon: any;
  // 数据列表
  data: any[];
}
```

`CardList` 的使用如下代码所示：

```vue
<script setup lang="ts">
import defaultAvatar from '#/assets/ai/bot/defaultBotAvatar.png';
import { Edit } from '@element-plus/icons-vue';

const pageList = [
  {
    "id": 1000000,
    "description": "",
    "icon": "",
    "title": "测试BOT"
  }
  ...
]

const actions: ActionButton[] = [
  {
    icon: Edit,
    text: $t('button.edit'),
    className: '',
    permission: '',
    onClick(row: any) {...},
  }
  ...
];
</script>

<template>
  <CardList
    iconField="icon"
    titleField="title"
    descField="description"
    :default-icon="defaultAvatar"
    :data="pageList"
    :actions="actions"
  />
</template>
```

## PageData 组件

PageData 用于获取分页数据,一般配合 CardList 组件使用。

### 使用方法

`PageData` 的参数类型：

```typescript
interface PageDataProps {
    // api路径
    pageUrl: string;
    // 当前分页数量
    pageSize?: number;
    // 可选分页数量
    pageSizes?: number[];
    // 额外查询参数
    extraQueryParams?: Record<string, any>;
}
```

`PageData` 的使用如下代码所示：

```vue
<script setup lang="ts">
import defaultAvatar from '#/assets/ai/bot/defaultBotAvatar.png';
import { Edit } from '@element-plus/icons-vue';

const pageDataRef = ref();

const actions: ActionButton[] = [
  {
    icon: Edit,
    text: $t('button.edit'),
    className: '',
    permission: '',
    onClick(row: any) {...},
  }
  ...
];

// 刷新数据
pageDataRef.value.setQuery({});

// 查询数据
pageDataRef.value.setQuery({ title: params, isQueryOr: true });
</script>

<template>
  <PageData
      ref="pageDataRef"
      page-url="/api/v1/bot/page"
      :page-sizes="[12, 18, 24]"
      :page-size="12"
  >
    <template #default="{ pageList }">
      <CardList
        iconField="icon"
        titleField="title"
        descField="description"
        :default-icon="defaultAvatar"
        :data="pageList"
        :actions="actions"
      />
    </template>
  </PageData>
</template>
```