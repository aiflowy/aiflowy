<script setup lang="ts">
import { reactive, ref } from 'vue';

import { $t } from '@aiflowy/locales';

import {
  ElForm,
  ElFormItem,
  ElInput,
  ElOption,
  ElSelect,
  ElSlider,
} from 'element-plus';

const formRef = ref();
const form = reactive({
  fileType: 'doc',
  splitterName: 'SimpleDocumentSplitter',
  chunkSize: 512,
  overlapSize: 128,
  regex: '',
  rowsPerChunk: 0,
});
const fileTypes = [
  {
    label: $t('aiKnowledge.splitterDoc.document'),
    value: 'doc',
  },
];
const splitterNames = [
  {
    label: $t('aiKnowledge.splitterDoc.simpleDocumentSplitter'),
    value: 'SimpleDocumentSplitter',
  },
  {
    label: $t('aiKnowledge.splitterDoc.simpleTokenizeSplitter'),
    value: 'SimpleTokenizeSplitter',
  },
  {
    label: $t('aiKnowledge.splitterDoc.regexDocumentSplitter'),
    value: 'RegexDocumentSplitter',
  },
];
const rules = {
  name: [
    { required: true, message: 'Please input Activity name', trigger: 'blur' },
  ],
  region: [
    {
      required: true,
      message: 'Please select Activity zone',
      trigger: 'change',
    },
  ],
};
defineExpose({
  getSplitterFormValues() {
    return form;
  },
});
</script>

<template>
  <div class="splitter-doc-container">
    <ElForm
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="auto"
      class="custom-form"
    >
      <ElFormItem
        :label="$t('aiKnowledge.splitterDoc.fileType')"
        prop="fileType"
      >
        <ElSelect v-model="form.fileType">
          <ElOption
            v-for="item in fileTypes"
            :key="item.value"
            v-bind="item"
            :label="item.label"
          />
        </ElSelect>
      </ElFormItem>
      <ElFormItem
        :label="$t('aiKnowledge.splitterDoc.splitterName')"
        prop="splitterName"
      >
        <ElSelect v-model="form.splitterName">
          <ElOption
            v-for="item in splitterNames"
            :key="item.value"
            v-bind="item"
            :label="item.label"
          />
        </ElSelect>
      </ElFormItem>
      <ElFormItem
        :label="$t('aiKnowledge.splitterDoc.chunkSize')"
        v-if="
          form.splitterName === 'SimpleDocumentSplitter' ||
          form.splitterName === 'SimpleTokenizeSplitter'
        "
        prop="chunkSize"
      >
        <ElSlider v-model="form.chunkSize" show-input :max="2048" />
      </ElFormItem>
      <ElFormItem
        :label="$t('aiKnowledge.splitterDoc.overlapSize')"
        v-if="
          form.splitterName === 'SimpleDocumentSplitter' ||
          form.splitterName === 'SimpleTokenizeSplitter'
        "
        prop="overlapSize"
      >
        <ElSlider v-model="form.overlapSize" show-input :max="2048" />
      </ElFormItem>
      <ElFormItem
        :label="$t('aiKnowledge.splitterDoc.regex')"
        prop="regex"
        v-if="form.splitterName === 'RegexDocumentSplitter'"
      >
        <ElInput v-model="form.regex" />
      </ElFormItem>
    </ElForm>
  </div>
</template>

<style scoped>
.splitter-doc-container {
  height: 100%;
  width: 100%;
  align-items: center;
  display: flex;
  justify-content: center;
}
.custom-form {
  width: 500px;
}
.custom-form :deep(.el-input),
.custom-form :deep(.ElSelect) {
  width: 100%;
}
</style>
