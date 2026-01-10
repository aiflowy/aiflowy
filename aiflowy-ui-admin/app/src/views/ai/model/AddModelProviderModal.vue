<script setup lang="ts">
import { reactive, ref } from 'vue';

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

import { api } from '#/api/request';
import UploadAvatar from '#/components/upload/UploadAvatar.vue';
import { $t } from '#/locales';
import providerList from '#/views/ai/model/modelUtils/providerList.json';

const emit = defineEmits(['reload']);

const formDataRef = ref();

defineExpose({
  openAddDialog() {
    formDataRef.value?.resetFields();
    dialogVisible.value = true;
  },
  openEditDialog(item: any) {
    dialogVisible.value = true;
    isAdd.value = false;
    Object.assign(formData, item);
  },
});
const providerOptions =
  ref<Array<{ label: string; options: any; value: string }>>(providerList);
const isAdd = ref(true);
const dialogVisible = ref(false);
const formData = reactive({
  id: '',
  icon: '',
  providerName: '',
  providerType: '',
  apiKey: '',
  endpoint: '',
  chatPath: '',
  embedPath: '',
  rerankPath: '',
});
const closeDialog = () => {
  dialogVisible.value = false;
};
const rules = {
  providerName: [
    {
      required: true,
      message: $t('message.required'),
      trigger: 'blur',
    },
  ],
  providerType: [
    {
      required: true,
      message: $t('message.required'),
      trigger: 'blur',
    },
  ],
};
const btnLoading = ref(false);
const save = async () => {
  btnLoading.value = true;
  try {
    if (!isAdd.value) {
      api.post('/api/v1/modelProvider/update', formData).then((res) => {
        if (res.errorCode === 0) {
          ElMessage.success(res.message);
          emit('reload');
          closeDialog();
        }
      });
      return;
    }
    await formDataRef.value.validate();
    api.post('/api/v1/modelProvider/save', formData).then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success(res.message);
        emit('reload');
        closeDialog();
      }
    });
  } finally {
    btnLoading.value = false;
  }
};
const handleChangeProvider = (val: string) => {
  const tempProvider = providerList.find((item) => item.value === val);
  if (!tempProvider) {
    return;
  }
  formData.providerName = tempProvider.label;
  formData.endpoint = providerOptions.value.find(
    (item) => item.value === val,
  )?.options.llmEndpoint;
  formData.chatPath = providerOptions.value.find(
    (item) => item.value === val,
  )?.options.chatPath;
  formData.embedPath = providerOptions.value.find(
    (item) => item.value === val,
  )?.options.embedPath;
};
</script>

<template>
  <ElDialog
    v-model="dialogVisible"
    draggable
    :title="isAdd ? $t('button.add') : $t('button.edit')"
    :before-close="closeDialog"
    :close-on-click-modal="false"
    align-center
    width="482"
  >
    <ElForm
      label-width="100px"
      ref="formDataRef"
      :model="formData"
      status-icon
      :rules="rules"
    >
      <ElFormItem
        prop="icon"
        style="display: flex; align-items: center"
        :label="$t('llmProvider.icon')"
      >
        <UploadAvatar v-model="formData.icon" />
      </ElFormItem>
      <ElFormItem prop="providerName" :label="$t('llmProvider.providerName')">
        <ElInput v-model.trim="formData.providerName" />
      </ElFormItem>
      <ElFormItem prop="provider" :label="$t('llmProvider.apiType')">
        <ElSelect v-model="formData.providerType" @change="handleChangeProvider">
          <ElOption
            v-for="item in providerOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value || ''"
          />
        </ElSelect>
      </ElFormItem>
      <ElFormItem prop="apiKey" :label="$t('llmProvider.apiKey')">
        <ElInput v-model.trim="formData.apiKey" />
      </ElFormItem>
      <ElFormItem prop="endpoint" :label="$t('llmProvider.endpoint')">
        <ElInput v-model.trim="formData.endpoint" />
      </ElFormItem>
      <ElFormItem prop="chatPath" :label="$t('llmProvider.chatPath')">
        <ElInput v-model.trim="formData.chatPath" />
      </ElFormItem>
      <ElFormItem prop="rerankPath" :label="$t('llmProvider.rerankPath')">
        <ElInput v-model.trim="formData.rerankPath" />
      </ElFormItem>
      <ElFormItem prop="embedPath" :label="$t('llmProvider.embedPath')">
        <ElInput v-model.trim="formData.embedPath" />
      </ElFormItem>
    </ElForm>
    <template #footer>
      <ElButton @click="closeDialog">
        {{ $t('button.cancel') }}
      </ElButton>
      <ElButton
        type="primary"
        @click="save"
        :loading="btnLoading"
        :disabled="btnLoading"
      >
        {{ $t('button.save') }}
      </ElButton>
    </template>
  </ElDialog>
</template>

<style scoped>
.headers-container-reduce {
  align-items: center;
}
.addHeadersBtn {
  width: 100%;
  border-style: dashed;
  border-color: var(--el-color-primary);
  border-radius: 8px;
  margin-top: 8px;
}
.head-con-content {
  margin-bottom: 8px;
  align-items: center;
}
</style>
