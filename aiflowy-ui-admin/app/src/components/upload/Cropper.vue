<script setup lang="ts">
import type { UploadProps, UploadRequestHandler } from 'element-plus';

import { computed, ref, watch } from 'vue';
import { VueCropper } from 'vue-cropper';
import 'vue-cropper/dist/index.css';

import { useAccessStore } from '@aiflowy/stores';

import { Delete, Plus, Refresh } from '@element-plus/icons-vue';
import {
  ElButton,
  ElDialog,
  ElIcon,
  ElImage,
  ElMessage,
  ElUpload,
} from 'element-plus';

import { api } from '#/api/request';

// 定义组件props
interface Props {
  modelValue?: string; // 双向绑定的图片URL
  crop?: boolean; // 是否启用裁剪
  action?: string; // 上传地址
  headers?: Record<string, string>; // 上传请求头
  data?: Record<string, any>; // 上传额外数据
  cropConfig?: Partial<CropConfig>; // 裁剪配置
  limit?: number; // 文件大小限制(MB)
}

interface CropConfig {
  title: string;
  outputSize: number;
  outputType: string;
  info: boolean;
  full: boolean;
  fixed: boolean;
  fixedNumber: [number, number];
  canMove: boolean;
  canMoveBox: boolean;
  fixedBox: boolean;
  original: boolean;
  autoCrop: boolean;
  autoCropWidth: number;
  autoCropHeight: number;
  centerBox: boolean;
  high: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  action: '/api/v1/commons/upload',
  crop: false,
  headers: () => ({}),
  data: () => ({}),
  cropConfig: () => ({}),
  limit: 5,
});

const emit = defineEmits<{
  'update:modelValue': [value: string];
  uploadError: [error: Error];
  uploadSuccess: [url: string];
}>();

const accessStore = useAccessStore();
const headers = computed(() => ({
  'aiflowy-token': accessStore.accessToken,
  'Content-Type': 'multipart/form-data',
  ...props.headers,
}));

// 默认裁剪配置
const defaultCropConfig: CropConfig = {
  title: '图片裁剪',
  outputSize: 1,
  outputType: 'png',
  info: true,
  full: false,
  fixed: false,
  fixedNumber: [1, 1],
  canMove: false,
  canMoveBox: true,
  fixedBox: false,
  original: false,
  autoCrop: true,
  autoCropWidth: 200,
  autoCropHeight: 200,
  centerBox: true,
  high: true,
};

// refs
const uploadRef = ref<InstanceType<typeof ElUpload>>();
const cropperRef = ref<InstanceType<typeof VueCropper>>();
const showCropDialog = ref(false);
const cropImageUrl = ref('');
const uploading = ref(false);
const currentFile = ref<File | null>(null);

// 合并裁剪配置
const mergedCropConfig = computed(() => ({
  ...defaultCropConfig,
  ...props.cropConfig,
}));

// 触发上传 - 修复：直接触发上传组件的点击事件
const triggerUpload = () => {
  // 创建隐藏的input元素来触发文件选择
  const input = document.createElement('input');
  input.type = 'file';
  input.accept = 'image/*';
  input.style.display = 'none';

  input.addEventListener('change', (e) => {
    const file = (e.target as HTMLInputElement).files?.[0];
    if (file) {
      handleFileSelect(file);
    }
    input.remove();
  });

  document.body.append(input);
  input.click();
};

// 处理文件选择
const handleFileSelect = (file: File) => {
  // 验证文件
  const isImage = file.type.startsWith('image/');
  if (!isImage) {
    ElMessage.error('只能上传图片文件!');
    return;
  }

  const isLtLimit = file.size / 1024 / 1024 < props.limit;
  if (!isLtLimit) {
    ElMessage.error(`图片大小不能超过 ${props.limit}MB!`);
    return;
  }

  currentFile.value = file;

  // 如果需要裁剪，显示裁剪对话框
  if (props.crop) {
    cropImageUrl.value = URL.createObjectURL(file);
    showCropDialog.value = true;
  } else {
    // 直接上传
    uploadFile(file);
  }
};

// 上传前验证
const beforeUpload: UploadProps['beforeUpload'] = (rawFile) => {
  const isImage = rawFile.type.startsWith('image/');
  if (!isImage) {
    ElMessage.error('只能上传图片文件!');
    return false;
  }

  const isLtLimit = rawFile.size / 1024 / 1024 < props.limit;
  if (!isLtLimit) {
    ElMessage.error(`图片大小不能超过 ${props.limit}MB!`);
    return false;
  }

  // 如果需要裁剪，显示裁剪对话框
  if (props.crop) {
    currentFile.value = rawFile;
    cropImageUrl.value = URL.createObjectURL(rawFile);
    showCropDialog.value = true;
    return false; // 阻止自动上传
  }

  return true;
};

