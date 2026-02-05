<script setup lang="ts">
import type { BotInfo } from '@aiflowy/types';

import type { SaveBotParams, UpdateBotParams } from '#/api/ai/bot';

import { ref } from 'vue';

import { $t } from '@aiflowy/locales';

import {
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
} from 'element-plus';
import { tryit } from 'radash';

import { saveBot, updateBotApi } from '#/api/ai/bot';
import DictSelect from '#/components/dict/DictSelect.vue';
import UploadAvatar from '#/components/upload/UploadAvatar.vue';

const emit = defineEmits(['success']);

const initialFormData = {
  icon: '',
  title: '',
  alias: '',
  description: '',
  categoryId: '',
  status: 1,
};
const dialogVisible = ref(false);
const dialogType = ref<'create' | 'edit'>('create');
const formRef = ref<InstanceType<typeof ElForm>>();
const formData = ref<SaveBotParams | UpdateBotParams>(initialFormData);
const rules = {
  title: [{ required: true, message: $t('message.required'), trigger: 'blur' }],
  alias: [{ required: true, message: $t('message.required'), trigger: 'blur' }],
};
const loading = ref(false);

const handleSubmit = async () => {
  loading.value = true;

  const [err, res] = await tryit(
    dialogType.value === 'create' ? saveBot : updateBotApi,
  )(formData.value as any);

  if (!err && res.errorCode === 0) {
    emit('success');
    ElMessage.success($t('message.saveOkMessage'));
    dialogVisible.value = false;
  }
  loading.value = false;
};

defineExpose({
  open(type: typeof dialogType.value, bot?: BotInfo) {
    formData.value = bot
      ? {
          id: bot.id,
          icon: bot.icon,
          title: bot.title,
          alias: bot.alias,
          description: bot.description,
          categoryId: bot.categoryId,
          status: bot.status,
        }
      : initialFormData;
    dialogType.value = type;
    dialogVisible.value = true;
  },
});
</script>

<template>
  <ElDialog
    v-model="dialogVisible"
    :title="`${$t(`button.${dialogType}`)}${$t('bot.chatAssistant')}`"
    draggable
    align-center
  >
    <ElForm ref="formRef" :model="formData" :rules="rules" label-width="150px">
      <ElFormItem :label="$t('common.avatar')" prop="icon">
        <UploadAvatar v-model="formData.icon" />
      </ElFormItem>
      <ElFormItem prop="categoryId" :label="$t('aiWorkflow.categoryId')">
        <DictSelect v-model="formData.categoryId" dict-code="aiBotCategory" />
      </ElFormItem>
      <ElFormItem :label="$t('aiWorkflow.title')" prop="title">
        <ElInput v-model="formData.title" />
      </ElFormItem>
      <ElFormItem :label="$t('plugin.alias')" prop="alias">
        <ElInput v-model="formData.alias" />
      </ElFormItem>
      <ElFormItem :label="$t('plugin.description')" prop="description">
        <ElInput type="textarea" :rows="3" v-model="formData.description" />
      </ElFormItem>
      <ElFormItem prop="status" :label="$t('aiWorkflow.status')">
        <DictSelect v-model="formData.status" dict-code="showOrNot" />
      </ElFormItem>
    </ElForm>

    <template #footer>
      <ElButton @click="dialogVisible = false">
        {{ $t('button.cancel') }}
      </ElButton>
      <ElButton
        type="primary"
        :loading="loading"
        :disabled="loading"
        @click="handleSubmit"
      >
        {{ $t('button.save') }}
      </ElButton>
    </template>
  </ElDialog>
</template>
