<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { onMounted, ref, watch } from 'vue';

import { InfoFilled } from '@element-plus/icons-vue';
import {
  ElButton,
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
import UploadAvatar from '#/components/upload/UploadAvatar.vue';
import { $t } from '#/locales';

const props = defineProps({
  detailData: {
    type: Object,
    default: () => ({
      id: '',
      alias: '',
      deptId: '',
      icon: '',
      title: '',
      description: '',
      slug: '',
      vectorStoreEnable: false,
      vectorStoreType: '',
      vectorStoreCollection: '',
      vectorStoreConfig: '',
      vectorEmbedModelId: '',
      options: {
        canUpdateEmbeddingModel: true,
      },
      rerankModelId: '',
      searchEngineEnable: false,
      englishName: '',
    }),
    required: true,
  },
});

const emit = defineEmits(['reload']);

const entity = ref<any>({ ...props.detailData });

watch(
  () => props.detailData,
  (newVal) => {
    entity.value = { ...newVal };
  },
  { immediate: true, deep: true },
);

const embeddingLlmList = ref<any>([]);
const rerankerLlmList = ref<any>([]);
const vecotrDatabaseList = ref<any>([
  // { value: 'milvus', label: 'Milvus' },
  { value: 'redis', label: 'Redis' },
  { value: 'opensearch', label: 'OpenSearch' },
  { value: 'elasticsearch', label: 'ElasticSearch' },
  { value: 'aliyun', label: $t('documentCollection.alibabaCloud') },
  { value: 'qcloud', label: $t('documentCollection.tencentCloud') },
]);

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

async function save() {
  try {
    const valid = await saveForm.value?.validate();
    if (!valid) return;

    btnLoading.value = true;
    const res = await api.post(
      '/api/v1/documentCollection/update',
      entity.value,
    );

    if (res.errorCode === 0) {
      ElMessage.success($t('message.saveOkMessage'));
      emit('reload');
    }
  } catch (error) {
    ElMessage.error($t('message.saveFail'));
    console.error('保存失败：', error);
  } finally {
    btnLoading.value = false;
  }
}
</script>

<template>
  <div class="document-config-container">
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
      <ElFormItem style="margin-top: 20px; text-align: right">
        <ElButton
          type="primary"
          @click="save"
          :loading="btnLoading"
          :disabled="btnLoading"
        >
          {{ $t('button.save') }}
        </ElButton>
      </ElFormItem>
    </ElForm>
  </div>
</template>

<style scoped>
.document-config-container {
  height: 100%;
  overflow: auto;
}
</style>
