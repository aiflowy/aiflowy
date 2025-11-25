<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';

import { ElMessage, ElOption, ElSelect } from 'element-plus';

import { api } from '#/api/request';
import { $t } from '#/locales';
// 字典项接口
interface DictItem {
  value: number | string;
  label: string;
  disabled?: boolean;
  [key: string]: any;
}

interface Props {
  modelValue: Array<number | string> | null | number | string | undefined;
  dictCode: string; // 字典编码
  placeholder?: string;
  clearable?: boolean;
  filterable?: boolean;
  disabled?: boolean;
  multiple?: boolean;
  collapseTags?: boolean;
  collapseTagsTooltip?: boolean;
  showCode?: boolean; // 是否显示字典编码前缀
  immediate?: boolean; // 是否立即加载
  extraOptions?: DictItem[];
}

interface Emits {
  (
    e: 'update:modelValue',
    value: Array<number | string> | null | number | string,
  ): void;
  (
    e: 'change',
    value: Array<number | string> | null | number | string,
    dictItem?: DictItem | DictItem[],
  ): void;
  (e: 'blur'): void;
  (e: 'dictLoaded', options: DictItem[]): void;
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: undefined,
  clearable: true,
  filterable: true,
  disabled: false,
  multiple: false,
  collapseTags: false,
  collapseTagsTooltip: false,
  showCode: false,
  immediate: true,
  extraOptions: () => [],
});

const emit = defineEmits<Emits>();
// 使用计算属性处理placeholder
const placeholderText = computed(() => {
  // 如果父组件传入了placeholder，优先使用
  if (props.placeholder !== undefined) {
    return props.placeholder;
  }
  // 否则使用默认的国际化文本
  return $t('dictSelect.placeholder');
});
// 响应式数据
const dictOptions = ref<DictItem[]>([]);
const loading = ref(false);
const loadedCodes = ref<Set<string>>(new Set()); // 已加载的字典编码缓存

// 处理值变化
const handleChange = (
  value: Array<number | string> | null | number | string,
) => {
  emit('update:modelValue', value);

  // 找到对应的字典项
  const selectedItems: DictItem | DictItem[] | undefined =
    props.multiple && Array.isArray(value)
      ? (value
          .map((v) => dictOptions.value.find((item) => item.value === v))
          .filter(Boolean) as DictItem[])
      : dictOptions.value.find((item) => item.value === value);

  emit('change', value, selectedItems);
};

// 处理失去焦点
const handleBlur = () => {
  emit('blur');
};

// 获取字典数据
const fetchDictData = async (code: string) => {
  // 如果已经加载过，直接返回缓存
  if (loadedCodes.value.has(code)) {
    return;
  }

  loading.value = true;
  try {
    // 这里调用你的后端API
    const data = await getDictListByCode(code);
    // extraOptions 放最前面
    dictOptions.value = [...props.extraOptions, ...data];
    loadedCodes.value.add(code);
    emit('dictLoaded', data);
  } catch (error) {
    console.error(`${$t('dictSelect.getError')}: ${code}`, error);
    ElMessage.error(`${$t('dictSelect.getError')}: ${code}`);
    dictOptions.value = [];
  } finally {
    loading.value = false;
  }
};

// 模拟后端API调用 - 实际项目中替换为你的真实API
const getDictListByCode = async (code: string): Promise<DictItem[]> => {
  const requestPromise = api.get(`/api/v1/dict/items/${code}`);
  const dictData = await requestPromise;
  return dictData.data;
};

// 重新加载字典
const reloadDict = () => {
  if (props.dictCode) {
    fetchDictData(props.dictCode);
  }
};

// 监听字典编码变化
watch(
  () => props.dictCode,
  (newCode) => {
    if (newCode) {
      fetchDictData(newCode);
    }
  },
);

// 组件挂载时加载字典
onMounted(() => {
  if (props.immediate && props.dictCode) {
    fetchDictData(props.dictCode);
  }
});

// 暴露方法给父组件
defineExpose({
  reloadDict,
  getOptions: () => dictOptions.value,
});
</script>

<template>
  <ElSelect
    :model-value="modelValue"
    @update:model-value="handleChange"
    @blur="handleBlur"
    :placeholder="placeholderText"
    :clearable="clearable"
    :filterable="filterable"
    :disabled="disabled || loading"
    :loading="loading"
    :multiple="multiple"
    :collapse-tags="collapseTags"
    :collapse-tags-tooltip="collapseTagsTooltip"
  >
    <ElOption
      v-for="item in dictOptions"
      :key="item.value"
      :label="item.label"
      :value="item.value"
      :disabled="item.disabled"
    />
    <template #prefix v-if="showCode && dictCode">
      <span class="dict-select__prefix">{{ dictCode }}</span>
    </template>
  </ElSelect>
</template>

<style scoped>
.dict-select__prefix {
  color: #909399;
  font-size: 12px;
  margin-right: 4px;
}
</style>