// 统一上传方法
const uploadFile = async (file: File) => {
  try {
    uploading.value = true;

    const formData = new FormData();
    formData.append('file', file);

    Object.entries(props.data).forEach(([key, value]) => {
      formData.append(key, value);
    });

    const response = await api.post(props.action, formData, {
      headers: headers.value,
    });

    if (response.errorCode !== 0) {
      throw new Error(`上传失败: ${response.message}`);
    }

    const imageUrl = response.data.path;

    if (!imageUrl) {
      throw new Error('上传成功但未返回图片URL');
    }

    emit('update:modelValue', imageUrl);
    emit('uploadSuccess', imageUrl);

    ElMessage.success('上传成功!');
  } catch (error) {
    const err = error instanceof Error ? error : new Error('上传失败');
    emit('uploadError', err);
    ElMessage.error(err.message);
  } finally {
    uploading.value = false;
    currentFile.value = null;
  }
};

// 处理上传
const handleUpload: UploadRequestHandler = async (options) => {
  const { file, onSuccess } = options;
  await uploadFile(file);
  onSuccess({}); // 调用成功回调
};

// 处理裁剪 - 修复：使用正确的裁剪逻辑
const handleCrop = () => {
  if (!cropperRef.value) {
    ElMessage.error('裁剪器未初始化');
    return;
  }

  cropperRef.value.getCropBlob(async (blob: Blob | null) => {
    if (!blob) {
      ElMessage.error('裁剪失败，无法获取裁剪后的图片');
      return;
    }

    try {
      uploading.value = true;

      // 创建文件对象，保留原始文件名但使用裁剪后的内容
      const originalName = currentFile.value?.name || 'cropped-image';
      const fileExtension = originalName.split('.').pop() || 'png';
      const fileName = `cropped-${Date.now()}.${fileExtension}`;

      const file = new File([blob], fileName, { type: blob.type });

      await uploadFile(file);
      showCropDialog.value = false;
    } catch (error) {
      const err = error instanceof Error ? error : new Error('上传失败');
      emit('uploadError', err);
      ElMessage.error(err.message);
    } finally {
      uploading.value = false;
    }
  });
};

// 删除图片
const handleRemove = () => {
  emit('update:modelValue', '');
  ElMessage.success('删除成功!');
};

// 清理URL对象
watch(showCropDialog, (newVal) => {
  if (!newVal && cropImageUrl.value) {
    URL.revokeObjectURL(cropImageUrl.value);
    cropImageUrl.value = '';
  }
});
</script>

<template>
  <div class="image-upload-container">
    <!-- 上传按钮 -->
    <div v-if="!modelValue" class="upload-area">
      <ElUpload
        ref="uploadRef"
        class="avatar-uploader"
        action="#"
        :show-file-list="false"
        :before-upload="beforeUpload"
        :http-request="handleUpload"
        accept="image/*"
      >
        <ElIcon class="avatar-uploader-icon"><Plus /></ElIcon>
      </ElUpload>
    </div>

    <!-- 图片预览 -->
    <div v-else class="preview-area">
      <div class="preview-container">
        <ElImage
          :src="modelValue"
          :preview-src-list="[modelValue]"
          fit="cover"
          class="preview-image"
          :zoom-rate="1.2"
          :max-scale="7"
          :min-scale="0.2"
          hide-on-click-modal
        />
        <div class="preview-actions">
          <ElButton @click="triggerUpload">
            <ElIcon><Refresh /></ElIcon>
          </ElButton>
          <ElButton @click="handleRemove">
            <ElIcon><Delete /></ElIcon>
          </ElButton>
        </div>
      </div>
    </div>

    <!-- 裁剪对话框 -->
    <ElDialog
      v-model="showCropDialog"
      :title="mergedCropConfig.title"
      width="800px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div class="cropper-container">
        <VueCropper
          ref="cropperRef"
          :img="cropImageUrl"
          :output-size="mergedCropConfig.outputSize"
          :output-type="mergedCropConfig.outputType"
          :info="mergedCropConfig.info"
          :full="mergedCropConfig.full"
          :fixed="mergedCropConfig.fixed"
          :fixed-number="mergedCropConfig.fixedNumber"
          :can-move="mergedCropConfig.canMove"
          :can-move-box="mergedCropConfig.canMoveBox"
          :fixed-box="mergedCropConfig.fixedBox"
          :original="mergedCropConfig.original"
          :auto-crop="mergedCropConfig.autoCrop"
          :auto-crop-width="mergedCropConfig.autoCropWidth"
          :auto-crop-height="mergedCropConfig.autoCropHeight"
          :center-box="mergedCropConfig.centerBox"
          :high="mergedCropConfig.high"
          mode="cover"
        />
      </div>

      <template #footer>
        <span class="dialog-footer">
          <ElButton @click="showCropDialog = false" :disabled="uploading">
            取消
          </ElButton>
          <ElButton type="primary" @click="handleCrop" :loading="uploading">
            {{ uploading ? '上传中...' : '确认裁剪' }}
          </ElButton>
        </span>
      </template>
    </ElDialog>
  </div>
</template>

<style scoped>
/* 样式保持不变 */
.image-upload-container {
  display: inline-block;
}

.upload-area {
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar-uploader {
  width: 100px;
  height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-area {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.preview-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.preview-image {
  width: 100px;
  height: 100px;
  border-radius: 6px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.preview-actions {
  display: flex;
  gap: 5px;
}

.cropper-container {
  height: 400px;
  background: #f5f7fa;
  display: flex;
  justify-content: center;
  align-items: center;
}

.edit-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 20px 0;
}

.edit-actions .el-button {
  justify-content: flex-start;
}
</style>
