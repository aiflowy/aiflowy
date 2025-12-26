<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { $t } from '@aiflowy/locales';

import { ArrowLeft, Plus } from '@element-plus/icons-vue';
import { ElIcon, ElImage } from 'element-plus';

import { api } from '#/api/request';
import bookIcon from '#/assets/ai/knowledge/book.svg';
import HeaderSearch from '#/components/headerSearch/HeaderSearch.vue';
import PageSide from '#/components/page/PageSide.vue';
import ChunkDocumentTable from '#/views/ai/documentCollection/ChunkDocumentTable.vue';
import DocumentTable from '#/views/ai/documentCollection/DocumentTable.vue';
import ImportKnowledgeDocFile from '#/views/ai/documentCollection/ImportKnowledgeDocFile.vue';
import KnowledgeSearch from '#/views/ai/documentCollection/KnowledgeSearch.vue';

const route = useRoute();
const router = useRouter();

const knowledgeId = ref<string>((route.query.id as string) || '');
const knowledgeInfo = ref<any>({});
const getKnowledge = () => {
  api
    .get('/api/v1/documentCollection/detail', {
      params: { id: knowledgeId.value },
    })
    .then((res) => {
      if (res.errorCode === 0) {
        knowledgeInfo.value = res.data;
      }
    });
};
onMounted(() => {
  getKnowledge();
});
const back = () => {
  router.push({ path: '/ai/documentCollection' });
};
const categoryData = [
  { key: 'documentList', name: $t('documentCollection.documentList') },
  { key: 'knowledgeSearch', name: $t('documentCollection.knowledgeRetrieval') },
];
const headerButtons = [
  {
    key: 'importFile',
    text: $t('button.importFile'),
    icon: Plus,
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
const backDoc = () => {
  isImportFileVisible.value = false;
};
</script>

<template>
  <div class="document-container">
    <div v-if="!isImportFileVisible" class="doc-header-container">
      <div class="doc-knowledge-container">
        <div @click="back()" style="cursor: pointer">
          <ElIcon><ArrowLeft /></ElIcon>
        </div>
        <div>
          <ElImage :src="bookIcon" style="width: 36px; height: 36px" />
        </div>
        <div class="knowledge-info-container">
          <div class="title">{{ knowledgeInfo.title || '' }}</div>
          <div class="description">
            {{ knowledgeInfo.description || '' }}
          </div>
        </div>
      </div>
      <div class="doc-content">
        <div>
          <PageSide
            label-key="name"
            value-key="key"
            :menus="categoryData"
            default-selected="documentList"
            @change="handleCategoryClick"
          />
        </div>
        <div class="doc-table-content border border-[var(--el-border-color)]">
          <div v-if="selectedCategory === 'documentList'" class="doc-table">
            <div class="doc-header" v-if="!viewDocVisible">
              <HeaderSearch
                :buttons="headerButtons"
                @search="handleSearch"
                @button-click="handleButtonClick"
              />
            </div>
            <DocumentTable
              :knowledge-id="knowledgeId"
              @view-doc="viewDoc"
              v-if="!viewDocVisible"
            />
            <ChunkDocumentTable v-else :document-id="documentId" />
          </div>
          <!--知识检索-->
          <div
            v-if="selectedCategory === 'knowledgeSearch'"
            class="doc-search-container"
          >
            <KnowledgeSearch :knowledge-id="knowledgeId" />
          </div>
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
  padding: 24px 24px 30px 24px;
}
.doc-container {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}
.doc-table-content {
  border-radius: 8px;
  width: 100%;
  box-sizing: border-box;
  padding: 20px 14px 0 14px;
  background-color: var(--el-bg-color);
  flex: 1;
}
.doc-header {
  width: 100%;
  margin: 0 auto;
  padding-bottom: 21px;
}
.doc-content {
  display: flex;
  flex-direction: row;
  height: 100%;
  width: 100%;
  gap: 12px;
}

.doc-table {
  background-color: var(--el-bg-color);
}
.doc-imp-container {
  flex: 1;
  width: 100%;
  box-sizing: border-box;
}
.doc-header-container {
  display: flex;
  flex-direction: column;
  width: 100%;
}
.doc-knowledge-container {
  display: flex;
  flex-direction: row;
  align-items: center;
  margin-bottom: 20px;
  gap: 8px;
}
.knowledge-info-container {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.title {
  font-weight: 500;
  font-size: 16px;
  color: rgba(0, 0, 0, 0.85);
  line-height: 24px;
  text-align: left;
  font-style: normal;
  text-transform: none;
}

.description {
  font-weight: 400;
  font-size: 14px;
  color: #75808d;
  line-height: 22px;
  text-align: left;
  font-style: normal;
  text-transform: none;
}
.doc-search-container {
  width: 100%;
  height: 100%;
}
</style>
