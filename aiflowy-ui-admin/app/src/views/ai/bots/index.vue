<script setup lang="ts">
import type { BotInfo } from '@aiflowy/types';

import type { ActionButton } from '#/components/page/CardList.vue';

import { markRaw, ref } from 'vue';
import { useRouter } from 'vue-router';

import { $t } from '@aiflowy/locales';
import { tryit } from '@aiflowy/utils';

import {
  Delete,
  Edit,
  Plus,
  Setting,
  VideoPlay,
} from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';

import { removeBotFromId } from '#/api';
import defaultAvatar from '#/assets/ai/bot/defaultBotAvatar.png';
import HeaderSearch from '#/components/headerSearch/HeaderSearch.vue';
import CardList from '#/components/page/CardList.vue';
import PageData from '#/components/page/PageData.vue';

import Modal from './modal.vue';

const router = useRouter();
const pageDataRef = ref();
const modalRef = ref<InstanceType<typeof Modal>>();

// 操作按钮配置
const headerButtons = [
  {
    key: 'create',
    text: `${$t('button.create')} Bot`,
    icon: markRaw(Plus),
    type: 'primary',
    data: { action: 'create' },
    permission: '/api/v1/aiKnowledge/save',
  },
];
const actions: ActionButton[] = [
  {
    icon: Edit,
    text: $t('button.edit'),
    className: '',
    permission: '',
    onClick(row: BotInfo) {
      modalRef.value?.open('edit', row);
    },
  },
  {
    icon: Setting,
    text: $t('button.setting'),
    className: '',
    permission: '',
    onClick(row: BotInfo) {
      router.push({ path: `/ai/bots/setting/${row.id}` });
    },
  },
  {
    icon: VideoPlay,
    text: $t('button.run'),
    className: '',
    permission: '',
    onClick(row: BotInfo) {
      router.push({ path: `/ai/bots/run/${row.id}` });
      // 打开新窗口
      // window.open(`/ai/bots/run/${item.id}`, '_blank');
    },
  },
  {
    icon: Delete,
    text: $t('button.delete'),
    className: '',
    permission: '/api/v1/aiBot/remove',
    onClick(row: BotInfo) {
      removeBot(row);
    },
  },
];

const removeBot = async (bot: BotInfo) => {
  const [action] = await tryit(
    ElMessageBox.confirm($t('message.deleteAlert'), $t('message.noticeTitle'), {
      confirmButtonText: $t('message.ok'),
      cancelButtonText: $t('message.cancel'),
      type: 'warning',
    }),
  );

  if (!action) {
    const [err, res] = await tryit(removeBotFromId(bot.id));

    if (!err && res.errorCode === 0) {
      ElMessage.success($t('message.deleteOkMessage'));
      pageDataRef.value.setQuery({});
    }
  }
};

const handleSearch = (params: string) => {
  pageDataRef.value.setQuery({ title: params, isQueryOr: true });
};
const handleButtonClick = () => {
  modalRef.value?.open('create');
};
</script>

<template>
  <div class="space-y-6 p-6">
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
        <CardList
          :default-icon="defaultAvatar"
          :data="pageList"
          :actions="actions"
        />
      </template>
    </PageData>

    <!-- 创建&编辑Bot弹窗 -->
    <Modal ref="modalRef" @success="pageDataRef.setQuery({})" />
  </div>
</template>
