<script setup>
import { markRaw, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import { $t } from '@aiflowy/locales';

import { Delete, Edit, Notebook, Plus, Search } from '@element-plus/icons-vue';
import { ElDialog, ElMessage, ElMessageBox } from 'element-plus';

import { api } from '#/api/request';
import HeaderSearch from '#/components/headerSearch/HeaderSearch.vue';
import CardPage from '#/components/page/CardList.vue';
import PageData from '#/components/page/PageData.vue';
import AiKnowledgeModal from '#/views/ai/knowledge/AiKnowledgeModal.vue';
import KnowledgeSearch from '#/views/ai/knowledge/KnowledgeSearch.vue';

const router = useRouter();

// 操作按钮配置
const actions = ref([
  {
    icon: Edit,
    text: $t('button.edit'),
    className: '',
    permission: '/api/v1/aiKnowledge/save',
    onClick(row) {
      aiKnowledgeModalRef.value.openDialog(row);
    },
  },
  {
    icon: Notebook,
    text: $t('aiKnowledge.actions.knowledge'),
    className: '',
    permission: '/api/v1/aiKnowledge/save',
    onClick(row) {
      router.replace({
        path: '/ai/knowledge/document',
        query: {
          id: row.id,
          pageKey: '/ai/knowledge',
        },
      });
    },
  },
  {
    icon: Search,
    text: $t('aiKnowledge.actions.retrieve'),
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
    className: '',
    permission: '/api/v1/aiKnowledge/remove',
    onClick(row) {
      handleDelete(row);
    },
  },
]);

onMounted(() => {});
const handleDelete = (item) => {
  ElMessageBox.confirm($t('message.deleteAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('message.ok'),
    cancelButtonText: $t('message.cancel'),
    type: 'warning',
  })
    .then(() => {
      api.post('/api/v1/aiKnowledge/remove', { id: item.id }).then((res) => {
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
    text: $t('aiKnowledge.actions.addKnowledge'),
    icon: markRaw(Plus),
    type: 'primary',
    data: { action: 'add' },
    permission: '/api/v1/aiKnowledge/save',
  },
];
const handleButtonClick = (event, _item) => {
  switch (event.key) {
    case 'add': {
      aiKnowledgeModalRef.value.openDialog({});
      break;
    }
  }
};
const handleSearch = (params) => {
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
        page-url="/api/v1/aiKnowledge/page"
        :page-size="12"
        :page-sizes="[12, 24, 36, 48]"
        :init-query-params="{ status: 1 }"
      >
        <template #default="{ pageList }">
          <CardPage
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
    <AiKnowledgeModal ref="aiKnowledgeModalRef" @reload="handleSearch" />
    <!--    知识检索模态框-->
    <ElDialog
      v-model="searchKnowledgeModalVisible"
      draggable
      :close-on-click-modal="false"
      width="80%"
      align-center
      :title="$t('aiKnowledge.knowledgeRetrieval')"
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
