<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { onMounted, ref } from 'vue';

import {
  ElButton,
  ElDatePicker,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElInputNumber,
  ElMessage,
} from 'element-plus';

import { api } from '#/api/request';
import DictSelect from '#/components/dict/DictSelect.vue';
import { $t } from '#/locales';

const props = withDefaults(defineProps<RecordModalProps>(), {});
const emit = defineEmits(['reload']);
// vue
export interface RecordModalProps {
  formItems: any[];
  tableId: any;
}
onMounted(() => {});
defineExpose({
  openDialog,
});
const saveForm = ref<FormInstance>();
// variables
const dialogVisible = ref(false);
const isAdd = ref(true);
const entity = ref<any>({});
const btnLoading = ref(false);
const rules = ref({});

// functions
function openDialog(row: any) {
  if (row.id) {
    isAdd.value = false;
  }
  entity.value = row;
  dialogVisible.value = true;
}
function save() {
  saveForm.value?.validate((valid) => {
    if (valid) {
      const data: Record<string, any> = {};
      for (const formItem of props.formItems) {
        data[formItem.key] = entity.value[formItem.key];
      }
      data.tableId = props.tableId;
      data.id = entity.value.id;
      api
        .postForm('/api/v1/datacenterTable/saveValue', data)
        .then((res) => {
          btnLoading.value = false;
          if (res.errorCode === 0) {
            ElMessage.success(res.message);
            emit('reload');
            closeDialog();
          }
        })
        .catch(() => {
          btnLoading.value = false;
        });
    }
  });
}
function closeDialog() {
  saveForm.value?.resetFields();
  isAdd.value = true;
  entity.value = {};
  dialogVisible.value = false;
}
</script>

<template>
  <ElDialog
    v-model="dialogVisible"
    draggable
    :title="isAdd ? $t('button.add') : $t('button.edit')"
    :before-close="closeDialog"
    :close-on-click-modal="false"
    width="800px"
  >
    <ElForm
      label-width="100px"
      ref="saveForm"
      :model="entity"
      status-icon
      :rules="rules"
    >
      <ElFormItem
        v-for="item in props.formItems"
        :rules="
          item.required
            ? [
                {
                  required: true,
                  message: `${item.title}${$t('message.notEmpty')}`,
                },
              ]
            : []
        "
        :key="item.key"
        :prop="item.key"
        :label="item.title"
      >
        <ElInput v-if="item.fieldType === 1" v-model.trim="entity[item.key]" />
        <ElInputNumber
          v-if="item.fieldType === 2"
          v-model.number="entity[item.key]"
          :precision="0"
        />
        <ElDatePicker
          v-if="item.fieldType === 3"
          v-model="entity[item.key]"
          type="datetime"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
        <ElInputNumber
          v-if="item.fieldType === 4"
          v-model="entity[item.key]"
          :precision="2"
        />
        <DictSelect
          v-if="item.fieldType === 5"
          v-model="entity[item.key]"
          dict-code="yesOrNo"
        />
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

<style scoped></style>
