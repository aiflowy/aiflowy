<script setup lang="ts">
import type { BotInfo } from '@aiflowy/types';

import { markRaw } from 'vue';
import { useRouter } from 'vue-router';

import { Delete, Edit, Setting, VideoPlay } from '@element-plus/icons-vue';

import CardPage from '#/components/cardPage/CardPage.vue';
import PageData from '#/components/page/PageData.vue';

const router = useRouter();

// 操作按钮配置
const actions = [
  {
    name: 'edit',
    label: '编辑',
    type: 'default',
    icon: markRaw(Edit),
  },
  {
    name: 'setting',
    label: '设置',
    type: 'default',
    icon: markRaw(Setting),
  },
  {
    name: 'run',
    label: '运行',
    type: 'default',
    icon: markRaw(VideoPlay),
  },
  {
    name: 'delete',
    label: '删除',
    type: 'danger',
    icon: markRaw(Delete),
  },
];

const handleAction = ({
  action,
  item,
}: {
  action: (typeof actions)[number];
  item: BotInfo;
}) => {
  if (action.name === 'run') {
    router.push({ path: `/ai/bots/run/${item.id}` });
    // window.open(`/ai/bots/run/${item.id}`, '_blank');
  } else if (action.name === 'setting') {
    router.push({ path: `/ai/bots/setting/${item.id}` });
  }
};
</script>

<template>
  <div class="page-container">
    <PageData page-url="/api/v1/aiBot/page" :init-query-params="{ status: 1 }">
      <template #default="{ pageList }">
        <CardPage
          title-key="title"
          avatar-key="icon"
          description-key="description"
          :data="pageList"
          :actions="actions"
          @action-click="handleAction"
        />
      </template>
    </PageData>
  </div>
</template>
