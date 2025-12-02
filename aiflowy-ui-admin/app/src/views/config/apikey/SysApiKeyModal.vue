<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { onMounted, ref } from 'vue';

import {
  ElButton,
  ElCheckbox,
  ElCheckboxGroup,
  ElDatePicker,
  ElDialog,
  ElForm,
  ElFormItem,
  ElMessage,
} from 'element-plus';

import { api } from '#/api/request';
import DictSelect from '#/components/dict/DictSelect.vue';
import { $t } from '#/locales';

// 定义权限接口
interface ResourcePermission {
  id: number;
  title: string;
  requestInterface: string;
}

// 定义表单数据接口
interface Entity {
  apiKey: string;
  status: number | string;
  deptId: number | string;
  expiredAt: Date | null | string;
  permissionIds: (number | string)[]; // 绑定值：权限 ID 数组
  id?: number; // 编辑时的主键
}

const emit = defineEmits(['reload']);

// 表单实例
const saveForm = ref<FormInstance>();
// 对话框状态
const dialogVisible = ref(false);
const isAdd = ref(true);
// 表单数据（初始化默认值）
const entity = ref<Entity>({
  apiKey: '',
  status: '',
  deptId: '',
  expiredAt: null,
  permissionIds: [],
});
// 加载状态
const btnLoading = ref(false);
// 资源权限列表
const resourcePermissionList = ref<ResourcePermission[]>([]);

// 表单校验规则（必填项校验）
const rules = ref({
  status: [
    {
      required: true,
      message: $t('message.pleaseSelect', { name: $t('sysApiKey.status') }),
      trigger: 'change',
    },
  ],
  expiredAt: [
    {
      required: true,
      message: $t('message.pleaseSelect', { name: $t('sysApiKey.expiredAt') }),
      trigger: 'change',
    },
  ],
});

function openDialog(row: Partial<Entity> = {}) {
  saveForm.value?.resetFields();
  entity.value = {
    apiKey: '',
    status: '',
    deptId: '',
    expiredAt: null,
    permissions: [],
    ...row,
  };
  isAdd.value = !row.id;
  dialogVisible.value = true;
}

// 获取资源权限列表
function getResourcePermissionList() {
  api
    .get('/api/v1/sysApiKeyResourcePermission/list')
    .then((res) => {
      if (res.errorCode === 0) {
        resourcePermissionList.value = res.data;
      } else {
        ElMessage.error(res.message || $t('message.fetchFail'));
      }
    })
    .catch(() => {
      ElMessage.error($t('message.fetchFail'));
    });
}

// 保存表单
function save() {
  saveForm.value?.validate((valid) => {
    if (valid) {
      btnLoading.value = true;
      const url = isAdd.value
        ? 'api/v1/sysApiKey/save'
        : 'api/v1/sysApiKey/update';
      api
        .post(url, entity.value)
        .then((res) => {
          btnLoading.value = false;
          if (res.errorCode === 0) {
            ElMessage.success(res.message || $t('message.operateSuccess'));
            emit('reload');
            closeDialog();
          } else {
            ElMessage.error(res.message || $t('message.operateFail'));
          }
        })
        .catch(() => {
          btnLoading.value = false;
          ElMessage.error($t('message.operateFail'));
        });
    }
  });
}

// 关闭对话框
function closeDialog() {
  saveForm.value?.resetFields();
  // 重置表单数据
  entity.value = {
    apiKey: '',
    status: '',
    deptId: '',
    expiredAt: null,
    permissionIds: [],
  };
  isAdd.value = true;
  dialogVisible.value = false;
}

onMounted(() => {
  getResourcePermissionList();
});

defineExpose({
  openDialog,
});
</script>

<template>
  <ElDialog
    v-model="dialogVisible"
    draggable
    :title="isAdd ? $t('button.add') : $t('button.edit')"
    :before-close="closeDialog"
    :close-on-click-modal="false"
    width="600px"
  >
    <ElForm
      label-width="120px"
      ref="saveForm"
      :model="entity"
      status-icon
      :rules="rules"
      class="form-container"
    >
      <!-- 状态选择 -->
      <ElFormItem prop="status" :label="$t('sysApiKey.status')">
        <DictSelect
          v-model="entity.status"
          dict-code="dataStatus"
          style="width: 100%"
        />
      </ElFormItem>

      <ElFormItem prop="expiredAt" :label="$t('sysApiKey.expiredAt')">
        <ElDatePicker
          v-model="entity.expiredAt"
          type="datetime"
          :placeholder="
            $t('message.pleaseSelect', { name: $t('sysApiKey.expiredAt') })
          "
          style="width: 100%"
        />
      </ElFormItem>

      <ElFormItem
        prop="permissions"
        :label="$t('sysApiKey.permissions')"
        class="permission-form-item"
      >
        <ElCheckboxGroup
          v-model="entity.permissionIds"
          class="permission-checkbox-group"
        >
          <ElCheckbox
            v-for="item in resourcePermissionList"
            :key="item.id"
            :value="item.id"
            class="permission-checkbox"
          >
            {{ item.requestInterface }} - {{ item.title }}
          </ElCheckbox>
        </ElCheckboxGroup>
      </ElFormItem>
    </ElForm>

    <template #footer>
      <ElButton @click="closeDialog">
        {{ $t('button.cancel') }}
      </ElButton>
      <ElButton
        type="primary"
        @click="save"
        :loading="btnLoading"
        :disabled="btnLoading"
      >
        {{ $t('button.save') }}
      </ElButton>
    </template>
  </ElDialog>
</template>

<style scoped>
.form-container {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 10px;
}

.permission-form-item .el-form-item__content {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.permission-checkbox {
  margin: 4px 0;
  min-width: 200px;
}

.form-container::-webkit-scrollbar {
  width: 6px;
}
.form-container::-webkit-scrollbar-thumb {
  background-color: #e5e7eb;
  border-radius: 3px;
}
</style>
