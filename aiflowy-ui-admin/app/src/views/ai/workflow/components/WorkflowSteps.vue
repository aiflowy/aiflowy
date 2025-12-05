<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { computed, ref, watch } from 'vue';

import {
  CircleCloseFilled,
  SuccessFilled,
  WarningFilled,
} from '@element-plus/icons-vue';
import {
  ElButton,
  ElCollapse,
  ElCollapseItem,
  ElForm,
  ElFormItem,
  ElIcon,
} from 'element-plus';

import ShowJson from '#/components/json/ShowJson.vue';
import { $t } from '#/locales';
import ConfirmItem from '#/views/ai/workflow/components/ConfirmItem.vue';
import ConfirmItemMulti from '#/views/ai/workflow/components/ConfirmItemMulti.vue';

export interface WorkflowStepsProps {
  workflowId: any;
  nodeJson: any;
  initSignal?: boolean;
  pollingData?: any;
}
const props = defineProps<WorkflowStepsProps>();
const emit = defineEmits(['resume']);
const nodes = ref<any[]>([]);
const nodeStatusMap = ref<Record<string, any>>({});
const isChainError = ref(false);
watch(
  () => props.pollingData,
  (newVal) => {
    const nodes = newVal.nodes;
    for (const nodeId in nodes) {
      nodeStatusMap.value[nodeId] = nodes[nodeId];
    }
  },
  { deep: true },
);
watch(
  () => props.initSignal,
  () => {
    nodeStatusMap.value = {};
    isChainError.value = false;
    confirmBtnLoading.value = false;
  },
);
watch(
  () => props.nodeJson,
  (newVal) => {
    if (newVal) {
      nodes.value = [...newVal];
    }
  },
  { immediate: true },
);
const displayNodes = computed(() => {
  return nodes.value.map((node) => ({
    ...node,
    ...nodeStatusMap.value[node.key],
  }));
});
const activeName = ref('1');
const confirmParams = ref<any>({});
// 定义一个对象来存储所有的 form 实例，key 为 node.key
const formRefs = ref<Record<string, FormInstance>>({});
// 动态设置 Ref 的辅助函数
const setFormRef = (el: any, key: string) => {
  if (el) {
    formRefs.value[key] = el as FormInstance;
  }
};
const confirmBtnLoading = ref(false);
function getSelectMode(ops: any) {
  return ops.formType || 'radio';
}
function handleConfirm(node: any) {
  const nodeKey = node.key;
  // 根据 key 获取具体的 form 实例
  const form = formRefs.value[nodeKey];

  if (!form) {
    console.warn(`Form instance for ${nodeKey} not found`);
    return;
  }
  const confirmKey = node.suspendForParameters[0].name;
  form.validate((valid) => {
    if (valid) {
      const value = {
        chainId: node.chainId,
        confirmParams: {
          [confirmKey]: 'yes',
          ...confirmParams.value,
        },
      };
      confirmBtnLoading.value = true;
      emit('resume', value);
    }
  });
}
</script>

<template>
  <div>
    <ElCollapse v-model="activeName" accordion expand-icon-position="left">
      <ElCollapseItem
        v-for="node in displayNodes"
        :key="node.key"
        :title="`${node.label}-${node.status}`"
        :name="node.key"
      >
        <template #title>
          <div class="flex items-center justify-between">
            <div>
              {{ node.label }}
            </div>
            <div class="flex items-center">
              <ElIcon v-if="node.status === 20" color="green" size="20">
                <SuccessFilled />
              </ElIcon>
              <div v-if="node.status === 1" class="spinner"></div>
              <ElIcon v-if="node.status === 10" color="red" size="20">
                <CircleCloseFilled />
              </ElIcon>
              <ElIcon v-if="isChainError" color="orange" size="20">
                <WarningFilled />
              </ElIcon>
            </div>
          </div>
        </template>
        <div v-if="node.original.type === 'confirmNode'" class="p-2.5">
          <ElForm
            :ref="(el) => setFormRef(el, node.key)"
            label-position="top"
            :model="confirmParams"
          >
            <template
              v-for="(ops, idx) in node.suspendForParameters"
              :key="idx"
            >
              <div class="header-container" v-if="ops.formType !== 'confirm'">
                <div class="blue-bar">&nbsp;</div>
                <span>{{ ops.formLabel }}</span>
              </div>
              <div
                class="description-container"
                v-if="ops.formType !== 'confirm'"
              >
                {{ ops.formDescription }}
              </div>
              <ElFormItem
                v-if="ops.formType !== 'confirm'"
                :prop="ops.name"
                :rules="[{ required: true, message: $t('message.required') }]"
              >
                <ConfirmItem
                  v-if="getSelectMode(ops) === 'radio'"
                  v-model="confirmParams[ops.name]"
                  :selection-data-type="ops.contentType || 'text'"
                  :selection-data="ops.enums"
                />
                <ConfirmItemMulti
                  v-else
                  v-model="confirmParams[ops.name]"
                  :selection-data-type="ops.contentType || 'text'"
                  :selection-data="ops.enums"
                />
              </ElFormItem>
            </template>
            <ElFormItem>
              <div class="flex justify-end">
                <ElButton
                  :disabled="confirmBtnLoading"
                  type="primary"
                  @click="handleConfirm(node)"
                >
                  {{ $t('button.confirm') }}
                </ElButton>
              </div>
            </ElFormItem>
          </ElForm>
        </div>
        <div v-else>
          <ShowJson :value="node.result" />
        </div>
      </ElCollapseItem>
    </ElCollapse>
  </div>
</template>

<style scoped>
.spinner {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: var(--el-color-primary);
  border-right-color: var(--el-color-primary);
  animation: spin 1s linear infinite;
}
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
.header-container {
  font-weight: bold;
  display: flex;
  align-items: center;
  word-break: break-all;
}

.blue-bar {
  display: inline-block;
  width: 2px;
  border-radius: 1px;
  height: 16px;
  margin-right: 16px;
  background-color: var(--el-color-primary);
}

.description-container {
  margin-bottom: 16px;
  color: #969799;
  word-break: break-all;
}
</style>
