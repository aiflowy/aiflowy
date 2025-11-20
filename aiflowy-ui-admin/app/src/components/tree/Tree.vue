<script setup lang="ts">
import type { TreeV2Instance } from 'element-plus';

import { nextTick, onMounted, ref, watch } from 'vue';

import { ElMessage, ElTreeV2 } from 'element-plus';

import { api } from '#/api/request';
import { $t } from '#/locales';
// 定义组件属性
const props = defineProps({
  // 获取树数据的URL
  dataUrl: {
    type: String,
    required: true,
  },
  // 已选择的节点数组（支持双向绑定）
  modelValue: {
    type: Array<any>,
    default: () => [],
  },
  // 节点键名
  nodeKey: {
    type: String,
    default: 'id',
  },
  // 树形配置
  defaultProps: {
    type: Object,
    default: () => ({
      children: 'children',
      label: 'label',
    }),
  },
  // 是否默认展开所有节点
  defaultExpandAll: {
    type: Boolean,
    default: false,
  },
  // 是否严格遵循父子不互相关联
  checkStrictly: {
    type: Boolean,
    default: false,
  },
  // 树的高度
  height: {
    type: Number,
    default: 200,
  },
  // 是否显示子节点数量
  showCount: {
    type: Boolean,
    default: true,
  },
  // 是否显示已选择区域
  showSelected: {
    type: Boolean,
    default: true,
  },
  // 加载提示文本
  loadingText: {
    type: String,
    default: $t('message.loading'),
  },
});

// 定义事件
const emit = defineEmits(['update:modelValue', 'change', 'check']);

// 响应式数据
const treeData = ref([]);
const loading = ref(false);
const filterText = ref('');
const treeRef = ref<TreeV2Instance>();
const nodeMap = ref(new Map()); // 用于存储节点键到节点数据的映射
const isTreeReady = ref(false); // 标记树组件是否准备就绪
// 监听过滤文本变化
watch(filterText, (val) => {
  treeRef.value?.filter(val);
});

// 监听modelValue变化，更新树的选择状态
watch(
  () => props.modelValue,
  (newVal: any) => {
    if (isTreeReady.value && treeRef.value) {
      // 使用nextTick确保DOM更新完成
      nextTick(() => {
        treeRef.value!.setCheckedKeys(newVal);
      });
    }
  },
  { immediate: true, deep: true },
);
// 监听树数据变化，当数据加载完成后设置选中状态
watch(
  () => treeData.value,
  () => {
    if (treeData.value.length > 0 && props.modelValue.length > 0) {
      // 数据加载完成后，等待树组件渲染完成再设置选中状态
      nextTick(() => {
        isTreeReady.value = true;
        treeRef.value!.setCheckedKeys(props.modelValue);
      });
    }
  },
  { deep: true },
);

// 过滤节点方法
const filterNode = (value: any, data: any) => {
  if (!value) return true;
  return data[props.defaultProps.label]?.toString().includes(value);
};

// 处理节点选择变化
const handleCheck = (_: any, checkedInfo: any) => {
  const checkedKeys = checkedInfo.checkedKeys;
  emit('update:modelValue', checkedKeys);
  emit('change', checkedKeys);
  emit('check', {
    checkedNodes: checkedInfo.checkedNodes,
    checkedKeys,
    halfCheckedNodes: checkedInfo.halfCheckedNodes,
    halfCheckedKeys: checkedInfo.halfCheckedKeys,
  });
};

// 构建节点映射
const buildNodeMap = (nodes: any) => {
  nodes.forEach((node: any) => {
    nodeMap.value.set(node[props.nodeKey], node);
    if (node[props.defaultProps.children]) {
      buildNodeMap(node[props.defaultProps.children]);
    }
  });
};

// 获取树数据
const fetchTreeData = async () => {
  if (!props.dataUrl) return;

  loading.value = true;
  try {
    const res = await api.get(props.dataUrl);

    treeData.value = res.data;
    // 构建节点映射
    nodeMap.value.clear();
    buildNodeMap(res.data);
  } catch (error) {
    console.error('get data error:', error);
    ElMessage.error($t('message.getDataError'));
  } finally {
    loading.value = false;
  }
};

// 获取当前选中的节点
const getCheckedNodes = () => {
  return treeRef.value?.getCheckedNodes() || [];
};

// 获取当前选中的叶子节点
const getCheckedLeafNodes = () => {
  return treeRef.value?.getCheckedNodes(true) || [];
};

// 获取半选中的节点
const getHalfCheckedNodes = () => {
  return treeRef.value?.getHalfCheckedNodes() || [];
};

// 清空选择
const clearChecked = () => {
  treeRef.value?.setCheckedKeys([]);
};

// 设置选中节点
const setCheckedKeys = (keys: any) => {
  treeRef.value?.setCheckedKeys(keys);
};

// 根据键值获取节点数据
const getNodeByKey = (key: any) => {
  return nodeMap.value.get(key);
};

// 暴露方法给父组件
defineExpose({
  getCheckedNodes,
  getCheckedLeafNodes,
  getHalfCheckedNodes,
  clearChecked,
  setCheckedKeys,
  getNodeByKey,
  refresh: fetchTreeData,
});

// 组件挂载时获取数据
onMounted(() => {
  fetchTreeData();
});
</script>

<template>
  <div class="tree-select">
    <div class="tree-header"></div>
    <div class="tree-wrapper">
      <ElTreeV2
        ref="treeRef"
        :data="treeData"
        :props="defaultProps"
        :node-key="nodeKey"
        :default-expand-all="defaultExpandAll"
        :filter-node-method="filterNode"
        :highlight-current="true"
        show-checkbox
        :check-strictly="checkStrictly"
        :height="height"
        @check="handleCheck"
        v-loading="loading"
        :element-loading-text="loadingText"
      >
        <template #default="{ node, data }">
          <span class="tree-node">
            <span class="node-label">{{ $t(node.label) }}</span>
            <span
              v-if="showCount && data[defaultProps.children]"
              class="node-count"
            >
              ({{ data[defaultProps.children].length }})
            </span>
          </span>
        </template>
      </ElTreeV2>
    </div>
  </div>
</template>

<style scoped>
.tree-select {
  background-color: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  width: 100%;
}

.tree-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background-color: #f8f9fa;
  border-bottom: 1px solid #e4e7ed;
}
.tree-wrapper {
  padding: 8px;
}

.tree-node {
  display: flex;
  gap: 8px;
  align-items: center;
}

.node-count {
  font-size: 12px;
  color: #909399;
}
</style>
