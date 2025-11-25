<script setup lang="ts">
import { ref } from 'vue';

import { ElButton, ElInput } from 'element-plus';

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
const handleSearch = () => {
  api
    .get(
      `/api/v1/aiKnowledge/search?knowledgeId=${props.knowledgeId}&keyword=${keyword.value}`,
    )
    .then((res) => {
      console.log('search result', res);
      searchDataList.value = res.data;
    });
};
</script>

<template>
  <div class="search-container">
    <div class="search-input">
      <ElInput v-model="keyword" placeholder="请输入内容" />
      <ElButton type="primary" @click="handleSearch">搜索</ElButton>
    </div>
    <div class="search-result">
      <PreviewSearchKnowledge :data="searchDataList" />
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
