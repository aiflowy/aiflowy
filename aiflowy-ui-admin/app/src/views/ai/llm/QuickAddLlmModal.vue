<script setup lang="ts">
import { defineEmits, nextTick, onMounted, reactive, ref } from 'vue';

import { $t } from '@aiflowy/locales';

import {
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
  ElOption,
  ElSelect,
} from 'element-plus';

import { getLlmBrandList, quickAddLlm } from '#/api/ai/llm';

interface LlmFormType {
  brand: string;
  apiKey: string;
}
interface brandDataType {
  key: string;
  title: string;
}

const emit = defineEmits(['success']);
const quickAddLlmDialog = ref(false);
const openQuickAddLlmDialog = () => {
  nextTick(() => {
    llmFormRef.value?.resetFields();
  });
  quickAddLlmDialog.value = true;
};

const llmForm: LlmFormType = reactive({
  brand: '',
  apiKey: '',
});
const llmFormRef = ref();
defineExpose({
  openQuickAddLlmDialog,
});
const handleConfirm = () => {
  llmFormRef.value.validate().then(() => {
    quickAddLlm(JSON.stringify(llmForm)).then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('message.saveOkMessage'));
        quickAddLlmDialog.value = false;
        emit('success');
        quickAddLlmDialog.value = false;
      }
    });
  });
};
const rules = {
  brand: [
    {
      required: true,
      message: $t('llm.llmModal.BrandRequired'),
      trigger: 'blur',
    },
  ],
  llmApiKey: [
    {
      required: true,
      message: $t('llm.llmModal.ApiKeyRequired'),
      trigger: 'blur',
    },
  ],
};
const brandListData = ref<brandDataType[]>([]);
onMounted(() => {
  getLlmBrandList().then((res) => {
    brandListData.value = res.data;
  });
});
</script>

<template>
  <ElDialog
    v-model="quickAddLlmDialog"
    :title="$t('llm.llmModal.QuickAddLlmModal')"
    width="700"
    align-center
  >
    <ElForm
      ref="llmFormRef"
      style="width: 100%; margin-top: 20px"
      :model="llmForm"
      :rules="rules"
      label-width="auto"
    >
      <ElFormItem
        :label="$t('llm.filed.brand')"
        prop="brand"
        label-position="right"
      >
        <ElSelect
          v-model="llmForm.brand"
          :placeholder="$t('llm.placeholder.brand')"
        >
          <ElOption
            v-for="item in brandListData"
            :key="item.key"
            :label="item.title"
            :value="item.key"
          />
        </ElSelect>
      </ElFormItem>

      <ElFormItem
        :label="$t('llm.filed.llmApiKey')"
        prop="apiKey"
        label-position="right"
      >
        <ElInput v-model="llmForm.apiKey" />
      </ElFormItem>
    </ElForm>

    <template #footer>
      <div class="dialog-footer">
        <ElButton @click="quickAddLlmDialog = false">
          {{ $t('button.cancel') }}
        </ElButton>
        <ElButton type="primary" @click="handleConfirm">
          {{ $t('button.confirm') }}
        </ElButton>
      </div>
    </template>
  </ElDialog>
</template>

<style scoped></style>
