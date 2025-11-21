<script setup lang="ts">
import { markRaw, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { $t } from '@aiflowy/locales';

import { Back, Plus } from '@element-plus/icons-vue';

import CategoryPanel from '#/components/categoryPanel/CategoryPanel.vue';
import HeaderSearch from '#/components/headerSearch/HeaderSearch.vue';
import ChunkDocumentTable from '#/views/ai/knowledge/ChunkDocumentTable.vue';
import DocumentTable from '#/views/ai/knowledge/DocumentTable.vue';

const route = useRoute();
const router = useRouter();

const knowledgeId = ref<string>((route.query.id as string) || '');

const categoryData = [
  { key: 'docList', name: $t('aiKnowledge.documentList') },
  { key: 'knowledgeSearch', name: $t('aiKnowledge.knowledgeRetrieval') },
];
const headerButtons = [
  {
    key: 'back',
    text: $t('button.back'),
    icon: markRaw(Back),
    data: { action: 'back' },
  },
  {
    key: 'importFile',
    text: $t('button.importFile'),
    icon: markRaw(Plus),
    type: 'primary',
    data: { action: 'importFile' },
  },
];
const selectedCategory = ref('docList');
const handleSearch = () => {};
const handleButtonClick = (event: any) => {
  // 根据按钮 key 执行不同操作
  switch (event.key) {
    case 'back': {
      router.push({ path: '/ai/knowledge' });
      break;
    }
    case 'importFile': {
      break;
    }
  }
};
const handleCategoryClick = (category: any) => {
  selectedCategory.value = category.key;
  viewDocVisible.value = false;
};
const viewDocVisible = ref(false);
const documentId = ref('');
// 子组件传递事件，显示查看文档详情
const viewDoc = (docId: string) => {
  viewDocVisible.value = true;
  documentId.value = docId;
};
</script>

<template>
  <div class="doc-container">
    <div class="doc-header">
      <HeaderSearch
        :buttons="headerButtons"
        search-placeholder="搜索用户"
        @search="handleSearch"
        @button-click="handleButtonClick"
      />
    </div>
    <div class="doc-content">
      <CategoryPanel
        :default-selected-category="$t('aiKnowledge.documentList')"
        :categories="categoryData"
        title-key="name"
        :use-img-for-svg="true"
        :need-hide-collapse="true"
        :expand-width="200"
        @click="handleCategoryClick"
      />
      <div v-if="selectedCategory === 'docList'" class="doc-table">
        <DocumentTable
          :knowledge-id="knowledgeId"
          @view-doc="viewDoc"
          v-if="!viewDocVisible"
        />
        <ChunkDocumentTable v-else :document-id="documentId" />
      </div>
      <div v-if="selectedCategory === 'knowledgeSearch'" class="doc-table">
        <DocumentTable :knowledge-id="knowledgeId" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.doc-container {
  padding: 20px;
  width: 100%;
  display: flex;
  flex-direction: column;
}
.doc-header {
  width: 100%;
  margin: 0 auto;
}
.doc-content {
  display: flex;
  flex-direction: row;
  height: calc(100vh - 161px);
  padding-top: 20px;
  width: 100%;
}
.doc-table {
  width: 100%;
  height: 100%;
  overflow: auto;
}
</style>
