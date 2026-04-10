<script setup lang="ts">
import DocumentCollectionModal from './DocumentCollectionModal.vue';

const props = defineProps({
  detailData: {
    type: Object,
    default: () => ({
      id: '',
      alias: '',
      deptId: '',
      icon: '',
      title: '',
      description: '',
      slug: '',
      vectorStoreEnable: false,
      vectorStoreType: '',
      vectorStoreCollection: '',
      vectorStoreConfig: '',
      vectorEmbedModelId: '',
      options: {
        canUpdateEmbeddingModel: true,
      },
      rerankModelId: '',
      searchEngineEnable: false,
      englishName: '',
    }),
    required: true,
  },
});

const emit = defineEmits(['reload']);
</script>

<template>
  <DocumentCollectionModal
    :is-modal="false"
    :detail-data="detailData"
    @reload="emit('reload')"
  />
</template>
