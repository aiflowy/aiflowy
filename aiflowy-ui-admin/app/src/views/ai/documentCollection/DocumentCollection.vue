<script setup lang="ts">
import type { ActionButton } from '#/components/page/CardList.vue';

import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import { $t } from '@aiflowy/locales';

import { Delete, Edit, Notebook, Plus, Search } from '@element-plus/icons-vue';
import { ElDialog, ElMessage, ElMessageBox } from 'element-plus';

import { api } from '#/api/request';
import defaultIcon from '#/assets/ai/knowledge/book.svg';
import HeaderSearch from '#/components/headerSearch/HeaderSearch.vue';
import CardPage from '#/components/page/CardList.vue';
import PageData from '#/components/page/PageData.vue';
import DocumentCollectionModal from '#/views/ai/documentCollection/DocumentCollectionModal.vue';
import KnowledgeSearch from '#/views/ai/documentCollection/KnowledgeSearch.vue';

const router = useRouter();

// 操作按钮配置
const actions: ActionButton[] = [
  {
    icon: Edit,
    text: $t('button.edit'),
    className: '',
    permission: '/api/v1/documentCollection/save',
    onClick(row) {
      aiKnowledgeModalRef.value.openDialog(row);
    },
  },
  {
    icon: Notebook,
    text: $t('documentCollection.actions.knowledge'),
    className: '',
    permission: '/api/v1/documentCollection/save',
    onClick(row) {
      router.replace({
        path: '/ai/documentCollection/document',
        query: {
          id: row.id,
          pageKey: '/ai/documentCollection',
        },
      });
    },
  },
  {
    icon: Search,
    text: $t('documentCollection.actions.retrieve'),
    className: '',
    permission: '',
    onClick(row) {
      selectSearchKnowledgeId.value = row.id;
      searchKnowledgeModalVisible.value = true;
    },
  },
  {
    text: $t('button.delete'),
    icon: Delete,
    className: 'item-danger',
    permission: '/api/v1/documentCollection/remove',
    onClick(row) {
      handleDelete(row);
    },
  },
];

onMounted(() => {});
const handleDelete = (item: any) => {
  ElMessageBox.confirm($t('message.deleteAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('message.ok'),
    cancelButtonText: $t('message.cancel'),
    type: 'warning',
  })
    .then(() => {
      api
        .post('/api/v1/documentCollection/remove', { id: item.id })
        .then((res) => {
          if (res.errorCode === 0) {
            ElMessage.success($t('message.deleteOkMessage'));
            pageDataRef.value.setQuery({});
          }
        });
    })
    .catch(() => {});
};
const selectSearchKnowledgeId = ref('');
const searchKnowledgeModalVisible = ref(false);

const pageDataRef = ref();
const aiKnowledgeModalRef = ref();
const headerButtons = [
  {
    key: 'add',
    text: $t('documentCollection.actions.addKnowledge'),
    icon: Plus,
    type: 'primary',
    data: { action: 'add' },
    permission: '/api/v1/documentCollection/save',
  },
];
const handleButtonClick = (event: any, _item: any) => {
  switch (event.key) {
    case 'add': {
      aiKnowledgeModalRef.value.openDialog({});
      break;
    }
  }
};
const handleSearch = (params: any) => {
  pageDataRef.value.setQuery({ title: params, isQueryOr: true });
};
</script>

<template>
  <div class="knowledge-container">
    <div class="knowledge-header">
      <HeaderSearch
        :buttons="headerButtons"
        @search="handleSearch"
        @button-click="handleButtonClick"
      />
    </div>
    <div class="kd-content-container">
      <PageData
        ref="pageDataRef"
        page-url="/api/v1/documentCollection/page"
        :page-size="12"
        :page-sizes="[12, 24, 36, 48]"
        :init-query-params="{ status: 1 }"
      >
        <template #default="{ pageList }">
          <CardPage
            :default-icon="defaultIcon"
            title-key="title"
            avatar-key="icon"
            description-key="description"
            :data="pageList"
            :actions="actions"
          />
        </template>
      </PageData>
    </div>
    <!--    新增知识库模态框-->
    <DocumentCollectionModal ref="aiKnowledgeModalRef" @reload="handleSearch" />
    <!--    知识检索模态框-->
    <ElDialog
      v-model="searchKnowledgeModalVisible"
      draggable
      :close-on-click-modal="false"
      width="80%"
      align-center
      :title="$t('documentCollection.knowledgeRetrieval')"
    >
      <div class="search-knowledge-dialog">
        <KnowledgeSearch :knowledge-id="selectSearchKnowledgeId" />
      </div>
    </ElDialog>
  </div>
</template>

<style scoped>
.knowledge-container {
  padding: 24px;
  width: 100%;
  margin: 0 auto;
}
.search-knowledge-dialog {
  height: calc(100vh - 161px);
}

h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}
.kd-content-container {
  margin-top: 24px;
}
</style>
