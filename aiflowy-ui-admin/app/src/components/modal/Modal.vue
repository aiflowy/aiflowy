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

interface basicFormType {
  brand: string;
  apiKey: string;
}
interface brandDataType {
  key: string;
  title: string;
}

const emit = defineEmits(['success']);
const dialogVisible = ref(false);
const openDialog = () => {
  nextTick(() => {
    basicFormRef.value?.resetFields();
  });
  dialogVisible.value = true;
};

const basicForm: basicFormType = reactive({});
const basicFormRef = ref();
defineExpose({
  openDialog,
});
const handleConfirm = () => {
  basicFormRef.value.validate().then(() => {
    quickAddLlm(JSON.stringify(basicForm)).then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('message.saveOkMessage'));
        dialogVisible.value = false;
        emit('success');
        dialogVisible.value = false;
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
    v-model="dialogVisible"
    :title="$t('button.add')"
    width="700"
    align-center
  >
    <ElForm
      ref="basicFormRef"
      style="width: 100%; margin-top: 20px"
      :model="basicForm"
      :rules="rules"
      label-width="auto"
    >
      <ElFormItem
        :label="$t('llm.filed.brand')"
        prop="brand"
        label-position="right"
      >
        <ElSelect
          v-model="basicForm.brand"
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
        <ElInput v-model="basicForm.apiKey" />
      </ElFormItem>
    </ElForm>

    <template #footer>
      <div class="dialog-footer">
        <ElButton @click="dialogVisible = false">
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
