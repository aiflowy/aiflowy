<script setup lang="ts">
import { ref } from 'vue';

import { $t } from '@aiflowy/locales';

import { Back } from '@element-plus/icons-vue';
import { ElButton, ElPagination, ElStep, ElSteps } from 'element-plus';

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
const activeStep = ref(1);
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
const confirmImport = () => {
  // 确认导入
  confirmImportRef.value.handleSave();
};
</script>

<template>
  <div class="imp-doc-container">
    <div class="imp-doc-header">
      <ElButton @click="back" :icon="Back">
        {{ $t('button.back') }}
      </ElButton>
    </div>
    <div class="imp-doc-content">
      <ElSteps :active="activeStep">
        <ElStep :title="$t('aiKnowledge.importDoc.fileUpload')" />
        <ElStep :title="$t('aiKnowledge.importDoc.parameterSettings')" />
        <ElStep :title="$t('aiKnowledge.importDoc.segmentedPreview')" />
        <ElStep :title="$t('aiKnowledge.importDoc.confirmImport')" />
      </ElSteps>

      <div style="margin-top: 20px">
        <!--      文件上传导入-->
        <div class="knw-file-upload" v-if="activeStep === 1">
          <ImportKnowledgeFileContainer ref="fileUploadRef" />
        </div>
        <!--      分割参数设置-->
        <div class="knw-file-splitter" v-if="activeStep === 2">
          <SegmenterDoc ref="segmenterDocRef" />
        </div>
        <!--        分割预览-->
        <div class="knw-file-preview" v-if="activeStep === 3">
          <SplitterDocPreview
            :flies-list="files"
            :splitter-params="splitterParams"
            :page-number="pagination.currentPage"
            :page-size="pagination.pageSize"
            @update-total="handleTotalUpdate"
          />
        </div>
        <!--        确认导入-->
        <div class="knw-file-confirm" v-if="activeStep === 4">
          <ComfirmImportDocument
            :splitter-params="splitterParams"
            :files-list="files"
            ref="confirmImportRef"
          />
        </div>
      </div>
    </div>

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
      <ElButton @click="confirmImport" type="primary" v-if="activeStep === 4">
        {{ $t('button.startImport') }}
      </ElButton>
    </div>
  </div>
</template>

<style scoped>
.imp-doc-container {
  height: 100%;
  background-color: var(--el-color-white);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.imp-doc-content {
  height: 100%;
  padding-top: 20px;
}
.imp-doc-footer {
  display: flex;
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
</style>
