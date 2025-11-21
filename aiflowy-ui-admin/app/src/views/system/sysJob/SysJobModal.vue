<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { onMounted, ref } from 'vue';

import {
  ElAlert,
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
} from 'element-plus';

import { api } from '#/api/request';
import DictSelect from '#/components/dict/DictSelect.vue';
import { $t } from '#/locales';

const emit = defineEmits(['reload']);
// vue
onMounted(() => {});
defineExpose({
  openDialog,
});
const saveForm = ref<FormInstance>();
// variables
const dialogVisible = ref(false);
const isAdd = ref(true);
const initEntity = {
  deptId: '',
  jobName: '',
  jobType: '',
  jobParams: {
    workflowParams: {},
  },
  cronExpression: '',
  allowConcurrent: 0,
  misfirePolicy: 3,
  options: '',
  status: '',
  remark: '',
};
const entity = ref<any>(initEntity);
const btnLoading = ref(false);
// 基础验证规则
const baseRules = ref({
  jobName: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  jobType: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  cronExpression: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  allowConcurrent: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  misfirePolicy: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
});

const paramsLoading = ref(false);
const workflowParams = ref<any[]>([]);
const rules = ref({ ...baseRules.value });

// functions
function openDialog(row: any) {
  if (row.id) {
    entity.value = { ...row };
    // 确保 jobParams 存在
    if (!entity.value.jobParams) {
      entity.value.jobParams = {};
    }
    if (entity.value.jobParams.workflowId) {
      getWorkflowParams(entity.value.jobParams.workflowId);
    }
    isAdd.value = false;
  } else {
    entity.value = { ...initEntity };
  }
  dialogVisible.value = true;
}
function save() {
  saveForm.value?.validate((valid) => {
    if (valid) {
      btnLoading.value = true;
      api
        .post(
          isAdd.value ? 'api/v1/sysJob/save' : 'api/v1/sysJob/update',
          entity.value,
        )
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
  entity.value = { ...initEntity };
  workflowParams.value = [];
  dialogVisible.value = false;
}
function jobTypeChange(v: any) {
  workflowParams.value = [];
  entity.value.jobParams = {
    workflowParams: {},
  };
  if (v === 1) {
    entity.value.jobParams.workflowParams = {};
  }
}
function workflowChange(v: any) {
  entity.value.jobParams.workflowParams = {};
  getWorkflowParams(v);
}
function getWorkflowParams(v: any) {
  paramsLoading.value = true;
  api.get(`/api/v1/aiWorkflow/getRunningParameters?id=${v}`).then((res) => {
    paramsLoading.value = false;
    workflowParams.value = res.data.parameters;
  });
}
const str = '"param"';
</script>

<template>
  <ElDialog
    v-model="dialogVisible"
    draggable
    :title="isAdd ? $t('button.add') : $t('button.edit')"
    :before-close="closeDialog"
    :close-on-click-modal="false"
  >
    <ElForm
      v-loading="paramsLoading"
      label-width="120px"
      ref="saveForm"
      :model="entity"
      status-icon
      :rules="rules"
    >
      <ElFormItem prop="jobName" :label="$t('sysJob.jobName')">
        <ElInput v-model.trim="entity.jobName" />
      </ElFormItem>
      <ElFormItem prop="cronExpression" :label="$t('sysJob.cronExpression')">
        <ElInput v-model.trim="entity.cronExpression" />
      </ElFormItem>
      <ElFormItem prop="jobType" :label="$t('sysJob.jobType')">
        <DictSelect
          v-model="entity.jobType"
          dict-code="jobType"
          @change="jobTypeChange"
        />
      </ElFormItem>
      <ElFormItem
        v-if="entity.jobType === 1"
        :label="$t('sysJob.workflow')"
        prop="jobParams.workflowId"
        :rules="[
          { required: true, message: $t('message.required'), trigger: 'blur' },
        ]"
      >
        <DictSelect
          v-model="entity.jobParams.workflowId"
          dict-code="aiWorkFlow"
          @change="workflowChange"
        />
      </ElFormItem>
      <ElFormItem
        v-for="item in workflowParams"
        :key="item.id"
        :label="item.description || item.name"
        :prop="`jobParams.workflowParams.${item.name}`"
        :rules="
          item.required
            ? [
                {
                  required: true,
                  message: $t('message.required'),
                  trigger: 'blur',
                },
              ]
            : []
        "
      >
        <ElInput v-model="entity.jobParams.workflowParams[item.name]" />
      </ElFormItem>
      <ElFormItem
        v-if="entity.jobType === 2"
        :label="$t('sysJob.beanMethod')"
        prop="jobParams.beanMethod"
        :rules="[
          { required: true, message: $t('message.required'), trigger: 'blur' },
        ]"
      >
        <ElInput v-model="entity.jobParams.beanMethod" />
        <ElAlert
          style="margin-top: 5px"
          :title="`${$t('sysJob.example')}：sysJobServiceImpl.testParam(${str},false,299)`"
        />
      </ElFormItem>
      <ElFormItem
        v-if="entity.jobType === 3"
        :label="$t('sysJob.javaMethod')"
        prop="jobParams.javaMethod"
        :rules="[
          { required: true, message: $t('message.required'), trigger: 'blur' },
        ]"
      >
        <ElInput v-model="entity.jobParams.javaMethod" />
        <ElAlert
          style="margin-top: 5px"
          :title="`${$t('sysJob.example')}：tech.aiflowy.job.util.JobUtil.execTest(${str},1,0.52D,100L)`"
        />
      </ElFormItem>
      <ElFormItem prop="allowConcurrent" :label="$t('sysJob.allowConcurrent')">
        <DictSelect v-model="entity.allowConcurrent" dict-code="yesOrNo" />
      </ElFormItem>
      <ElFormItem prop="misfirePolicy" :label="$t('sysJob.misfirePolicy')">
        <DictSelect v-model="entity.misfirePolicy" dict-code="misfirePolicy" />
      </ElFormItem>
      <ElFormItem prop="remark" :label="$t('sysJob.remark')">
        <ElInput v-model.trim="entity.remark" />
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
