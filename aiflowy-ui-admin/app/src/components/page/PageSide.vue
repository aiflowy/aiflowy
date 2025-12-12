<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { computed, onMounted, ref } from 'vue';

import { Menu, MoreFilled, Plus } from '@element-plus/icons-vue';
import {
  ElButton,
  ElDialog,
  ElDropdown,
  ElDropdownItem,
  ElDropdownMenu,
  ElForm,
  ElFormItem,
  ElIcon,
  ElInput,
  ElInputNumber,
  ElMessage,
  ElMessageBox,
  ElTooltip,
} from 'element-plus';

import { api } from '#/api/request';
import { $t } from '#/locales';

interface FieldDefinition {
  // 字段名称
  prop: string;
  // 字段标签
  label: string;
  // 字段类型：input, number, select, radio, checkbox, switch, date, datetime
  type?: 'input' | 'number';
  // 是否必填
  required?: boolean;
  // 占位符
  placeholder?: string;
}
interface PageSideProps {
  title?: string;
  iconKey?: string;
  nameKey?: string;
  valueKey?: string;
  listUrl: string;
  saveUrl: string;
  updateUrl: string;
  deleteUrl: string;
  collapsedEnabled?: boolean;
  fields: FieldDefinition[];
  collapseWidth?: number;
  expandWidth?: number;
  extraQueryParams?: any;
}
const props = withDefaults(defineProps<PageSideProps>(), {
  title: '',
  iconKey: 'avatar',
  nameKey: 'title',
  valueKey: 'id',
  collapsedEnabled: true,
  collapseWidth: 50,
  expandWidth: 225,
  extraQueryParams: {},
});
const emit = defineEmits(['onSelected']);
onMounted(() => {
  getList();
});
const collapsed = ref(false);
const panelWidth = computed(() => {
  return collapsed.value ? props.collapseWidth : props.expandWidth;
});
const list = ref<any>([]);
const selected = ref('');
const listLoading = ref(false);
const formData = ref<any>({});
const dialogVisible = ref(false);
const formRef = ref<FormInstance>();
const saveLoading = ref(false);
const queryParams = ref<any>({});
function handleClick(item: any) {
  selected.value = item[props.valueKey];
  emit('onSelected', selected.value);
}
function getList() {
  listLoading.value = true;
  api
    .get(props.listUrl, {
      params: { ...queryParams.value, ...props.extraQueryParams },
    })
    .then((res) => {
      listLoading.value = false;
      list.value = res.data;
    });
}
function showDialog(item: any) {
  formRef.value?.resetFields();
  formData.value = { ...item };
  dialogVisible.value = true;
}
function handleSubmit() {
  formRef.value?.validate((valid) => {
    if (valid) {
      saveLoading.value = true;
      const url = formData.value.id ? props.updateUrl : props.saveUrl;
      api.post(url, formData.value).then((res) => {
        saveLoading.value = false;
        if (res.errorCode === 0) {
          ElMessage.success(res.message);
          dialogVisible.value = false;
          getList();
        }
      });
    }
  });
}
function remove(row: any) {
  ElMessageBox.confirm($t('message.deleteAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('message.ok'),
    cancelButtonText: $t('message.cancel'),
    type: 'warning',
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        instance.confirmButtonLoading = true;
        api
          .post(props.deleteUrl, { id: row.id })
          .then((res) => {
            instance.confirmButtonLoading = false;
            if (res.errorCode === 0) {
              ElMessage.success(res.message);
              done();
              getList();
            }
          })
          .catch(() => {
            instance.confirmButtonLoading = false;
          });
      } else {
        done();
      }
    },
  }).catch(() => {});
}
const listData = computed(() => {
  return [
    {
      [props.valueKey]: '',
      [props.nameKey]: $t('common.allCategories'),
    },
    ...list.value,
  ];
});
const formRules = computed(() => {
  const rules: Record<string, any[]> = {};
  props.fields.forEach((field) => {
    const fieldRules = [];
    if (field.required) {
      fieldRules.push({
        required: true,
        message: `${$t('message.required')}`,
        trigger: 'blur',
      });
    }
    if (fieldRules.length > 0) {
      rules[field.prop] = fieldRules;
    }
  });
  return rules;
});
</script>

