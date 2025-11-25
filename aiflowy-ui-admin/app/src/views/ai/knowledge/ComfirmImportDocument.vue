<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';
import { useRoute } from 'vue-router';

import { $t } from '@aiflowy/locales';

import { ElTable, ElTableColumn, ElTag } from 'element-plus';

import { api } from '#/api/request';

const props = defineProps({
  filesList: {
    default: () => [],
    type: Array<any>,
  },
  splitterParams: {
    default: () => {},
    type: Object,
  },
});

const route = useRoute();

const knowledgeIdRef = ref<string>((route.query.id as string) || '');
const localFilesList = ref<any[]>([]);
watch(
  () => props.filesList,
  (newVal) => {
    localFilesList.value = [...newVal];
  },
  { immediate: true },
);
defineExpose({
  handleSave() {
    localFilesList.value.forEach((file, index) => {
      saveDoc(file.filePath, 'saveText', file.fileName, index);
    });
  },
});
onMounted(() => {
  console.log('fliesList');
  console.log(props.filesList);
  console.log(props.splitterParams);
});

function saveDoc(
  filePath: string,
  operation: string,
  fileOriginName: string,
  index: number,
) {
  api
    .post(
      '/api/v1/aiDocument/saveText',
      {},
      {
        params: {
          filePath,
          operation,
          knowledgeId: knowledgeIdRef.value,
          fileOriginName,
          ...props.splitterParams,
        },
      },
    )
    .then((res) => {
      if (res.errorCode === 0) {
        localFilesList.value[index].progressUpload = 'success';
      }
      console.log('保存结果');
      console.log(res);
    });
}
</script>

<template>
  <div class="import-doc-file-list">
    <ElTable :data="localFilesList" size="large" style="width: 100%">
      <ElTableColumn
        prop="fileName"
        :label="$t('aiKnowledge.importDoc.fileName')"
        width="250"
      />
      <ElTableColumn
        prop="progressUpload"
        :label="$t('aiKnowledge.splitterDoc.uploadStatus')"
      >
        <template #default="{ row }">
          <ElTag type="success" v-if="row.progressUpload === 'success'">
            {{ $t('aiKnowledge.splitterDoc.completed') }}
          </ElTag>
          <ElTag type="primary" v-else>
            {{ $t('aiKnowledge.splitterDoc.pendingUpload') }}
          </ElTag>
        </template>
      </ElTableColumn>
    </ElTable>
  </div>
</template>

<style scoped>
.import-doc-file-list {
  width: 100%;
}
</style>
