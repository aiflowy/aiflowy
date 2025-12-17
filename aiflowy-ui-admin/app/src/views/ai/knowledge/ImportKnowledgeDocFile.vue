<script setup lang="ts">
import { ref } from 'vue';

import { $t } from '@aiflowy/locales';

import { Back } from '@element-plus/icons-vue';
import {
  ElButton,
  ElMessage,
  ElPagination,
  ElStep,
  ElSteps,
} from 'element-plus';

import ComfirmImportDocument from '#/views/ai/knowledge/ComfirmImportDocument.vue';
import ImportKnowledgeFileContainer from '#/views/ai/knowledge/ImportKnowledgeFileContainer.vue';
import SegmenterDoc from '#/views/ai/knowledge/SegmenterDoc.vue';
import SplitterDocPreview from '#/views/ai/knowledge/SplitterDocPreview.vue';

const emits = defineEmits(['importBack']);
const back = () => {
  emits('importBack');
};
const files = ref([]);
const splitterParams = ref({});
const activeStep = ref(0);
const fileUploadRef = ref();
const confirmImportRef = ref();
const segmenterDocRef = ref();
const pagination = ref({
  pageSize: 10,
  currentPage: 1,
  total: 0,
});
const goToNextStep = () => {
  // 第一步文件上传成功后，获取子组件的数据
  if (activeStep.value === 1 && fileUploadRef.value) {
    files.value = fileUploadRef.value.getFilesData(); // 调用子组件暴露的方法
    // console.log('父组件获取到子组件的 fileData：', filesData);
  }
  if (activeStep.value === 2 && segmenterDocRef.value) {
    splitterParams.value = segmenterDocRef.value.getSplitterFormValues(); // 调用子组件暴露的方法
  }

  activeStep.value += 1;
};
const goToPreviousStep = () => {
  activeStep.value -= 1;
};
const handleSizeChange = (val: number) => {
  pagination.value.pageSize = val;
};
const handleCurrentChange = (val: number) => {
  pagination.value.currentPage = val;
};
const handleTotalUpdate = (newTotal: number) => {
  pagination.value.total = newTotal; // 同步到父组件的 pagination.total
};
const loadingSave = ref(false);
const confirmImport = () => {
  loadingSave.value = true;
  // 确认导入
  confirmImportRef.value.handleSave();
};
const finishImport = () => {
  loadingSave.value = false;
  ElMessage.success($t('aiKnowledge.splitterDoc.importSuccess'));
  emits('importBack');
};
</script>

