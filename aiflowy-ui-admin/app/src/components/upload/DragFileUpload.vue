<script setup lang="ts">
import type { UploadProps } from 'element-plus';

import { defineEmits, ref } from 'vue';

import { useAppConfig } from '@aiflowy/hooks';
import { useAccessStore } from '@aiflowy/stores';

import { UploadFilled } from '@element-plus/icons-vue';
import { ElIcon, ElUpload } from 'element-plus';

const props = defineProps({
  action: {
    type: String,
    default: '/api/v1/commons/upload',
  },
});
const emit = defineEmits(['success', 'onChange']);
const accessStore = useAccessStore();
const headers = ref({
  'aiflowy-token': accessStore.accessToken,
});
const { apiURL } = useAppConfig(import.meta.env, import.meta.env.PROD);
const handleSuccess: UploadProps['onSuccess'] = (response) => {
  emit('success', response.data.path);
};
const handleChange: UploadProps['onChange'] = (file, fileList) => {
  emit('onChange', file, fileList);
};
</script>

<template>
  <ElUpload
    class="upload-demo"
    drag
    :headers="headers"
    :action="`${apiURL}${props.action}`"
    :on-success="handleSuccess"
    :on-change="handleChange"
    multiple
  >
    <ElIcon class="el-icon--upload"><UploadFilled /></ElIcon>
    <div class="el-upload__text">
      Drop file here or <em>click to upload</em>
    </div>
    <!--    <template #tip>-->
    <!--      <div class="el-upload__tip">-->
    <!--        jpg/png files with a size less than 500kb-->
    <!--      </div>-->
    <!--    </template>-->
  </ElUpload>
</template>
