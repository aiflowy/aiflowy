<script lang="ts" setup>
import type { UploadProps, UploadUserFile } from 'element-plus';

import { defineEmits, ref } from 'vue';

import { useAppConfig } from '@aiflowy/hooks';
import { useAccessStore } from '@aiflowy/stores';

import { ElButton, ElUpload } from 'element-plus';

import { $t } from '#/locales';

const props = defineProps({
  action: {
    type: String,
    default: '/api/v1/commons/upload',
  },
  tips: {
    type: String,
    default: '',
  },
  limit: {
    type: Number,
    default: 1,
  },
  multiple: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits([
  'success', // 文件上传成功
  'handleDelete',
  'handlePreview',
  'beforeUpload',
]);

const { apiURL } = useAppConfig(import.meta.env, import.meta.env.PROD);

const accessStore = useAccessStore();
const headers = ref({
  'aiflowy-token': accessStore.accessToken,
});
const fileList = ref<UploadUserFile[]>([]);

const beforeUpload: UploadProps['beforeUpload'] = (rawFile) => {
  emit('beforeUpload', rawFile);
};
const handleRemove: UploadProps['onRemove'] = (file, uploadFiles) => {
  emit('handleDelete', file, uploadFiles);
};
const handleSuccess: UploadProps['onSuccess'] = (response) => {
  emit('success', response.data.path);
};
</script>

<template>
  <ElUpload
    v-model:file-list="fileList"
    class="upload-demo"
    :headers="headers"
    :action="`${apiURL}${props.action}`"
    :multiple="props.multiple"
    :before-upload="beforeUpload"
    :on-remove="handleRemove"
    :limit="props.limit"
    :on-success="handleSuccess"
  >
    <ElButton type="primary">{{ $t('button.upload') }}</ElButton>
  </ElUpload>
</template>
