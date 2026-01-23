<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { onMounted, ref } from 'vue';

import { InfoFilled } from '@element-plus/icons-vue';
import {
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElIcon,
  ElInput,
  ElMessage,
  ElOption,
  ElSelect,
  ElSwitch,
  ElTooltip,
} from 'element-plus';

import { api } from '#/api/request';
import DictSelect from '#/components/dict/DictSelect.vue';
import UploadAvatar from '#/components/upload/UploadAvatar.vue';
import { $t } from '#/locales';

const emit = defineEmits(['reload']);
const embeddingLlmList = ref<any>([]);
const rerankerLlmList = ref<any>([]);

const getEmbeddingLlmListData = async () => {
  try {
    const url = `/api/v1/model/list?modelType=embeddingModel`;
    const res = await api.get(url, {});
    if (res.errorCode === 0) {
      embeddingLlmList.value = res.data;
    }
  } catch (error) {
    ElMessage.error($t('message.apiError'));
    console.error('获取嵌入模型列表失败：', error);
  }
};

const getRerankerLlmListData = async () => {
  try {
    const res = await api.get('/api/v1/model/list?modelType=rerankModel');
    rerankerLlmList.value = res.data;
  } catch (error) {
    ElMessage.error($t('message.apiError'));
    console.error('获取重排模型列表失败：', error);
  }
};

onMounted(async () => {
  await getEmbeddingLlmListData();
  await getRerankerLlmListData();
});

const saveForm = ref<FormInstance>();
const dialogVisible = ref(false);
const isAdd = ref(true);
const vecotrDatabaseList = ref<any>([
  { value: 'milvus', label: 'Milvus' },
  { value: 'redis', label: 'Redis' },
  { value: 'opensearch', label: 'OpenSearch' },
  { value: 'elasticsearch', label: 'ElasticSearch' },
  { value: 'aliyun', label: $t('documentCollection.alibabaCloud') },
  { value: 'qcloud', label: $t('documentCollection.tencentCloud') },
]);

const defaultEntity = {
  alias: '',
  deptId: '',
  icon: '',
  title: '',
  categoryId: '',
  description: '',
  slug: '',
  vectorStoreEnable: false,
  vectorStoreType: '',
  vectorStoreCollection: '',
  vectorStoreConfig: '',
  vectorEmbedModelId: '',
  dimensionOfVectorModel: undefined,
  options: {
    canUpdateEmbeddingModel: true,
  },
  rerankModelId: '',
  searchEngineEnable: '',
  englishName: '',
};
const entity = ref<any>({ ...defaultEntity });

const btnLoading = ref(false);
const rules = ref({
  deptId: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  englishName: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  description: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  title: [{ required: true, message: $t('message.required'), trigger: 'blur' }],
  vectorStoreType: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  vectorStoreCollection: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  vectorStoreConfig: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
  vectorEmbedModelId: [
    { required: true, message: $t('message.required'), trigger: 'blur' },
  ],
});

function openDialog(row: any = {}) {
  if (row.id) {
    isAdd.value = false;
    entity.value = {
      ...defaultEntity,
      ...row,
      options: { ...defaultEntity.options, ...row.options },
    };
  } else {
    isAdd.value = true;
    entity.value = { ...defaultEntity };
  }
  dialogVisible.value = true;
}

async function save() {
  try {
    const valid = await saveForm.value?.validate();
    if (!valid) return;

    btnLoading.value = true;
    const res = await api.post(
      isAdd.value
        ? '/api/v1/documentCollection/save'
        : '/api/v1/documentCollection/update',
      entity.value,
    );

    if (res.errorCode === 0) {
      ElMessage.success(res.message || $t('message.saveSuccess'));
      emit('reload');
      closeDialog();
    } else {
      ElMessage.error(res.message || $t('message.saveFail'));
    }
  } catch (error) {
    ElMessage.error($t('message.saveFail'));
    console.error('保存失败：', error);
  } finally {
    btnLoading.value = false;
  }
}

