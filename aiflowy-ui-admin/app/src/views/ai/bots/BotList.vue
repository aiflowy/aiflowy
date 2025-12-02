<script setup lang="ts">
import type { BotInfo } from '@aiflowy/types';

import { markRaw, ref } from 'vue';
import { useRouter } from 'vue-router';

import { $t } from '@aiflowy/locales';

import {
  Delete,
  Edit,
  Plus,
  Setting,
  VideoPlay,
} from '@element-plus/icons-vue';

import CardPage from '#/components/cardPage/CardPage.vue';
import HeaderSearch from '#/components/headerSearch/HeaderSearch.vue';
import PageData from '#/components/page/PageData.vue';

const router = useRouter();
const pageDataRef = ref();

const headerButtons = [
  {
    key: 'add',
    text: $t('aiBots.createBot'),
    icon: markRaw(Plus),
    type: 'primary',
    data: { action: 'add' },
    permission: '/api/v1/aiKnowledge/save',
  },
];
// 操作按钮配置
const actions = [
  {
    name: 'edit',
    label: $t('button.edit'),
    type: 'default',
    icon: markRaw(Edit),
  },
  {
    name: 'setting',
    label: $t('button.setting'),
    type: 'default',
    icon: markRaw(Setting),
  },
  {
    name: 'run',
    label: $t('button.run'),
    type: 'default',
    icon: markRaw(VideoPlay),
  },
  {
    name: 'delete',
    label: $t('button.delete'),
    type: 'danger',
    icon: markRaw(Delete),
    permission: '/api/v1/aiBot/remove',
  },
];

const handleButtonClick = (event: any) => {
  switch (event.key) {
    case 'add': {
      // aiKnowledgeModalRef.value.openDialog({});
      console.warn(event);
      break;
    }
  }
};
const handleSearch = (params: string) => {
  pageDataRef.value.setQuery({ title: params, isQueryOr: true });
};
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
  <div class="space-y-5 p-5">
    <HeaderSearch
      :buttons="headerButtons"
      @search="handleSearch"
      @button-click="handleButtonClick"
    />
    <PageData
      ref="pageDataRef"
      page-url="/api/v1/aiBot/page"
      :init-query-params="{ status: 1 }"
    >
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
