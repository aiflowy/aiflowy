<script setup lang="ts">
import type { UploadFile } from 'element-plus';

import { onMounted, ref } from 'vue';

import { downloadFileFromBlob } from '@aiflowy/utils';

import {
  ElButton,
  ElDialog,
  ElMessage,
  ElMessageBox,
  ElUpload,
} from 'element-plus';

import { api } from '#/api/request';
import uploadIcon from '#/assets/datacenter/upload.png';
import { $t } from '#/locales';

const props = withDefaults(defineProps<BatchImportModalProps>(), {
  title: 'title',
});
const emit = defineEmits(['reload']);
export interface BatchImportModalProps {
  tableId: any;
  title?: string;
}
// vue
onMounted(() => {});
defineExpose({
  openDialog,
});
// variables
const dialogVisible = ref(false);
const downloadLoading = ref(false);
const fileList = ref<any[]>([]);
const currentFile = ref<File | null>();
const btnLoading = ref(false);
// functions
function openDialog() {
  dialogVisible.value = true;
}
function closeDialog() {
  fileList.value = [];
  currentFile.value = null;
  dialogVisible.value = false;
}
function onFileChange(uploadFile: UploadFile) {
  currentFile.value = uploadFile.raw;
  return false;
}
function downloadTemplate() {
  downloadLoading.value = true;
  api
    .download(`/api/v1/datacenterTable/getTemplate?tableId=${props.tableId}`)
    .then((res) => {
      downloadLoading.value = false;
      downloadFileFromBlob({
        fileName: 'template.xlsx',
        source: res,
      });
    });
}
function handleUpload() {
  if (!currentFile.value) {
    ElMessage.warning($t('datacenterTable.uploadDesc'));
    return;
  }
  const formData = new FormData();
  formData.append('file', currentFile.value);
  formData.append('tableId', props.tableId);
  btnLoading.value = true;
  api.postFile('/api/v1/datacenterTable/importData', formData).then((res) => {
    btnLoading.value = false;
    if (res.errorCode === 0) {
      const arr: any[] = res.data.errorRows;
      let html = '';
      for (const element of arr) {
        html += `<p>${JSON.stringify(element)}</p><br>`;
      }
      closeDialog();
      ElMessageBox.alert(
        `<strong>${$t('datacenterTable.totalNum')}：</strong>${res.data.totalCount}
        <strong>${$t('datacenterTable.successNum')}：</strong>${res.data.successCount}
        <strong>${$t('datacenterTable.failNum')}：</strong>${res.data.errorCount}<br>
        <strong>${$t('datacenterTable.failList')}：</strong>${html}`,
        $t('datacenterTable.importComplete'),
        {
          confirmButtonText: $t('message.ok'),
          dangerouslyUseHTMLString: true,
          callback: () => {
            emit('reload');
          },
        },
      );
    } else {
      ElMessage.error($t('message.fail'));
    }
  });
}
</script>

<template>
  <ElDialog
    v-model="dialogVisible"
    draggable
    :title="props.title"
    :before-close="closeDialog"
    :close-on-click-modal="false"
  >
    <ElUpload
      :file-list="fileList"
      drag
      action="#"
      accept=".xlsx,.xls,.csv"
      :auto-upload="false"
      :on-change="onFileChange"
      :limit="1"
    >
      <div class="flex flex-col items-center">
        <img alt="" :src="uploadIcon" class="h-12 w-12" />
        <div class="text-base font-medium">
          {{ $t('datacenterTable.uploadTitle') }}
        </div>
        <div class="desc text-[13px]">
          {{ $t('datacenterTable.uploadDesc') }}
        </div>
      </div>
    </ElUpload>
    <ElButton
      :disabled="downloadLoading"
      type="primary"
      link
      @click="downloadTemplate"
    >
      {{ $t('datacenterTable.downloadTemplate') }}
    </ElButton>
    <template #footer>
      <div class="dialog-footer">
        <ElButton @click="closeDialog">
          {{ $t('button.cancel') }}
        </ElButton>
        <ElButton
          :disabled="btnLoading"
          :loading="btnLoading"
          type="primary"
          @click="handleUpload"
        >
          {{ $t('button.confirm') }}
        </ElButton>
      </div>
    </template>
  </ElDialog>
</template>

<style scoped>
.desc {
  color: var(--el-text-color-secondary);
}
</style>
