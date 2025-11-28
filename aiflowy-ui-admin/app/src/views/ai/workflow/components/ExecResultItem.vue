<script setup lang="ts">
import { ElCard, ElCol, ElImage } from 'element-plus';

import fileIcon from '#/assets/ai/workflow/fileIcon.png';

const props = defineProps({
  defName: {
    type: String,
    required: true,
  },
  contentType: {
    type: String,
    required: true,
  },
  resultCount: {
    type: Number,
    required: true,
  },
  result: {
    type: Array,
    required: true,
  },
});

function makeItem(item: any, index: number) {
  const name = `${props.defName}-${index + 1}`;
  // 保存需要用
  return {
    resourceName: name,
    resourceUrl: item,
    title: name,
    filePath: item,
    content: typeof item === 'string' ? item : JSON.stringify(item),
  };
}
</script>

<template>
  <ElCol
    :span="resultCount === 1 ? 24 : 12"
    v-for="(item, idx) of result"
    :key="idx"
  >
    <ElCard shadow="hover" class="mb-3">
      <template #header>
        <div>
          <div class="font-medium">
            {{ makeItem(item, idx).resourceName }}
          </div>
        </div>
      </template>
      <div class="h-40 w-full overflow-auto break-words">
        <ElImage
          v-if="contentType === 'image'"
          :src="`${item}`"
          :preview-src-list="[`${item}`]"
          class="h-36 w-full"
          fit="contain"
        />
        <video
          v-if="contentType === 'video'"
          controls
          :src="`${item}`"
          class="h-36 w-full"
        ></video>
        <audio
          v-if="contentType === 'audio'"
          controls
          :src="`${item}`"
          class="h-3/5 w-full"
        ></audio>
        <div v-if="contentType === 'text'">
          {{ typeof item === 'string' ? item : JSON.stringify(item) }}
        </div>
        <div v-if="contentType === 'other' || contentType === 'file'">
          <div class="mt-5 flex justify-center">
            <img :src="fileIcon" alt="" class="h-20 w-20" />
          </div>
          <div class="mt-3 text-center">
            <a :href="`${item}`" target="_blank">下载</a>
          </div>
        </div>
      </div>
    </ElCard>
  </ElCol>
</template>

<style scoped></style>
