<script lang="ts" setup>
import type { UploadProps } from 'element-plus';

import { defineEmits, ref, watch } from 'vue';

import { useAppConfig } from '@aiflowy/hooks';
import { useAccessStore } from '@aiflowy/stores';

import { Plus } from '@element-plus/icons-vue';
import { ElIcon, ElImage, ElMessage, ElUpload } from 'element-plus';

const props = defineProps({
  action: {
    type: String,
    default: '/api/v1/commons/upload',
  },
  fileSize: {
    type: Number,
    default: 2,
  },
  allowedImageTypes: {
    type: Array<string>,
    default: () => ['image/gif', 'image/jpeg', 'image/png', 'image/webp'],
  },
  modelValue: { type: String, default: '' },
});

const emit = defineEmits(['success', 'update:modelValue']);
const accessStore = useAccessStore();
const headers = ref({
  'aiflowy-token': accessStore.accessToken,
});

const { apiURL } = useAppConfig(import.meta.env, import.meta.env.PROD);
const localImageUrl = ref(props.modelValue);
const handleAvatarSuccess: UploadProps['onSuccess'] = (
  _response,
  uploadFile,
) => {
  localImageUrl.value = URL.createObjectURL(uploadFile.raw!);
  emit('success', _response.data.path);
  emit('update:modelValue', _response.data.path);
};

watch(
  () => props.modelValue,
  (newVal) => {
    localImageUrl.value = newVal;
  },
  { immediate: true },
);

const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (!props.allowedImageTypes.includes(rawFile.type)) {
    const formatTypes = props.allowedImageTypes
      .map((type: string) => {
        const parts = type.split('/');
        return parts[1] ? parts[1].toUpperCase() : '';
      })
      .filter(Boolean);
    ElMessage.error(`头像只能是${formatTypes.join(', ')}格式`);
    return false;
  } else if (rawFile.size / 1024 / 1024 > props.fileSize) {
    ElMessage.error(`头像限制 ${props.fileSize} M`);
    return false;
  }
  return true;
};
</script>

<template>
  <ElUpload
    class="avatar-uploader"
    :action="`${apiURL}${props.action}`"
    :headers="headers"
    :show-file-list="false"
    :on-success="handleAvatarSuccess"
    :before-upload="beforeAvatarUpload"
  >
    <ElImage
      v-if="localImageUrl"
      :src="localImageUrl"
      class="avatar"
      fit="cover"
    />
    <ElIcon v-else class="avatar-uploader-icon"><Plus /></ElIcon>
  </ElUpload>
</template>

<style scoped>
.avatar-uploader .avatar {
  width: 100px;
  height: 100px;
  display: block;
}
</style>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: var(--el-text-color-secondary);
  width: 100px;
  height: 100px;
  text-align: center;
}
</style>
