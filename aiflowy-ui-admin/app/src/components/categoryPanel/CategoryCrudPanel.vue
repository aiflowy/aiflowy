<script setup lang="ts">
import { computed, onMounted, ref, toRefs } from 'vue';

import { $t } from '@aiflowy/locales';

import { MoreFilled, Plus } from '@element-plus/icons-vue';
import {
  ElButton,
  ElDialog,
  ElDropdown,
  ElDropdownItem,
  ElDropdownMenu,
  ElEmpty,
  ElForm,
  ElFormItem,
  ElIcon,
  ElInput,
  ElMessage,
  ElMessageBox,
} from 'element-plus';

const props = defineProps({
  title: {
    type: String,
    default: '数据管理',
  },
  categoryData: {
    type: Array<Record<string, any>>,
    default: () => [],
  },
  disabled: {
    type: Boolean,
  },
  loading: {
    type: Boolean,
  },
  titleKey: {
    type: String,
    default: 'title',
  },
  panelWidth: {
    type: Number,
    default: 200,
  },
  valueKey: {
    type: String,
    default: undefined,
  },
  defaultSelectedCategory: {
    type: String,
    default: null,
  },
  defaultFormData: {
    type: Object,
    default: () => ({}),
  },
});

// 暴露给父组件的事件
const emit = defineEmits([
  'add', // 新增提交时触发
  'edit', // 编辑提交时触发
  'delete', // 删除时触发
  'click', // 点击时触发
]);

const finalValueKey = computed(() => {
  // 父组件传递了 valueKey 就用 valueKey，否则用 titleKey
  return props.valueKey ?? props.titleKey;
});

const { defaultFormData } = toRefs(props);

// 状态管理
const dialogVisible = ref(false); // 弹窗显隐
const isEdit = ref(false); // 是否为编辑模式
const formData = ref({
  ...defaultFormData,
}); // 表单数据

/**
 * 从 Proxy 对象提取原始数据
 */
const extractFromProxy = (proxyObj: any) => {
  try {
    if (proxyObj && typeof proxyObj === 'object') {
      const raw = (proxyObj as any).__v_raw || proxyObj;

      if (raw && typeof raw === 'object') {
        const result: any = Array.isArray(raw) ? [] : {};
        for (const key in raw) {
          if (
            key !== '__v_skip' &&
            Object.prototype.hasOwnProperty.call(raw, key)
          ) {
            result[key] = extractFromProxy(raw[key]);
          }
        }
        return result;
      }
      return raw;
    }
    return proxyObj;
  } catch (error) {
    console.warn('提取 Proxy 对象失败，使用浅拷贝:', error);
    return { ...proxyObj };
  }
};

/**
 * 新增按钮点击事件
 */
const handleAdd = () => {
  isEdit.value = false;
  formData.value = {
    ...defaultFormData,
  };
  dialogVisible.value = true;
};

/**
 * 编辑按钮点击事件
 * @param {object} row - 表格当前行数据
 */
const handleEdit = (row: any) => {
  isEdit.value = true;
  // 使用提取原始数据的方法
  formData.value = extractFromProxy(row);
  dialogVisible.value = true;
};

/**
 * 删除按钮点击事件
 * @param {object} row - 表格当前行数据
 */
