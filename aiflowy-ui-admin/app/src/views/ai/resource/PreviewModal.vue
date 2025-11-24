<script setup lang="ts">
import { ref } from 'vue';

import { ElDialog, ElImage } from 'element-plus';

defineExpose({
  openDialog,
});
const dialogVisible = ref(false);
const data = ref<any>();
function openDialog(row: any) {
  data.value = row;
  dialogVisible.value = true;
}
function closeDialog() {
  dialogVisible.value = false;
}
</script>

<template>
  <ElDialog
    v-model="dialogVisible"
    draggable
    :title="$t('message.preview')"
    :before-close="closeDialog"
    :close-on-click-modal="false"
  >
    <div class="flex justify-center">
      <ElImage
        v-if="data.resourceType === 0"
        style="width: 200px"
        :preview-src-list="[data.resourceUrl]"
        :src="data.resourceUrl"
      />
      <video v-if="data.resourceType === 1" controls width="640" height="360">
        <source :src="data.resourceUrl" type="video/mp4" />
        您的浏览器不支持 video 元素。
      </video>
      <audio v-if="data.resourceType === 2" controls :src="data.resourceUrl">
        您的浏览器不支持 audio 元素。
      </audio>
    </div>
  </ElDialog>
</template>

<style scoped></style>