function closeDialog() {
  saveForm.value?.resetFields();
  isAdd.value = true;
  entity.value = { ...defaultEntity };
  dialogVisible.value = false;
}

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
    align-center
  >
    <ElForm
      label-width="150px"
      ref="saveForm"
      :model="entity"
      status-icon
      :rules="rules"
    >
      <ElFormItem
        prop="icon"
        :label="$t('documentCollection.icon')"
        style="display: flex; align-items: center"
      >
        <UploadAvatar v-model="entity.icon" />
      </ElFormItem>
      <ElFormItem prop="title" :label="$t('documentCollection.title')">
        <ElInput
          v-model.trim="entity.title"
          :placeholder="$t('documentCollection.placeholder.title')"
        />
      </ElFormItem>
      <ElFormItem
        prop="categoryId"
        :label="$t('documentCollection.categoryId')"
      >
        <DictSelect
          v-model="entity.categoryId"
          dict-code="aiDocumentCollectionCategory"
        />
      </ElFormItem>
      <ElFormItem prop="alias" :label="$t('documentCollection.alias')">
        <ElInput v-model.trim="entity.alias" />
      </ElFormItem>
      <ElFormItem
        prop="englishName"
        :label="$t('documentCollection.englishName')"
      >
        <ElInput v-model.trim="entity.englishName" />
      </ElFormItem>
      <ElFormItem
        prop="description"
        :label="$t('documentCollection.description')"
      >
        <ElInput
          v-model.trim="entity.description"
          :rows="4"
          type="textarea"
          :placeholder="$t('documentCollection.placeholder.description')"
        />
      </ElFormItem>
      <!--      <ElFormItem
        prop="vectorStoreEnable"
        :label="$t('documentCollection.vectorStoreEnable')"
      >
        <ElSwitch v-model="entity.vectorStoreEnable" />
      </ElFormItem>-->
      <ElFormItem
        prop="vectorStoreType"
        :label="$t('documentCollection.vectorStoreType')"
      >
        <ElSelect
          v-model="entity.vectorStoreType"
          :placeholder="$t('documentCollection.placeholder.vectorStoreType')"
        >
          <ElOption
            v-for="item in vecotrDatabaseList"
            :key="item.value"
            :label="item.label"
            :value="item.value || ''"
          />
        </ElSelect>
      </ElFormItem>
      <ElFormItem
        prop="vectorStoreCollection"
        :label="$t('documentCollection.vectorStoreCollection')"
      >
        <ElInput
          v-model.trim="entity.vectorStoreCollection"
          :placeholder="
            $t('documentCollection.placeholder.vectorStoreCollection')
          "
        />
      </ElFormItem>
      <ElFormItem
        prop="vectorStoreConfig"
        :label="$t('documentCollection.vectorStoreConfig')"
      >
        <ElInput
          v-model.trim="entity.vectorStoreConfig"
          :rows="4"
          type="textarea"
        />
      </ElFormItem>
      <ElFormItem prop="vectorEmbedModelId">
        <template #label>
          <span style="display: flex; align-items: center">
            {{ $t('documentCollection.vectorEmbedLlmId') }}
            <ElTooltip
              :content="$t('documentCollection.vectorEmbedModelTips')"
              placement="top"
              effect="light"
            >
              <ElIcon
                style="
                  margin-left: 4px;
                  color: #909399;
                  cursor: pointer;
                  font-size: 14px;
                "
              >
                <InfoFilled />
              </ElIcon>
            </ElTooltip>
          </span>
        </template>

        <ElSelect
          v-model="entity.vectorEmbedModelId"
          :disabled="!entity?.options?.canUpdateEmbeddingModel"
          :placeholder="$t('documentCollection.placeholder.embedLlm')"
        >
          <ElOption
            v-for="item in embeddingLlmList"
            :key="item.id"
            :label="item.title"
            :value="item.id || ''"
          />
        </ElSelect>
      </ElFormItem>
      <ElFormItem
        prop="dimensionOfVectorModel"
        :label="$t('documentCollection.dimensionOfVectorModel')"
      >
        <template #label>
          <span style="display: flex; align-items: center">
            {{ $t('documentCollection.dimensionOfVectorModel') }}
            <ElTooltip
              :content="$t('documentCollection.dimensionOfVectorModelTips')"
              placement="top"
              effect="light"
            >
              <ElIcon
                style="
                  margin-left: 4px;
                  color: #909399;
                  cursor: pointer;
                  font-size: 14px;
                "
              >
                <InfoFilled />
              </ElIcon>
            </ElTooltip>
          </span>
        </template>
        <ElInput
          :disabled="!entity?.options?.canUpdateEmbeddingModel"
          v-model.trim="entity.dimensionOfVectorModel"
          type="number"
        />
      </ElFormItem>
      <ElFormItem
        prop="rerankModelId"
        :label="$t('documentCollection.rerankLlmId')"
      >
        <ElSelect
          v-model="entity.rerankModelId"
          :placeholder="$t('documentCollection.placeholder.rerankLlm')"
        >
          <ElOption
            v-for="item in rerankerLlmList"
            :key="item.id"
            :label="item.title"
            :value="item.id || ''"
          />
        </ElSelect>
      </ElFormItem>
      <ElFormItem
        prop="searchEngineEnable"
        :label="$t('documentCollection.searchEngineEnable')"
      >
        <ElSwitch v-model="entity.searchEngineEnable" />
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
