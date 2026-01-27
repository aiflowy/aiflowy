<script setup lang="ts">
import type { Component, PropType } from 'vue';

import { ref } from 'vue';

import { $t } from '@aiflowy/locales';

import {
  ElAvatar,
  ElButton,
  ElCheckbox,
  ElCollapse,
  ElCollapseItem,
  ElDialog,
  ElMessage,
  ElText,
} from 'element-plus';

import HeaderSearch from '#/components/headerSearch/HeaderSearch.vue';
import PageData from '#/components/page/PageData.vue';

export interface ButtonConfig {
  key?: number | string;
  text: string;
  type?: 'danger' | 'default' | 'info' | 'primary' | 'success' | 'warning';
  icon?: Component | string; // 支持字符串图标或组件图标
  disabled?: boolean;
  permission?: string; // 权限编码
  [key: string]: any; // 允许传递自定义属性
}

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
  singleSelect: { type: Boolean, default: false },
  footerButtons: {
    type: Array as PropType<ButtonConfig[]>,
    default: () => [],
  },
});

const emit = defineEmits(['getData', 'buttonClick']);
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
    // 单选模式：先清空已选，再添加当前项
    if (props.singleSelect) {
      selectedIds.value = [id];
    } else {
      // 多选模式：追加当前项（避免重复）
      if (!selectedIds.value.includes(id)) {
        selectedIds.value.push(id);
      }
    }
  } else {
    // 取消选中：仅多选模式生效，单选模式不允许取消（可选）
    if (!props.singleSelect) {
      selectedIds.value = selectedIds.value.filter((i) => i !== id);
    }
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
  const hasSelected = props.isSelectMcp
    ? Object.keys(selectedToolMap.value).some(
        (parentId) => (selectedToolMap.value[parentId] ?? []).length > 0,
      )
    : selectedIds.value.length > 0;

  // 未选中内容时提示并返回
  if (!hasSelected) {
    ElMessage.error($t('message.selectTip'));
    return;
  }

  const submitData = props.isSelectMcp
    ? getMcpSelectedInfo()
    : selectedIds.value;
  emit('getData', submitData);

  // 关闭弹窗并返回数据
  dialogVisible.value = false;
  return submitData;
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
                        <ElText truncated class="title">
                          {{ item[titleKey] }}
                        </ElText>
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
                            <ElText truncated class="title">
                              {{ tool.name }}
                            </ElText>
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
                            <ElText truncated class="title">
                              {{ tool.name }}
                            </ElText>
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
                      <ElText truncated class="title">
                        {{ item.title }}
                      </ElText>
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
  background-color: var(--bot-collapse-itme-back);
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
  padding: 20px 50px 20px 20px;
  overflow: hidden;
  cursor: pointer;
  background-color: var(--el-bg-color);
  border: 1px solid hsl(var(--border));
  border-radius: 8px;
}

.title {
  font-family: PingFangSC, 'PingFang SC';
  font-size: 16px;
  font-style: normal;
  font-weight: 500;
  line-height: 24px;
  text-align: left;
  text-transform: none;
}

.content-left-container {
  display: flex;
  align-items: center;
}

.content-sec-left-container {
  display: flex;
  overflow: hidden;
}

.desc {
  display: -webkit-box;
  width: 100%;

  /* height: 42px;
  min-height: 42px; */
  margin-top: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  -webkit-line-clamp: 2;
  font-family: PingFangSC, 'PingFang SC';
  font-size: 14px;
  font-style: normal;
  font-weight: 400;
  line-height: 22px;
  text-align: left;
  text-transform: none;
  opacity: 0.65;
  -webkit-box-orient: vertical;
}

.title-right-container {
  /* display: flex;
  flex-direction: column;
  align-items: first baseline;
  justify-content: center; */
  padding-right: 10px;
  margin-left: 10px;
  overflow: hidden;
}

.title-sec-right-container {
  /* display: flex;
  flex-direction: column;
  align-items: flex-start; */
  padding-right: 10px;
  margin-left: 10px;
  overflow: hidden;
}

.container-second {
  display: flex;
  flex-direction: column;
  gap: 12px;

  /* padding: 20px 20px; */
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
  .content-title-wrapper:last-child {
  margin-bottom: 0;
}

.select-modal-container :deep(.el-collapse-item__header) {
  height: auto;
  padding: 12px;
  line-height: normal;
  color: #333;
  background-color: hsl(var(--background));
}

.select-modal-container :deep(.el-collapse-item__header:hover) {
  background-color: hsl(var(--background));
  border-color: hsl(var(--border));
}

.select-modal-container
  :deep(.el-collapse-item.is-active .el-collapse-item__header) {
  color: #1976d2;
  background-color: hsl(var(--background));
  border: none;
  border-bottom-color: transparent;
}

.select-modal-container :deep(.el-collapse-item__content) {
  padding: 12px;
  background-color: hsl(var(--background));
  border: none;
}

.select-modal-container :deep(.el-collapse-item__wrap) {
  background-color: hsl(var(--background));
  border: none;
}

.select-modal-container :deep(.el-collapse-item) {
  padding-bottom: 8px;
  background-color: hsl(var(--background));
}

.select-modal-container
  :deep(.el-collapse-item__content)
  .content-title-wrapper {
  margin-top: 12px;
  margin-bottom: 8px;
  background-color: var(--bot-collapse-itme-back);
  border: 1px solid hsl(var(--border));
  border-radius: 6px;
}

.select-modal-container
  :deep(.el-collapse-item__content)
  .content-title-wrapper:hover {
  background-color: var(--bot-collapse-itme-back);
}

.select-modal-container :deep(.el-collapse) {
  overflow: hidden;
  background-color: hsl(var(--background));
  border: 1px solid hsl(var(--border));
  border-radius: 4px;
}

.select-modal-container :deep(.el-checkbox__inner) {
  --el-checkbox-input-border: 1px solid hsl(var(--border));
}
</style>