<template>
  <div class="imp-doc-kno-container">
    <div class="imp-doc-header">
      <ElButton @click="back" :icon="Back">
        {{ $t('button.back') }}
      </ElButton>
    </div>
    <div class="imp-doc-kno-content">
      <div class="rounded-lg bg-[var(--table-header-bg-color)] py-5">
        <ElSteps :active="activeStep" align-center>
          <ElStep>
            <template #icon>
              <div class="flex items-center gap-2">
                <div class="h-8 w-8 rounded-full bg-[var(--step-item-bg)]">
                  <span class="text-accent-foreground text-sm/8">1</span>
                </div>
                <span class="text-base">{{
                  $t('aiKnowledge.importDoc.fileUpload')
                }}</span>
              </div>
            </template>
          </ElStep>
          <ElStep>
            <template #icon>
              <div class="flex items-center gap-2">
                <div class="h-8 w-8 rounded-full bg-[var(--step-item-bg)]">
                  <span class="text-accent-foreground text-sm/8">2</span>
                </div>
                <span class="text-base">{{
                  $t('aiKnowledge.importDoc.parameterSettings')
                }}</span>
              </div>
            </template>
          </ElStep>
          <ElStep>
            <template #icon>
              <div class="flex items-center gap-2">
                <div class="h-8 w-8 rounded-full bg-[var(--step-item-bg)]">
                  <span class="text-accent-foreground text-sm/8">3</span>
                </div>
                <span class="text-base">{{
                  $t('aiKnowledge.importDoc.segmentedPreview')
                }}</span>
              </div>
            </template>
          </ElStep>
          <ElStep>
            <template #icon>
              <div class="flex items-center gap-2">
                <div class="h-8 w-8 rounded-full bg-[var(--step-item-bg)]">
                  <span class="text-accent-foreground text-sm/8">4</span>
                </div>
                <span class="text-base">{{
                  $t('aiKnowledge.importDoc.confirmImport')
                }}</span>
              </div>
            </template>
          </ElStep>
        </ElSteps>
      </div>

      <div style="margin-top: 20px">
        <!--      文件上传导入-->
        <div class="knw-file-upload" v-if="activeStep === 0">
          <ImportKnowledgeFileContainer ref="fileUploadRef" />
        </div>
        <!--      分割参数设置-->
        <div class="knw-file-splitter" v-if="activeStep === 1">
          <SegmenterDoc ref="segmenterDocRef" />
        </div>
        <!--        分割预览-->
        <div class="knw-file-preview" v-if="activeStep === 2">
          <SplitterDocPreview
            :flies-list="files"
            :splitter-params="splitterParams"
            :page-number="pagination.currentPage"
            :page-size="pagination.pageSize"
            @update-total="handleTotalUpdate"
          />
        </div>
        <!--        确认导入-->
        <div class="knw-file-confirm" v-if="activeStep === 3">
          <ComfirmImportDocument
            :splitter-params="splitterParams"
            :files-list="files"
            ref="confirmImportRef"
            @loading-finish="finishImport"
          />
        </div>
      </div>
    </div>
    <div style="height: 40px"></div>
    <div class="imp-doc-footer">
      <div v-if="activeStep === 3" class="imp-doc-page-container">
        <ElPagination
          :page-sizes="[10, 20]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
      <ElButton @click="goToPreviousStep" type="primary" v-if="activeStep > 1">
        {{ $t('button.previousStep') }}
      </ElButton>
      <ElButton @click="goToNextStep" type="primary" v-if="activeStep < 4">
        {{ $t('button.nextStep') }}
      </ElButton>
      <ElButton
        @click="confirmImport"
        type="primary"
        v-if="activeStep === 4"
        :loading="loadingSave"
        :disabled="loadingSave"
      >
        {{ $t('button.startImport') }}
      </ElButton>
    </div>
  </div>
</template>

<style scoped>
.imp-doc-kno-container {
  position: relative;
  height: 100%;
  background-color: var(--el-bg-color);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.imp-doc-kno-content {
  flex: 1;
  padding-top: 20px;
  overflow: scroll;
}
.imp-doc-footer {
  position: absolute;
  bottom: 20px;
  right: 20px;
  display: flex;
  height: 40px;
  background-color: var(--el-bg-color);
  align-items: center;
  justify-content: flex-end;
}
.knw-file-preview {
  flex: 1;
  overflow: scroll;
}
.imp-doc-page-container {
  margin-right: 12px;
}
.knw-file-confirm {
  width: 100%;
}

:deep(.el-step__head) {
  --step-item-bg: rgba(0, 0, 0, 0.06);
  --step-item-solid-bg: rgba(0, 0, 0, 0.15);
  --accent-foreground: rgba(0, 0, 0, 0.45);
}
:deep(.el-step__head:where(.dark, .dark *)) {
  --step-item-bg: var(--el-text-color-placeholder);
  --step-item-solid-bg: var(--el-text-color-placeholder);
  --accent-foreground: var(--primary-foreground);
}
:deep(.el-step__head.is-finish) {
  --step-item-bg: hsl(var(--primary));
  --step-item-solid-bg: hsl(var(--primary));
  --accent-foreground: var(--primary-foreground);
}
:deep(.el-step__icon.is-icon) {
  width: 120px;
  background-color: var(--table-header-bg-color);
}
:deep(.el-step__line) {
  background-color: var(--step-item-solid-bg);
}
</style>