const handleDelete = (row: any) => {
  // 先提取原始数据
  const rawData = extractFromProxy(row);

  ElMessageBox.confirm(`此操作将永久删除该${props.title}, 是否继续?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      // 触发父组件删除事件，传递原始数据
      emit('delete', rawData);
    })
    .catch(() => {
      ElMessage({ type: 'info', message: '已取消删除' });
    });
};

/**
 * 表单提交事件
 */
const handleSubmit = () => {
  // 触发对应事件，传递表单数据
  if (isEdit.value) {
    emit('edit', formData.value);
  } else {
    emit('add', formData.value);
  }
  // 提交后关闭弹窗
  dialogVisible.value = false;
};

const selectedCategory = ref();

onMounted(() => {
  // 初始化时，检查是否有默认选中的分类
  if (props.defaultSelectedCategory) {
    selectedCategory.value = props.defaultSelectedCategory;
  }
});

const handleCategoryClick = (category: any) => {
  // 选中值：用 finalValueKey（父组件指定的字段）
  selectedCategory.value = category[finalValueKey.value];
  emit('click', {
    item: extractFromProxy(category),
    value: category[finalValueKey.value], // 父组件指定字段的值
    label: category[props.titleKey], // 分类名称
  });
};

const handleEditClick = (event: any, item: any) => {
  event.stopPropagation();
  handleEdit(item);
};

const handleDeleteClick = (event: any, item: any) => {
  event.stopPropagation();
  handleDelete(item);
};
</script>

<template>
  <div
    class="flex h-full flex-col rounded-lg border border-[var(--el-border-color)] bg-[var(--el-bg-color)] p-2"
    :style="{ width: `${panelWidth}px` }"
  >
    <div class="flex flex-1 flex-col gap-5">
      <h3 class="text-base font-medium">{{ title }}</h3>

      <div class="flex-1 overflow-scroll">
        <div v-for="(item, index) in categoryData" :key="index">
          <div
            :class="{ selected: selectedCategory === item[finalValueKey] }"
            class="crud-category-item"
            @click="handleCategoryClick(item)"
          >
            <div>{{ item[titleKey] }}</div>
            <div v-if="item.id !== '0'">
              <ElDropdown @click.stop>
                <span class="dropdown-trigger">
                  <ElIcon><MoreFilled /></ElIcon>
                </span>
                <template #dropdown>
                  <ElDropdownMenu>
                    <ElDropdownItem @click="handleEditClick($event, item)">
                      {{ $t('button.edit') }}
                    </ElDropdownItem>
                    <ElDropdownItem
                      @click="handleDeleteClick($event, item)"
                      divided
                      style="color: red"
                    >
                      {{ $t('button.delete') }}
                    </ElDropdownItem>
                  </ElDropdownMenu>
                </template>
              </ElDropdown>
            </div>
          </div>
        </div>
      </div>
    </div>

    <ElButton @click="handleAdd" :icon="Plus" :disabled="disabled" plain>
      {{ $t('button.add') }}
    </ElButton>

    <!-- 无数据提示 -->
    <div v-if="categoryData.length === 0 && !loading" class="no-data">
      <ElEmpty :description="$t('common.noDataAvailable')" />
    </div>

    <!-- 新增/编辑弹窗 -->
    <ElDialog
      :title="
        isEdit ? `${$t('button.edit')}${title}` : `${$t('button.add')}${title}`
      "
      v-model="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
    >
      <!-- 表单内容（父组件通过插槽配置） -->
      <slot name="form" :form-data="formData" :is-edit="isEdit"></slot>
      <ElForm :model="formData" status-icon>
        <ElFormItem :prop="titleKey">
          <ElInput v-model.trim="formData[titleKey]" />
        </ElFormItem>
      </ElForm>

      <template #footer>
        <ElButton @click="dialogVisible = false">
          {{ $t('button.cancel') }}
        </ElButton>
        <ElButton type="primary" @click="handleSubmit">
          {{ $t('button.confirm') }}
        </ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<style scoped>
.crud-category-item:hover {
  background-color: var(--el-color-primary-light-9);
  cursor: pointer;
}

.crud-category-item.selected {
  background-color: var(--el-color-primary-light-9);
  color: var(--el-text-color-primary);
}

.no-data {
  text-align: center;
  padding: 40px 0;
}

.crud-category-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  height: 40px;
  border-radius: 8px;
  font-size: 14px;
}

.crud-category-item :deep(.el-icon) {
  outline: none !important;
}

.dropdown-trigger {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 4px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.dropdown-trigger:hover {
  background-color: #f0f0f0;
}
</style>
