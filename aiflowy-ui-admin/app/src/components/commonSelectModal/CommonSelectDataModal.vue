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

interface SelectedMcpTool {
  name: string;
  description: string;
}
const props = defineProps({
  title: { type: String, default: '' },
  width: { type: String, default: '80%' },
  extraQueryParams: { type: Object, default: () => ({}) },
  searchParams: {
    type: Array as PropType<string[]>,
    default: () => [],
  },
  titleKey: { type: String, default: 'name' },
  pageUrl: { type: String, default: '' },
  hasParent: { type: Boolean, default: false },
  isSelectMcp: { type: Boolean, default: false },
});

const emit = defineEmits(['getData']);
const dialogVisible = ref(false);
const pageDataRef = ref();
const loading = ref(false);
const selectedIds = ref<(number | string)[]>([]);
// 存储上一级id与选中tool.name的关联关系
const selectedToolMap = ref<Record<number | string, SelectedMcpTool[]>>({});
defineExpose({
  openDialog(defaultSelectedIds: (number | string)[]) {
    selectedIds.value = defaultSelectedIds ? [...defaultSelectedIds] : [];
    dialogVisible.value = true;
  },
  /**
   * MCP专属弹窗打开方法（适配MCP回显，传递格式化后的MCP数据）
   * @param selectMcpMap - MCP已选数据映射（键：MCP父级ID，值：工具名称+描述数组）
   */
  openMcpDialog(selectMcpMap: Record<number | string, SelectedMcpTool[]>) {
    selectedIds.value = [];
    selectedToolMap.value = structuredClone(selectMcpMap);
    dialogVisible.value = true;
  },
});
const isSelected = (id: number | string) => {
  return selectedIds.value.includes(id);
};

const isSelectedMcp = (parentId: number | string, toolName: string) => {
  // 查找当前parentId下是否存在该tool.name的工具
  return !!selectedToolMap.value[parentId]?.some(
    (tool) => tool.name === toolName,
  );
};

const toggleSelectionMcp = (
  mcpId: number | string,
  toolName: string,
  toolDescription: string,
  checked: any,
) => {
  if (checked) {
    if (!selectedToolMap.value[mcpId]) {
      selectedToolMap.value[mcpId] = []; // 初始化空数组
    }
    const isExisted = selectedToolMap.value[mcpId]?.some(
      (tool) => tool.name === toolName,
    );
    if (!isExisted) {
      selectedToolMap.value[mcpId]?.push({
        name: toolName,
        description: toolDescription,
      });
    }
  } else {
    if (selectedToolMap.value[mcpId]) {
      selectedToolMap.value[mcpId] = selectedToolMap.value[mcpId].filter(
        (tool) => tool.name !== toolName,
      );
      if (selectedToolMap.value[mcpId].length === 0) {
        delete selectedToolMap.value[mcpId];
      }
    }
  }
};

const toggleSelection = (id: number | string, checked: any) => {
  if (checked) {
    selectedIds.value.push(id);
  } else {
    selectedIds.value = selectedIds.value.filter((i) => i !== id);
  }
};

/**
 * 封装：获取MCP选中的结构化信息（包含name和description）
 * @returns {Record<number | string, string[][]>[]} 符合要求的数据：[{ 上一级id: [[name1, description1], [name2, description2]] }]
 */
const getMcpSelectedInfo = (): Record<number | string, string[][]>[] => {
  const mcpSelectedResult: Record<number | string, string[][]>[] = [];

  Object.entries(selectedToolMap.value).forEach(([parentId, selectedTools]) => {
    // 转换每个工具为 [name, description] 一维数组
    const formattedToolList: string[][] = selectedTools.map((tool) => [
      tool.name,
      tool.description,
    ]);

    mcpSelectedResult.push({
      [parentId]: formattedToolList,
    });
  });

  return mcpSelectedResult;
};

const handleSubmitRun = () => {
  if (props?.isSelectMcp) {
    emit('getData', getMcpSelectedInfo());
  } else {
    emit('getData', selectedIds.value);
  }
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
  >
    <template #header>
      <div>
        <p class="el-dialog__title mb-4">{{ props.title }}</p>
        <HeaderSearch @search="handleSearch" />
      </div>
    </template>
    <div class="select-modal-container p-5">
      <PageData
        ref="pageDataRef"
        :page-url="pageUrl"
        :page-size="10"
        :extra-query-params="extraQueryParams"
      >
        <template #default="{ pageList }">
          <template v-if="hasParent">
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
                        <div class="title">{{ item[titleKey] }}</div>
                        <div class="desc">{{ item.description }}</div>
                      </div>
                    </div>
                  </template>
                  <!--选择插件-->
                  <div v-if="!isSelectMcp">
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
                  </div>
                  <!--选择MCP-->
                  <div v-if="isSelectMcp">
                    <div v-for="tool in item.tools" :key="tool.name">
                      <div class="content-title-wrapper">
                        <div class="content-left-container">
                          <div class="title-right-container">
                            <div class="title">{{ tool.name }}</div>
                            <div class="desc">{{ tool.description }}</div>
                          </div>
                        </div>
                        <div>
                          <ElCheckbox
                            :model-value="isSelectedMcp(item.id, tool.name)"
                            @change="
                              (val) =>
                                toggleSelectionMcp(
                                  item.id,
                                  tool.name,
                                  tool.description,
                                  val,
                                )
                            "
                          />
                        </div>
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
  /* height: 100%;
  overflow: auto; */
  background-color: var(--bot-select-data-item-back);
  border-radius: 8px;
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
  /* padding: 20px 20px; */
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
