<script setup lang="ts">
import { ref } from 'vue';

import { $t } from '@aiflowy/locales';

import { Delete, Download, View } from '@element-plus/icons-vue';
import {
  ElButton,
  ElIcon,
  ElMessage,
  ElMessageBox,
  ElTable,
  ElTableColumn,
} from 'element-plus';

import { api } from '#/api/request';
import PageData from '#/components/page/PageData.vue';

const props = defineProps({
  knowledgeId: {
    required: true,
    type: String,
  },
});

const emits = defineEmits(['viewDoc']);
const pageDataRef = ref();
const handleView = (row: any) => {
  emits('viewDoc', row.id);
};
const handleDownload = (row: any) => {
  console.log('下载文档', row);
};
const handleDelete = (row: any) => {
  ElMessageBox.confirm($t('message.deleteAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('button.confirm'),
    cancelButtonText: $t('button.cancel'),
    type: 'warning',
  }).then(() => {
    api.post('/api/v1/aiDocument/removeDoc', { id: row.id }).then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('message.deleteOkMessage'));
        pageDataRef.value.setQuery({ id: props.knowledgeId });
      }
    });
    // 删除逻辑
  });
};
</script>

<template>
  <PageData
    page-url="/api/v1/aiDocument/documentList"
    ref="pageDataRef"
    :page-size="10"
    :extra-query-params="{ id: props.knowledgeId }"
  >
    <template #default="{ pageList }">
      <ElTable :data="pageList" style="width: 100%" size="large">
        <ElTableColumn
          prop="id"
          :label="$t('aiKnowledge.fileName')"
          width="180"
        >
          <template #default="{ row }">
            <span>{{ row.title }}.{{ row.documentType }}</span>
          </template>
        </ElTableColumn>
        <ElTableColumn
          prop="documentType"
          :label="$t('aiKnowledge.documentType')"
          width="180"
        />
        <ElTableColumn
          prop="chunkCount"
          :label="$t('aiKnowledge.knowledgeCount')"
          width="180"
        />
        <ElTableColumn :label="$t('aiKnowledge.createdModifyTime')" width="200">
          <template #default="{ row }">
            <div class="time-container">
              <span>{{ row.created }}</span>
              <span>{{ row.modified }}</span>
            </div>
          </template>
        </ElTableColumn>
        <ElTableColumn
          fixed="right"
          :label="$t('common.handle')"
          min-width="120"
        >
          <template #default="scope">
            <ElButton link type="primary" @click="handleView(scope.row)">
              <ElIcon class="mr-1">
                <View />
              </ElIcon>
              {{ $t('button.view') }}
            </ElButton>
            <ElButton link type="primary" @click="handleDownload(scope.row)">
              <ElIcon class="mr-1">
                <Download />
              </ElIcon>
              {{ $t('button.download') }}
            </ElButton>
            <ElButton link type="danger" @click="handleDelete(scope.row)">
              <ElIcon class="mr-1">
                <Delete />
              </ElIcon>
              {{ $t('button.delete') }}
            </ElButton>
          </template>
        </ElTableColumn>
      </ElTable>
    </template>
  </PageData>
</template>

<style scoped>
.time-container {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
</style>
