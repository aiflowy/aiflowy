<script setup lang="ts">
import type { UploadProps, UploadRequestHandler } from 'element-plus';

import { computed, ref, watch } from 'vue';
import { VueCropper } from 'vue-cropper';
import 'vue-cropper/dist/index.css';

import { useAccessStore } from '@aiflowy/stores';

import { Delete, Edit, Plus } from '@element-plus/icons-vue';
import {
  ElButton,
  ElDialog,
  ElIcon,
  ElImage,
  ElMessage,
  ElTag,
  ElUpload,
} from 'element-plus';

import { api } from '#/api/request';

// 定义组件props
interface Props {
  modelValue?: string[]; // 双向绑定的图片URL数组
  crop?: boolean; // 是否启用裁剪
  action?: string; // 上传地址
  headers?: Record<string, string>; // 上传请求头
  data?: Record<string, any>; // 上传额外数据
  cropConfig?: Partial<CropConfig>; // 裁剪配置
  limit?: number; // 文件大小限制(MB)
  maxCount?: number; // 最大文件数量
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

interface FileItem {
  id: string; // 唯一标识
  url: string; // 图片URL
  name?: string; // 文件名
  uploading?: boolean; // 上传状态
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => [],
  action: '/api/v1/commons/upload',
  crop: false,
  headers: () => ({}),
  data: () => ({}),
  cropConfig: () => ({}),
  limit: 5,
  maxCount: 5,
});

