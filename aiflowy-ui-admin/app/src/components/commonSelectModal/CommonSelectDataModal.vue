<script setup lang="ts">
import type { PropType } from 'vue';

import { ref } from 'vue';

import { $t } from '@aiflowy/locales';

import {
  ElAvatar,
  ElButton,
  ElCheckbox,
  ElCollapse,
  ElCollapseItem,
  ElDialog,
} from 'element-plus';

import HeaderSearch from '#/components/headerSearch/HeaderSearch.vue';
import PageData from '#/components/page/PageData.vue';

const props = defineProps({
  title: { type: String, default: '' },
  width: { type: String, default: '80%' },
  extraQueryParams: { type: Object, default: () => ({}) },
  searchParams: {
    type: Array as PropType<string[]>,
    default: () => [],
  },
  pageUrl: { type: String, default: '' },
  isSelectPlugin: { type: Boolean, default: false },
});

const emit = defineEmits(['getData']);
const dialogVisible = ref(false);
const pageDataRef = ref();
const loading = ref(false);
const selectedIds = ref<(number | string)[]>([]);

defineExpose({
  openDialog(defaultSelectedIds: (number | string)[]) {
    selectedIds.value = defaultSelectedIds ? [...defaultSelectedIds] : [];
    dialogVisible.value = true;
  },
});
const isSelected = (id: number | string) => {
  return selectedIds.value.includes(id);
};

const toggleSelection = (id: number | string, checked: any) => {
  if (checked) {
    selectedIds.value.push(id);
  } else {
    selectedIds.value = selectedIds.value.filter((i) => i !== id);
  }
};
const handleSubmitRun = () => {
  emit('getData', selectedIds.value);
  dialogVisible.value = false;
  return selectedIds.value;
};
const handleSearch = (query: string) => {
  const tempParams = {} as Record<string, string>;
  props.searchParams.forEach((paramName) => {
    tempParams[paramName] = query;
  });

  pageDataRef.value?.setQuery({
    isQueryOr: true,
    ...tempParams,
  });
};
</script>

<template>
  <ElDialog
    v-model="dialogVisible"
    draggable
    :close-on-click-modal="false"
    :width="props.width"
    align-center
    :title="props.title"
  >
    <div class="select-modal-header-container">
      <HeaderSearch @search="handleSearch" />
    </div>
    <div class="select-modal-container">
      <PageData
        ref="pageDataRef"
        :page-url="pageUrl"
        :page-size="10"
        :extra-query-params="extraQueryParams"
      >
        <template #default="{ pageList }">
          <template v-if="isSelectPlugin">
            <ElCollapse
              accordion
              v-for="(item, index) in pageList"
              :key="index"
            >
              <ElCollapseItem>
                <template #title="{ isActive }">
                  <div
                    class="title-wrapper"
                    :class="[{ 'is-active': isActive }]"
                  >
                    <div>
                      <ElAvatar :src="item.icon" v-if="item.icon" />
                      <ElAvatar v-else src="/favicon.png" shape="circle" />
                    </div>
                    <div class="title-right-container">
                      <div class="title">{{ item.name }}</div>
                      <div class="desc">{{ item.description }}</div>
                    </div>
                  </div>
                </template>
                <div v-for="tool in item.tools" :key="tool.id">
                  <div class="content-title-wrapper">
                    <div class="content-left-container">
                      <div class="title-right-container">
                        <div class="title">{{ tool.name }}</div>
                        <div class="desc">{{ tool.description }}</div>
                      </div>
                    </div>
                    <div>
                      <ElCheckbox
                        :model-value="isSelected(tool.id)"
                        @change="(val) => toggleSelection(tool.id, val)"
                      />
                    </div>
                  </div>
                </div>
              </ElCollapseItem>
            </ElCollapse>
          </template>
          <template v-else>
            <div v-for="(item, index) in pageList" :key="index">
              <div class="content-title-wrapper">
                <div class="content-sec-left-container">
                  <div>
                    <ElAvatar :src="item.icon" v-if="item.icon" />
                    <ElAvatar v-else src="/favicon.png" shape="circle" />
                  </div>
                  <div class="title-sec-right-container">
                    <div class="title">{{ item.title }}</div>
                    <div class="desc">{{ item.description }}</div>
                  </div>
                </div>
                <div>
                  <ElCheckbox
                    :model-value="isSelected(item.id)"
                    @change="(val) => toggleSelection(item.id, val)"
                  />
                </div>
              </div>
            </div>
          </template>
        </template>
      </PageData>
    </div>
    <template #footer>
      <ElButton @click="dialogVisible = false">
        {{ $t('button.cancel') }}
      </ElButton>
      <ElButton type="primary" @click="handleSubmitRun" :loading="loading">
        {{ $t('button.confirm') }}
      </ElButton>
    </template>
  </ElDialog>
</template>

<style scoped>
.select-modal-container {
  height: calc(100vh - 161px);
  overflow: auto;
}

.select-modal-header-container {
  margin-bottom: 20px;
}

.title-wrapper {
  display: flex;
  align-items: center;
}

.content-title-wrapper {
  display: flex;
  align-items: center;
  margin-left: 20px;
  justify-content: space-between;
  padding-right: 20px;
}

.title {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}
.content-left-container {
  display: flex;
  align-items: center;
}
.content-sec-left-container {
  display: flex;
}
.desc {
  margin: 0;
  width: 100%;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  height: 42px;
  min-height: 42px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.title-right-container {
  margin-left: 10px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.title-sec-right-container {
  margin-left: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
</style>
