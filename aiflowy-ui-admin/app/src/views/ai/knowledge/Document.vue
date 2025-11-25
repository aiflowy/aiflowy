<script setup lang="ts">
import { markRaw, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { $t } from '@aiflowy/locales';

import { Back, Plus } from '@element-plus/icons-vue';

import CategoryPanel from '#/components/categoryPanel/CategoryPanel.vue';
import HeaderSearch from '#/components/headerSearch/HeaderSearch.vue';
import ChunkDocumentTable from '#/views/ai/knowledge/ChunkDocumentTable.vue';
import DocumentTable from '#/views/ai/knowledge/DocumentTable.vue';
import ImportKnowledgeDocFile from '#/views/ai/knowledge/ImportKnowledgeDocFile.vue';
import KnowledgeSearch from '#/views/ai/knowledge/KnowledgeSearch.vue';

const route = useRoute();
const router = useRouter();

const knowledgeId = ref<string>((route.query.id as string) || '');

const categoryData = [
  { key: 'documentList', name: $t('aiKnowledge.documentList') },
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
const isImportFileVisible = ref(false);
const selectedCategory = ref('documentList');
const handleSearch = () => {};
const handleButtonClick = (event: any) => {
  // 根据按钮 key 执行不同操作
  switch (event.key) {
    case 'back': {
      router.push({ path: '/ai/knowledge' });
      break;
    }
    case 'importFile': {
      isImportFileVisible.value = true;
      break;
    }
  }
};
const handleCategoryClick = (category: any) => {
  selectedCategory.value = category.item.key;
  viewDocVisible.value = false;
};
const viewDocVisible = ref(false);
const documentId = ref('');
// 子组件传递事件，显示查看文档详情
const viewDoc = (docId: string) => {
  viewDocVisible.value = true;
  documentId.value = docId;
};
const backDoc = () => {
  isImportFileVisible.value = false;
};
</script>

<template>
  <div class="document-container">
    <div v-if="!isImportFileVisible" class="doc-container">
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
          default-selected-category="documentList"
          :categories="categoryData"
          title-key="name"
          value-key="key"
          :use-img-for-svg="true"
          :need-hide-collapse="true"
          :expand-width="200"
          @click="handleCategoryClick"
        />
        <div v-if="selectedCategory === 'documentList'" class="doc-table">
          <DocumentTable
            :knowledge-id="knowledgeId"
            @view-doc="viewDoc"
            v-if="!viewDocVisible"
          />
          <ChunkDocumentTable v-else :document-id="documentId" />
        </div>
        <div v-if="selectedCategory === 'knowledgeSearch'" class="doc-table">
          <KnowledgeSearch :knowledge-id="knowledgeId" />
        </div>
      </div>
    </div>

    <div v-else class="doc-imp-container">
      <ImportKnowledgeDocFile @import-back="backDoc" />
    </div>
  </div>
</template>

<style scoped>
.document-container {
  width: 100%;
  display: flex;
  height: 100%;
}
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
.doc-imp-container {
  flex: 1;
  width: 100%;
  padding: 20px;
}
</style>