const emit = defineEmits<{
  remove: [url: string, fileList: string[]];
  'update:modelValue': [value: string[]];
  uploadError: [error: Error];
  uploadSuccess: [url: string, fileList: string[]];
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
const currentCropIndex = ref<number>(-1); // 当前裁剪的文件索引，-1表示新增文件

// 文件列表
const fileList = ref<FileItem[]>([]);

// 合并裁剪配置
const mergedCropConfig = computed(() => ({
  ...defaultCropConfig,
  ...props.cropConfig,
}));

// 从URL中提取文件名
const getFileNameFromUrl = (url: string): string => {
  try {
    return url.split('/').pop() || 'image';
  } catch {
    return 'image';
  }
};
// 生成唯一ID
const generateId = () => {
  return Date.now().toString(36) + Math.random().toString(36).slice(2);
};
// 从modelValue初始化文件列表
watch(
  () => props.modelValue,
  (urls) => {
    if (
      JSON.stringify(urls) !==
      JSON.stringify(fileList.value.map((item) => item.url))
    ) {
      fileList.value = urls.map((url) => ({
        id: generateId(),
        url,
        name: getFileNameFromUrl(url),
      }));
    }
  },
  { immediate: true, deep: true },
);

// 处理单个文件重新上传
const triggerReupload = (index: number) => {
  const input = document.createElement('input');
  input.type = 'file';
  input.accept = 'image/*';
  input.style.display = 'none';

  input.addEventListener('change', (e) => {
    const file = (e.target as HTMLInputElement).files?.[0];
    if (file) {
      currentCropIndex.value = index; // 标记为重新上传
      handleFileSelect(file, index);
    }
    input.remove();
  });

  document.body.append(input);
  input.click();
};

// 处理单个文件选择
const handleFileSelect = (file: File, index: number) => {
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
  currentCropIndex.value = index;

  // 如果需要裁剪，显示裁剪对话框
  if (props.crop) {
    cropImageUrl.value = URL.createObjectURL(file);
    showCropDialog.value = true;
  } else {
    // 直接上传
    uploadFile(file, index);
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

  if (fileList.value.length >= props.maxCount) {
    ElMessage.error(`最多只能上传 ${props.maxCount} 个文件`);
    return false;
  }

  // 如果需要裁剪，显示裁剪对话框
  if (props.crop) {
    currentFile.value = rawFile;
    currentCropIndex.value = -1; // 新增文件
    cropImageUrl.value = URL.createObjectURL(rawFile);
    showCropDialog.value = true;
    return false; // 阻止自动上传
  }

  return true;
};

// 统一上传方法
const uploadFile = async (file: File, index: number) => {
  try {
    // 如果是重新上传，标记为上传中状态
    if (index >= 0 && index < fileList.value.length && fileList.value[index]) {
      fileList.value[index].uploading = true;
    } else {
      uploading.value = true;
    }

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

    // 更新文件列表
    if (index >= 0 && index < fileList.value.length && fileList.value[index]) {
      // 重新上传，替换原有文件
      fileList.value[index].url = imageUrl;
      fileList.value[index].name = file.name;
      fileList.value[index].uploading = false;
    } else {
      // 新增文件
      fileList.value.push({
        id: generateId(),
        url: imageUrl,
        name: file.name,
        uploading: false,
      });
    }

    // 更新modelValue
    const urls = fileList.value.map((item) => item.url);
    emit('update:modelValue', urls);
    emit('uploadSuccess', imageUrl, urls);

    ElMessage.success(index >= 0 ? '重新上传成功!' : '上传成功!');
  } catch (error) {
    const err = error instanceof Error ? error : new Error('上传失败');

    // 重置上传状态
    if (index >= 0 && index < fileList.value.length && fileList.value[index]) {
      fileList.value[index].uploading = false;
    }

    emit('uploadError', err);
    ElMessage.error(err.message);
  } finally {
    uploading.value = false;
    currentFile.value = null;
    currentCropIndex.value = -1;
  }
};

// 处理上传
const handleUpload: UploadRequestHandler = async (options) => {
  const { file, onSuccess } = options;
  await uploadFile(file, -1);
  onSuccess({});
};

// 处理裁剪
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

      // 创建文件对象
      const originalName = currentFile.value?.name || 'cropped-image';
      const fileExtension = originalName.split('.').pop() || 'png';
      const fileName = `cropped-${Date.now()}.${fileExtension}`;

      const file = new File([blob], fileName, { type: blob.type });

      await uploadFile(file, currentCropIndex.value);
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
const handleRemove = (index: number) => {
  const removedUrl = fileList.value[index]?.url;
  fileList.value.splice(index, 1);

  const urls = fileList.value.map((item) => item.url);
  emit('update:modelValue', urls);
  emit('remove', removedUrl || '', urls);

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
  <div class="multi-image-upload-container">
    <!-- 文件列表展示 -->
    <div class="file-list">
      <div v-for="(file, index) in fileList" :key="file.id" class="file-item">
        <div class="preview-container">
          <ElImage
            :src="file.url"
            :preview-src-list="fileList.map((f) => f.url)"
            fit="cover"
            class="preview-image"
            :zoom-rate="1.2"
            :max-scale="7"
            :min-scale="0.2"
            hide-on-click-modal
          />
          <div class="preview-actions">
            <ElButton
              type="primary"
              text
              :loading="file.uploading"
              @click="triggerReupload(index)"
            >
              <ElIcon><Edit /></ElIcon>
              {{ file.uploading ? '上传中...' : '重新上传' }}
            </ElButton>
            <ElButton type="danger" text @click="handleRemove(index)">
              <ElIcon><Delete /></ElIcon>
              删除
            </ElButton>
          </div>
          <ElTag v-if="file.name" class="file-name" size="small">
            {{ file.name }}
          </ElTag>
        </div>
      </div>

      <!-- 上传按钮 -->
      <div v-if="fileList.length < maxCount" class="upload-area file-item">
        <ElUpload
          ref="uploadRef"
          class="avatar-uploader"
          action="#"
          :show-file-list="false"
          :before-upload="beforeUpload"
          :http-request="handleUpload"
          accept="image/*"
          :multiple="true"
        >
          <ElIcon class="avatar-uploader-icon"><Plus /></ElIcon>
          <div class="upload-text">点击上传</div>
          <div class="upload-hint">最多 {{ maxCount }} 个文件</div>
        </ElUpload>
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
.multi-image-upload-container {
  width: 100%;
}

.file-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: flex-start;
}

.file-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.preview-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background: #fff;
  transition: all 0.3s;
}

.preview-container:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
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
  gap: 8px;
  flex-wrap: wrap;
  justify-content: center;
}

.file-name {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.upload-area {
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar-uploader {
  width: 100px;
  height: 140px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 8px;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 12px;
  color: #606266;
  text-align: center;
  margin-bottom: 4px;
}

.upload-hint {
  font-size: 10px;
  color: #909399;
  text-align: center;
}

.cropper-container {
  height: 400px;
  background: #f5f7fa;
  display: flex;
  justify-content: center;
  align-items: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
