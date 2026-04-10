<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { onMounted, ref } from 'vue';

import {
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
import ModelVerifyVectorDatabaseConfig from '#/views/ai/vectorDatabase/ModelVerifyVectorDatabaseConfig.vue';

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

// 定义默认的配置对象模板，确保vectorDatabaseConfigOptions始终存在
const defaultConfig = {
  deptId: '',
  title: '',
  options: '',
  type: '',
  configOptions: {
    host: '',
    port: '',
    username: '',
    password: '',
    apiKey: '',
    endpoint: '',
  },
};
// 初始化entity，基于默认模板创建新对象
const entity = ref<any>({ ...defaultConfig });

const btnLoading = ref(false);
const rules = ref({
  title: [{ required: true, message: $t('message.required'), trigger: 'blur' }],
  type: [{ required: true, message: $t('message.required'), trigger: 'blur' }],
});

// functions
function openDialog(row: any) {
  entity.value = {
    ...defaultConfig, // 先拿默认模板
    ...row, // 覆盖row中的属性
    // 确保vectorDatabaseConfigOptions始终是对象（即使row中没有该属性）
    configOptions: {
      ...defaultConfig.configOptions,
      ...row?.configOptions,
    },
  };

  if (row.id) {
    isAdd.value = false;
  } else {
    entity.value.type = 'redis';
  }
  dialogVisible.value = true;
}

function save() {
  saveForm.value?.validate((valid) => {
    if (valid) {
      btnLoading.value = true;
      api
        .post(
          isAdd.value
            ? 'api/v1/vectorDatabase/saveConfig'
            : 'api/v1/vectorDatabase/update',
          { ...entity.value },
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
  // 修复：重置时恢复为默认配置模板，而非空对象
  entity.value = { ...defaultConfig };
  dialogVisible.value = false;
}

const redis = ref('redis');
const elasticsearch = ref('elasticsearch');
const opensearch = ref('opensearch');
const aliyun = ref('aliyun');
const tencent = ref('tencent');
const milvus = ref('milvus');

const getShowFirst = () => {
  return (
    entity.value.type === redis.value ||
    entity.value.type === elasticsearch.value ||
    entity.value.type === opensearch.value ||
    entity.value.type === milvus.value
  );
};

const getShowSecond = () => {
  return (
    entity.value.type === elasticsearch.value ||
    entity.value.type === opensearch.value ||
    entity.value.type === milvus.value
  );
};

const canShow = (params: string) => {
  if (params === 'agreement') {
    return getShowSecond();
  }
  if (params === 'host') {
    return getShowFirst();
  }
  if (params === 'port') {
    return getShowFirst();
  }
  if (params === 'username') {
    return getShowFirst();
  }
  if (params === 'password') {
    return getShowFirst();
  }
  if (params === 'apiKey') {
    return (
      entity.value.type === aliyun.value || entity.value.type === tencent.value
    );
  }
  if (params === 'endpoint') {
    return entity.value.type === aliyun.value;
  }
};
const modelVerifyVectorDatabaseConfigRef = ref();
const configurationCheck = () => {
  modelVerifyVectorDatabaseConfigRef.value.openDialog(entity.value);
};
</script>

<template>
  <div>
    <ModelVerifyVectorDatabaseConfig ref="modelVerifyVectorDatabaseConfigRef" />
    <ElDialog
      v-model="dialogVisible"
      draggable
      :title="isAdd ? $t('button.add') : $t('button.edit')"
      :before-close="closeDialog"
      :close-on-click-modal="false"
    >
      <ElForm
        label-width="120px"
        ref="saveForm"
        :model="entity"
        status-icon
        :rules="rules"
      >
        <ElFormItem prop="title" :label="$t('vectorDatabase.title')">
          <ElInput v-model.trim="entity.title" />
        </ElFormItem>
        <ElFormItem prop="type" :label="$t('vectorDatabase.type')">
          <DictSelect v-model="entity.type" dict-code="vectorDatabaseType" />
        </ElFormItem>
        <ElFormItem
          prop="host"
          :label="$t('vectorDatabase.agreement')"
          v-if="canShow('agreement')"
        >
          <DictSelect
            v-model="entity.configOptions.agreement"
            dict-code="agreement"
          />
        </ElFormItem>
        <ElFormItem
          prop="host"
          :label="$t('vectorDatabase.host')"
          v-if="canShow('host') || entity.type === tencent"
        >
          <ElInput v-model.trim="entity.configOptions.host" />
        </ElFormItem>
        <ElFormItem
          prop="port"
          :label="$t('vectorDatabase.port')"
          v-if="canShow('port')"
        >
          <ElInput v-model.trim="entity.configOptions.port" />
        </ElFormItem>
        <ElFormItem
          prop="username"
          :label="$t('vectorDatabase.username')"
          v-if="canShow('username')"
        >
          <ElInput v-model.trim="entity.configOptions.username" />
        </ElFormItem>
        <ElFormItem
          prop="password"
          :label="$t('vectorDatabase.password')"
          v-if="canShow('password')"
        >
          <ElInput v-model.trim="entity.configOptions.password" />
        </ElFormItem>
        <ElFormItem
          prop="apiKey"
          :label="$t('vectorDatabase.apiKey')"
          v-if="canShow('apiKey')"
        >
          <ElInput v-model.trim="entity.configOptions.apiKey" />
        </ElFormItem>
        <ElFormItem
          prop="endpoint"
          :label="$t('vectorDatabase.endpoint')"
          v-if="canShow('endpoint')"
        >
          <ElInput v-model.trim="entity.configOptions.endpoint" />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="configurationCheck">
          {{ $t('vectorDatabase.configurationCheck') }}
        </ElButton>
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
  </div>
</template>

<style scoped></style>
