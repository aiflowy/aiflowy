<script setup lang="ts">
import { ref, watch } from 'vue';

import { ElInput, ElMessage, ElTable, ElTableColumn } from 'element-plus';

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => [],
  editable: false,
  isEditOutput: false,
});

const emit = defineEmits<Emits>();

export interface TreeTableNode {
  key: string;
  name: string;
  description: string;
  method?: 'Body' | 'Header' | 'Path' | 'Query';
  required?: boolean;
  defaultValue?: string;
  enabled?: boolean;
  type?: string;
  children?: TreeTableNode[];
}

interface Props {
  modelValue?: TreeTableNode[];
  editable?: boolean;
  isEditOutput?: boolean;
}

const data = ref<TreeTableNode[]>([]);
const expandedKeys = ref<string[]>(['1']);
const errors = ref<
  Record<string, Partial<Record<keyof TreeTableNode, string>>>
>({});

watch(
  () => props.modelValue,
  (newVal) => {
    if (newVal) {
      data.value = newVal;
    }
  },
  { immediate: true, deep: true },
);

// 计算缩进宽度
const getIndentWidth = (record: TreeTableNode): number => {
  const level = String(record.key).split('-').length - 1;
  const indentSize = 20;
  return level > 0 ? level * indentSize : 0;
};

// 数据变化处理
const handleDataChange = () => {
  emit('update:modelValue', data.value);
};

// 展开/折叠处理
const onExpand = (_row: TreeTableNode, expandedRows: TreeTableNode[]) => {
  expandedKeys.value = expandedRows.map((item) => item.key);
};

// 验证字段
const validateFields = (): boolean => {
  const newErrors: Record<
    string,
    Partial<Record<keyof TreeTableNode, string>>
  > = {};
  let isValid = true;

  const checkNode = (node: TreeTableNode): boolean => {
    const { name, description, method, type } = node;
    const nodeErrors: Partial<Record<keyof TreeTableNode, string>> = {};

    if (!name?.trim()) {
      nodeErrors.name = '参数名称不能为空';
      isValid = false;
    }

    if (!description?.trim()) {
      nodeErrors.description = '参数描述不能为空';
      isValid = false;
    }

    if (isRootNode(node) && !method && !props.isEditOutput) {
      nodeErrors.method = '传入方法不能为空';
      isValid = false;
    }

    if (!type) {
      nodeErrors.type = '参数类型不能为空';
      isValid = false;
    }

    if (Object.keys(nodeErrors).length > 0) {
      newErrors[node.key] = nodeErrors;
    }

    if (node.children) {
      node.children.forEach((child) => {
        if (!checkNode(child)) isValid = false;
      });
    }

    return isValid;
  };

  data.value.forEach((node) => {
    if (!checkNode(node)) isValid = false;
  });

  errors.value = newErrors;
  return isValid;
};

// 判断是否为根节点
const isRootNode = (record: TreeTableNode): boolean => {
  return !record.key.includes('-');
};

// 提交参数
const handleSubmitParams = () => {
  if (!validateFields()) {
    ElMessage.error('请完善表单信息');
    return;
  }
  return data.value;
};

defineExpose({
  handleSubmitParams,
});

interface Emits {
  (e: 'update:modelValue', value: TreeTableNode[]): void;
  (e: 'submit', value: TreeTableNode[]): void;
}
</script>

<template>
  <div class="tree-table-container">
    <ElTable
      :data="data"
      row-key="key"
      :border="true"
      size="default"
      :expand-row-keys="expandedKeys"
      @expand-change="onExpand"
      style="width: 100%; overflow-x: auto"
    >
      <ElTableColumn prop="name" label="参数名称" class-name="first-column">
        <template #default="{ row }">
          <div class="name-cell">
            <div
              v-if="!props.editable"
              :style="{ paddingLeft: `${getIndentWidth(row)}px` }"
            >
              {{ row.name || '' }}
            </div>
            <div v-else class="editable-name">
              <div class="name-input-wrapper">
                <div :style="{ width: `${getIndentWidth(row)}px` }"></div>
                <ElInput
                  v-model="row.name"
                  :disabled="row.name === 'arrayItem'"
                  @input="handleDataChange"
                />
              </div>
              <div
                v-if="errors[row.key]?.name"
                class="error-message"
                :style="{ marginLeft: `${getIndentWidth(row)}px` }"
              >
                {{ errors[row.key]?.name }}
              </div>
            </div>
          </div>
        </template>
      </ElTableColumn>

      <!-- 参数值-->
      <ElTableColumn prop="defaultValue" label="参数值" width="150px">
        <template #default="{ row }">
          <span v-if="row.type === 'Object'"></span>
          <span v-else-if="!props.editable">{{ row.defaultValue || '' }}</span>
          <ElInput
            v-else
            v-model="row.defaultValue"
            @input="handleDataChange"
            :disabled="!props.editable"
          />
        </template>
      </ElTableColumn>
    </ElTable>
  </div>
</template>

<style scoped>
.tree-table-container {
  width: 100%;
  overflow-x: auto;
  box-sizing: border-box;
}

.name-cell {
  position: relative;
  min-width: 100%;
}

.editable-name {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.name-input-wrapper {
  display: flex;
  align-items: center;
  width: 100%;
}

.name-input-wrapper .el-input {
  box-sizing: border-box;
  width: 100%;
}

.error-message {
  color: #ff4d4f;
  font-size: 12px;
  margin-top: 2px;
  line-height: 1.2;
}

.action-buttons .el-button {
  width: 36px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

:deep(.el-table td.el-table__cell.first-column div) {
  display: flex;
  align-items: center;
  gap: 2px;
}
</style>
