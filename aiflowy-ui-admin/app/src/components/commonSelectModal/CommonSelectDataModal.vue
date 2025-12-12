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
            <div class="container-second">
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
            </div>
          </template>
          <template v-else>
            <div class="container-second">
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
  background-color: var(--bot-select-data-item-back);
  border-radius: 8px;
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
  justify-content: space-between;
  height: 113px;
  background-color: var(--el-bg-color);
  cursor: pointer;
  border-radius: 8px;
  padding: 20px 50px 20px 20px;
}

.title {
  font-size: 16px;
  font-family:
    PingFangSC,
    PingFang SC;
  font-weight: 500;
  font-size: 16px;
  color: rgba(0, 0, 0, 0.85);
  line-height: 24px;
  text-align: left;
  font-style: normal;
  text-transform: none;
}
.content-left-container {
  display: flex;
  align-items: center;
}
.content-sec-left-container {
  display: flex;
}
.desc {
  width: 100%;
  font-family:
    PingFangSC,
    PingFang SC;
  font-weight: 400;
  font-size: 14px;
  color: rgba(0, 0, 0, 0.45);
  line-height: 22px;
  text-align: left;
  font-style: normal;
  text-transform: none;
  height: 42px;
  min-height: 42px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-top: 12px;
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
.select-modal-container :deep(.el-collapse) {
  border: none;
}

.select-modal-container :deep(.el-collapse-item) {
  margin-bottom: 8px;
}

.select-modal-container :deep(.el-collapse-item__header) {
  height: auto;
  line-height: normal;
  padding: 12px;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.select-modal-container :deep(.el-collapse-item__wrap) {
  border: none;
}

.select-modal-container :deep(.el-collapse-item__content) {
  padding: 12px;
}

.select-modal-container
  :deep(.el-collapse-item.is-active .el-collapse-item__header) {
  border-bottom-color: transparent;
}
.container-second {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 20px 20px;
}

.select-modal-container :deep(.el-collapse-item__header) {
  background-color: var(--el-bg-color);
  border-color: #dee2e6;
  color: #333;
}

.select-modal-container :deep(.el-collapse-item__header:hover) {
  background-color: #e9ecef;
}

.select-modal-container
  :deep(.el-collapse-item.is-active .el-collapse-item__header) {
  color: #1976d2;
}

.select-modal-container :deep(.el-collapse-item__content) {
  background-color: #ffffff;
  padding: 0;
}

.select-modal-container
  :deep(.el-collapse-item__header .el-collapse-item__arrow) {
  color: #666;
}
.select-modal-container
  :deep(.el-collapse-item.is-active .el-collapse-item__arrow) {
  color: #1976d2;
}

.select-modal-container
  :deep(.el-collapse-item__content)
  .content-title-wrapper {
  background-color: #f9fafb;
  border: 1px solid #f0f0f0;
  border-radius: 6px;
  margin-bottom: 8px;
}

.select-modal-container
  :deep(.el-collapse-item__content)
  .content-title-wrapper:hover {
  background-color: #f0f7ff;
  border-color: #e6f4ff;
}

.select-modal-container
  :deep(.el-collapse-item__content)
  .content-title-wrapper:last-child {
  margin-bottom: 0;
}

.select-modal-container :deep(.el-collapse) {
  border: none;
  background-color: #ffffff;
}

.select-modal-container :deep(.el-collapse-item__header) {
  height: auto;
  line-height: normal;
  padding: 12px;
  border-radius: 4px;
  background-color: #ffffff !important;
  color: #333;
}

.select-modal-container :deep(.el-collapse-item__header:hover) {
  background-color: #ffffff !important;
  border-color: #e4e7ed;
}

.select-modal-container
  :deep(.el-collapse-item.is-active .el-collapse-item__header) {
  border-bottom-color: transparent;
  background-color: #ffffff !important;
  color: #1976d2;
  border: none;
}

.select-modal-container :deep(.el-collapse-item__content) {
  padding: 12px;
  background-color: #ffffff !important;
  border: none;
}

.select-modal-container :deep(.el-collapse-item__wrap) {
  border: none;
  background-color: #ffffff;
}

.select-modal-container :deep(.el-collapse-item) {
  margin-bottom: 8px;
  background-color: #ffffff;
}

.select-modal-container
  :deep(.el-collapse-item__content)
  .content-title-wrapper {
  background-color: #f9fafc;
  border: 1px solid #f0f0f0;
  border-radius: 6px;
  margin-bottom: 8px;
  margin-top: 12px;
}

.select-modal-container
  :deep(.el-collapse-item__content)
  .content-title-wrapper:hover {
  background-color: #f8f9fa;
  border-color: #e6f4ff;
}

.select-modal-container :deep(.el-collapse) {
  border: none;
  background-color: #ffffff;
}
</style>
