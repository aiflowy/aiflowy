<script setup lang="ts">
import { ref } from 'vue';

import { $t } from '@aiflowy/locales';

import { ElButton, ElProgress, ElTable, ElTableColumn } from 'element-plus';

import { formatFileSize } from '#/api/common/file';
import DragFileUpload from '#/components/upload/DragFileUpload.vue';

interface FileInfo {
  uid: string;
  fileName: string;
  progressUpload: number;
  fileSize: number;
  status: string;
  filePath: string;
}
const fileData = ref<FileInfo[]>([]);
const filesPath = ref([]);
defineExpose({
  getFilesData() {
    return fileData.value;
  },
});
function handleSuccess(response: any) {
  filesPath.value = response.data;
}
function handleChange(file: any) {
  const existingFile = fileData.value.find((item) => item.uid === file.uid);
  if (existingFile) {
    fileData.value = fileData.value.map((item) => {
      if (item.uid === file.uid) {
        return {
          ...item,
          fileSize: file.size,
          progressUpload: file.percentage,
          status: file.status,
          filePath: file?.response?.data?.path,
        };
      }
      return item;
    });
  } else {
    fileData.value.push({
      uid: file.uid,
      fileName: file.name,
      progressUpload: file.percentage,
      fileSize: file.size,
      status: file.status,
      filePath: file?.response?.data?.path,
    });
  }
}

function handleRemove(row: any) {
  fileData.value = fileData.value.filter((item) => item.uid !== row.uid);
}
</script>

<template>
  <div>
    <div>
      <DragFileUpload @success="handleSuccess" @on-change="handleChange" />
    </div>
    <div>
      <ElTable :data="fileData" style="width: 100%" size="large">
        <ElTableColumn
          prop="fileName"
          :label="$t('aiKnowledge.importDoc.fileName')"
          width="250"
        />
        <ElTableColumn
          prop="progressUpload"
          :label="$t('aiKnowledge.importDoc.progressUpload')"
          width="180"
        >
          <template #default="{ row }">
            <ElProgress
              :percentage="row.progressUpload"
              v-if="row.status === 'success'"
              status="success"
            />
            <ElProgress v-else :percentage="row.progressUpload" />
          </template>
        </ElTableColumn>
        <ElTableColumn
          prop="fileSize"
          :label="$t('aiKnowledge.importDoc.fileSize')"
        >
          <template #default="{ row }">
            <span>{{ formatFileSize(row.fileSize) }}</span>
          </template>
        </ElTableColumn>
        <ElTableColumn :label="$t('common.handle')">
          <template #default="{ row }">
            <ElButton type="danger" size="small" @click="handleRemove(row)">
              {{ $t('button.delete') }}
            </ElButton>
          </template>
        </ElTableColumn>
      </ElTable>
    </div>
  </div>
</template>

<style scoped></style>
