<script setup lang="ts">
import { ref } from 'vue';

import { $t } from '@aiflowy/locales';

import { ElButton, ElInput, ElMessage } from 'element-plus';

import { api } from '#/api/request';
import PreviewSearchKnowledge from '#/views/ai/knowledge/PreviewSearchKnowledge.vue';

const props = defineProps({
  knowledgeId: {
    type: String,
    required: true,
  },
});
const searchDataList = ref([]);
const keyword = ref('');
const previewSearchKnowledgeRef = ref();
const handleSearch = () => {
  if (!keyword.value) {
    ElMessage.error($t('message.pleaseInputContent'));
    return;
  }
  previewSearchKnowledgeRef.value.loadingContent(true);
  api
    .get(
      `/api/v1/aiKnowledge/search?knowledgeId=${props.knowledgeId}&keyword=${keyword.value}`,
    )
    .then((res) => {
      previewSearchKnowledgeRef.value.loadingContent(false);
      searchDataList.value = res.data;
    });
};
</script>

<template>
  <div class="search-container">
    <div class="search-input">
      <ElInput
        v-model="keyword"
        :placeholder="$t('common.searchPlaceholder')"
      />
      <ElButton type="primary" @click="handleSearch">
        {{ $t('button.query') }}
      </ElButton>
    </div>
    <div class="search-result">
      <PreviewSearchKnowledge
        :data="searchDataList"
        ref="previewSearchKnowledgeRef"
      />
    </div>
  </div>
</template>

<style scoped>
.search-container {
  width: 100%;
  height: 100%;
  padding: 20px;
  display: flex;
  flex-direction: column;
}
.search-input {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.search-result {
  padding-top: 20px;
  flex: 1;
}
</style>