<template>
  <div
    class="flex w-[225px] flex-col rounded-lg border border-[var(--el-border-color)] bg-[var(--el-bg-color)] p-2"
    :style="{ width: `${panelWidth}px` }"
  >
    <div class="flex flex-1 flex-col gap-5">
      <h3 class="text-base font-medium">{{ title }}</h3>

      <div class="flex-1 overflow-scroll">
        <div
          v-for="item in listData"
          :key="item[valueKey]"
          class="list-item"
          :class="{ selected: selected === item[valueKey] }"
          @click="handleClick(item)"
        >
          <div class="flex items-center gap-1">
            <div
              class="ml-[-3px] flex items-center justify-center"
              v-if="collapsed"
            >
              <ElTooltip placement="right" :content="item[nameKey]">
                <ElIcon>
                  <Menu />
                </ElIcon>
              </ElTooltip>
            </div>
            <div v-if="!collapsed">
              {{ item[nameKey] }}
            </div>
          </div>
          <div v-if="!collapsed && item[valueKey]">
            <ElDropdown @click.stop>
              <div class="icon-btn">
                <ElIcon>
                  <MoreFilled />
                </ElIcon>
              </div>
              <template #dropdown>
                <ElDropdownMenu>
                  <ElDropdownItem @click="showDialog(item)">
                    {{ $t('button.edit') }}
                  </ElDropdownItem>
                  <ElDropdownItem @click="remove(item)" style="color: red">
                    {{ $t('button.delete') }}
                  </ElDropdownItem>
                </ElDropdownMenu>
              </template>
            </ElDropdown>
          </div>
        </div>
      </div>
    </div>

    <ElButton @click="showDialog({})" :icon="Plus" plain>
      {{ $t('button.add') }}
    </ElButton>

    <!-- <div class="head">
      <div class="flex items-center gap-0.5" v-if="!collapsed">
        <div class="icon-btn" @click="showDialog({})">
          <ElIcon>
            <Plus />
          </ElIcon>
        </div>
        <div>{{ title }}</div>
      </div>
      <div class="flex items-center">
        <div class="icon-btn" v-if="!collapsed" @click="getList">
          <ElIcon>
            <Refresh />
          </ElIcon>
        </div>
        <div class="icon-btn" @click="collapsed = !collapsed">
          <ElIcon>
            <ArrowRight v-if="collapsed" />
            <ArrowLeft v-else />
          </ElIcon>
        </div>
      </div>
    </div> -->
    <!-- <div class="content" v-loading="listLoading">
      <div
        v-for="item in listData"
        :key="item[valueKey]"
        class="list-item"
        :class="{ selected: selected === item[valueKey] }"
        @click="handleClick(item)"
      >
        <div class="flex items-center gap-1">
          <div
            class="ml-[-3px] flex items-center justify-center"
            v-if="collapsed"
          >
            <ElTooltip placement="right" :content="item[nameKey]">
              <ElIcon>
                <Menu />
              </ElIcon>
            </ElTooltip>
          </div>
          <div v-if="!collapsed">
            {{ item[nameKey] }}
          </div>
        </div>
        <div v-if="!collapsed && item[valueKey]">
          <ElDropdown @click.stop>
            <div class="icon-btn">
              <ElIcon>
                <MoreFilled />
              </ElIcon>
            </div>
            <template #dropdown>
              <ElDropdownMenu>
                <ElDropdownItem @click="showDialog(item)">
                  {{ $t('button.edit') }}
                </ElDropdownItem>
                <ElDropdownItem @click="remove(item)" style="color: red">
                  {{ $t('button.delete') }}
                </ElDropdownItem>
              </ElDropdownMenu>
            </template>
          </ElDropdown>
        </div>
      </div>
    </div> -->
  </div>
  <ElDialog
    v-model="dialogVisible"
    :title="formData.id ? `${$t('button.edit')}` : `${$t('button.add')}`"
    :close-on-click-modal="false"
  >
    <ElForm
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
    >
      <!-- 动态生成表单项 -->
      <ElFormItem
        v-for="field in fields"
        :key="field.prop"
        :label="field.label"
        :prop="field.prop"
      >
        <ElInput
          v-if="!field.type || field.type === 'input'"
          v-model="formData[field.prop]"
          :placeholder="field.placeholder"
        />
        <ElInputNumber
          v-else-if="field.type === 'number'"
          v-model="formData[field.prop]"
          :placeholder="field.placeholder"
          style="width: 100%"
        />
      </ElFormItem>
    </ElForm>

    <template #footer>
      <ElButton @click="dialogVisible = false">
        {{ $t('button.cancel') }}
      </ElButton>
      <ElButton type="primary" @click="handleSubmit" :loading="saveLoading">
        {{ $t('button.confirm') }}
      </ElButton>
    </template>
  </ElDialog>
</template>

<style scoped>
.icon-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}
.icon-btn:hover {
  background-color: var(--el-fill-color-light);
}
.list-item {
  display: flex;
  align-items: center;
  gap: 10px;
  justify-content: space-between;
  padding: 10px;
  margin-bottom: 4px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
}
.list-item:hover {
  background-color: var(--el-color-primary-light-9);
}
.list-item.selected {
  background-color: var(--el-color-primary-light-8);
  color: var(--el-color-primary);
}
</style>
