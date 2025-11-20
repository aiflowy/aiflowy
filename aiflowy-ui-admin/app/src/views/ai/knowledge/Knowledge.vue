<script setup>
import { markRaw, onMounted, ref } from 'vue';

import { $t } from '@aiflowy/locales';

import { Delete, Edit, Plus, View } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';

import { api } from '#/api/request';
import CardPage from '#/components/cardPage/CardPage.vue';
import HeaderSearch from '#/components/headerSearch/HeaderSearch.vue';
import PageData from '#/components/page/PageData.vue';
import AiKnowledgeModal from '#/views/ai/knowledge/AiKnowledgeModal.vue';

// 操作按钮配置
const actions = ref([
  {
    name: 'edit',
    label: '编辑',
    type: 'primary',
    icon: markRaw(Edit),
  },
  {
    name: 'view',
    label: '列表',
    type: 'success',
    icon: markRaw(View),
  },
  {
    name: 'delete',
    label: '检索',
    type: 'danger',
    icon: markRaw(View),
  },
  {
    name: 'delete',
    label: '删除',
    type: 'info',
    icon: markRaw(Delete),
  },
]);

onMounted(() => {});
const handleDelete = (item) => {
  ElMessageBox.confirm('确定要删除吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
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
// 处理操作按钮点击
const handleAction = ({ action, item }) => {
  // 根据不同的操作执行不同的逻辑
  switch (action.name) {
    case 'delete': {
      handleDelete(item);
      break;
    }
    case 'edit': {
      aiKnowledgeModalRef.value.openDialog(item);
      break;
    }
    // 其他操作...
  }
};

const pageDataRef = ref();
const aiKnowledgeModalRef = ref();
const headerButtons = [
  {
    key: 'add',
    text: '新增知识库',
    icon: markRaw(Plus),
    type: 'primary',
    data: { action: 'add' },
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
        search-placeholder="搜索用户"
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
            @action-click="handleAction"
          />
        </template>
      </PageData>
    </div>
    <!--    新增知识库模态框-->
    <!--    <AddKnowledgeModal ref="addKnowledgeRef" @success="handleAddSuccess" />-->
    <AiKnowledgeModal ref="aiKnowledgeModalRef" @reload="handleSearch" />
  </div>
</template>

<style scoped>
.knowledge-container {
  padding: 20px;
  width: 100%;
  margin: 0 auto;
}

h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}
.kd-content-container {
  margin-top: 20px;
}
</style>
