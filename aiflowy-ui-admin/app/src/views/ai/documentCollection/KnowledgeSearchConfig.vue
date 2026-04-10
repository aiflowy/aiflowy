<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';

import { $t } from '@aiflowy/locales';

import { InfoFilled } from '@element-plus/icons-vue';
import {
  ElButton,
  ElForm,
  ElFormItem,
  ElInputNumber,
  ElMessage,
  ElOption,
  ElSelect,
  ElTag,
  ElTooltip,
} from 'element-plus';

import { api } from '#/api/request';

const props = defineProps({
  documentCollectionId: {
    type: String,
    required: true,
  },
});

onMounted(() => {
  getDocumentCollectionConfig();
});
const searchEngineEnable = ref(false);
const documentCollectionInfo = ref<any>(null);
const getDocumentCollectionConfig = () => {
  api
    .get(`/api/v1/documentCollection/detail?id=${props.documentCollectionId}`)
    .then((res) => {
      const { data } = res;
      documentCollectionInfo.value = res.data;
      searchConfig.docRecallMaxNum =
        data.options.docRecallMaxNum === null
          ? 5
          : Number(data.options.docRecallMaxNum);
      searchConfig.vectorWeight =
        data.options.vectorWeight === null
          ? 0.6
          : Number(data.options.vectorWeight);
      searchConfig.searcherWeight =
        data.options.searcherWeight === null
          ? 0.3
          : Number(data.options.searcherWeight);
      searchConfig.searchEngineType = data.options.searchEngineType || 'lucene';
      searchEngineEnable.value = !!data.searchEngineEnable;
    });
};

const searchConfig = reactive({
  docRecallMaxNum: 5,
  // 向量相似度权重
  vectorWeight: 0.7,
  searcherWeight: 0.3,
  searchEngineType: 'lucene',
});

const weightTotal = computed(
  () =>
    Number(searchConfig.vectorWeight || 0) +
    Number(searchConfig.searcherWeight || 0),
);
computed(() => {
  const total = weightTotal.value;
  if (Math.abs(total - 1) < 0.0001) return 'success';
  if (total < 1) return 'warning';
  return 'danger';
});
const submitConfig = () => {
  if (Math.abs(weightTotal.value - 1) > 0.0001) {
    ElMessage.error('向量、关键词权重之和必须等于 1');
    return;
  }
  // 提交时可根据需要决定是否传递 searchEngineEnable
  const submitData = {
    id: props.documentCollectionId,
    ...documentCollectionInfo.value,
    options: {
      docRecallMaxNum: searchConfig.docRecallMaxNum,
      // 向量权重
      vectorWeight: searchConfig.vectorWeight,
      searcherWeight: searchConfig.searcherWeight,
      searchEngineType: searchConfig.searchEngineType,
    },
    searchEngineEnable: searchEngineEnable.value,
  };

  api
    .post('/api/v1/documentCollection/update', submitData)
    .then(() => {
      ElMessage.success($t('documentCollectionSearch.message.saveSuccess'));
    })
    .catch((error) => {
      ElMessage.error($t('documentCollectionSearch.message.saveFailed'));
      console.error('保存配置失败：', error);
    });
};

const searchEngineOptions = [
  {
    label: 'Lucene',
    value: 'lucene',
  },
  {
    label: 'ElasticSearch',
    value: 'elasticSearch',
  },
];
</script>

<template>
  <div class="search-config-sidebar">
    <div class="config-header">
      <h3>{{ $t('documentCollectionSearch.title') }}</h3>
    </div>

    <ElForm
      class="config-form"
      :model="searchConfig"
      label-width="100%"
      size="small"
    >
      <ElFormItem prop="docRecallMaxNum" class="form-item">
        <div class="form-item-label">
          <span>{{
            $t('documentCollectionSearch.docRecallMaxNum.label')
          }}</span>
          <ElTooltip
            :content="$t('documentCollectionSearch.docRecallMaxNum.tooltip')"
            placement="top"
            effect="dark"
            class="label-tooltip"
          >
            <InfoFilled class="info-icon" />
          </ElTooltip>
        </div>
        <div class="form-item-content">
          <ElInputNumber
            v-model="searchConfig.docRecallMaxNum"
            :min="1"
            :max="50"
            :step="1"
            :placeholder="$t('documentCollectionSearch.placeholder.count')"
            class="form-control"
          >
            <template #append>
              {{ $t('documentCollectionSearch.unit.count') }}
            </template>
          </ElInputNumber>
        </div>
      </ElFormItem>

      <!--      <ElFormItem prop="mixedSimThreshold" class="form-item">-->
      <!--        <div class="form-item-label">-->
      <!--          <span>{{-->
      <!--            $t('documentCollectionSearch.mixedSimThreshold.label')-->
      <!--          }}</span>-->
      <!--          <ElTooltip-->
      <!--            :content="$t('documentCollectionSearch.mixedSimThreshold.tooltip')"-->
      <!--            placement="top"-->
      <!--            effect="dark"-->
      <!--            class="label-tooltip"-->
      <!--          >-->
      <!--            <InfoFilled class="info-icon" />-->
      <!--          </ElTooltip>-->
      <!--        </div>-->
      <!--        <div class="form-item-content">-->
      <!--          <ElInputNumber-->
      <!--            v-model="searchConfig.mixedSimThreshold"-->
      <!--            :min="0"-->
      <!--            :max="1"-->
      <!--            :step="0.01"-->
      <!--            show-input-->
      <!--            class="form-control"-->
      <!--          />-->
      <!--        </div>-->
      <!--      </ElFormItem>-->

      <ElFormItem prop="vectorWeight" class="form-item">
        <div class="form-item-label">
          <span>{{ $t('documentCollectionSearch.vectorWeight.label') }}</span>
          <ElTooltip
            :content="$t('documentCollectionSearch.vectorWeight.tooltip')"
            placement="top"
            effect="dark"
            class="label-tooltip"
          >
            <InfoFilled class="info-icon" />
          </ElTooltip>
        </div>
        <div class="form-item-content">
          <ElInputNumber
            v-model="searchConfig.vectorWeight"
            :min="0"
            :max="1"
            :step="0.01"
            show-input
            class="form-control"
          />
        </div>
      </ElFormItem>

      <ElFormItem prop="searcherWeight" class="form-item">
        <div class="form-item-label">
          <span>{{ $t('documentCollectionSearch.searcherWeight.label') }}</span>
          <ElTooltip
            :content="$t('documentCollectionSearch.searcherWeight.tooltip')"
            placement="top"
            effect="dark"
            class="label-tooltip"
          >
            <InfoFilled class="info-icon" />
          </ElTooltip>
        </div>
        <div class="form-item-content">
          <ElInputNumber
            v-model="searchConfig.searcherWeight"
            :min="0"
            :max="1"
            :step="0.01"
            show-input
            class="form-control"
          />
        </div>
      </ElFormItem>

      <ElFormItem class="form-item weight-total-item">
        <div class="form-item-label">
          <span>权重总和</span>
          <ElTooltip
            content="向量、关键词权重之和必须等于1"
            placement="top"
            effect="dark"
            class="label-tooltip"
          >
            <InfoFilled class="info-icon" />
          </ElTooltip>
        </div>
        <div class="form-item-content weight-total-content">
          <ElTag
            size="large"
            effect="dark"
            class="weight-total-tag"
            :style="{
              backgroundColor: 'var(--el-color-primary)',
              borderColor: 'var(--el-color-primary)',
              color: '#fff',
            }"
          >
            {{ weightTotal.toFixed(2) }}
          </ElTag>
        </div>
      </ElFormItem>

      <ElFormItem
        v-if="searchEngineEnable"
        prop="searchEngineType"
        class="form-item"
      >
        <div class="form-item-label">
          <span>{{
            $t('documentCollectionSearch.searchEngineType.label')
          }}</span>
          <ElTooltip
            :content="$t('documentCollectionSearch.searchEngineType.tooltip')"
            placement="top"
            effect="dark"
            class="label-tooltip"
          >
            <InfoFilled class="info-icon" />
          </ElTooltip>
        </div>
        <div class="form-item-content">
          <ElSelect
            v-model="searchConfig.searchEngineType"
            :placeholder="
              $t('documentCollectionSearch.searchEngineType.placeholder')
            "
            class="form-control"
          >
            <ElOption
              v-for="option in searchEngineOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </ElSelect>
        </div>
      </ElFormItem>
    </ElForm>

    <div class="config-footer">
      <ElButton type="primary" @click="submitConfig" class="submit-btn">
        {{ $t('documentCollectionSearch.button.save') }}
      </ElButton>
    </div>
  </div>
</template>

<style scoped>
.search-config-sidebar {
  width: 60%;
  height: 100%;
  padding: 16px;
  box-sizing: border-box;
  overflow-y: auto;
  overflow-x: hidden;
}

.config-header {
  margin-bottom: 16px;
  border-bottom: 1px solid #e6e6e6;
  padding-bottom: 8px;
}

.config-header h3 {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
}

.config-form {
  margin-bottom: 24px;
}

.form-item {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-item-label {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #606266;
  line-height: 1.4;
}

.label-tooltip {
  display: inline-block;
  cursor: help;
}

:deep(.form-item .el-form-item__content) {
  width: 100%;
  margin-left: 0 !important;
}

.form-item-content {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  margin-top: 4px;
}

.form-control {
  flex: 1;
  width: 100%;
  min-width: 0;
}

.switch-control {
  width: auto;
  flex: none;
  min-width: 80px;
}

.weight-total-item {
  flex-direction: row;
  align-items: center;
  gap: 12px;
}

.weight-total-item .form-item-label {
  margin: 0;
  flex: 1;
}

.weight-total-item .form-item-content {
  margin-top: 0;
  flex: 1;
  width: auto;
  display: flex;
  justify-content: flex-end;
}

.weight-total-content {
  justify-content: flex-end;
}

.weight-total-tag {
  margin-left: auto;
  min-width: 90px;
  text-align: center;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.info-icon {
  font-size: 14px;
  color: #909399;
  cursor: help;
  width: 16px;
  height: 16px;
  flex-shrink: 0;
  flex: none;
}

.info-icon:hover {
  color: #409eff;
}

.submit-btn {
  width: 100%;
  padding: 8px 0;
}

.config-footer {
  position: sticky;
  bottom: 0;
  padding-top: 8px;
}

:deep(.el-form-item__content) {
  width: 100%;
  box-sizing: border-box;
}

:deep(.el-slider) {
  --el-slider-input-width: 60px;
}

:deep(.el-input-number),
:deep(.el-select) {
  width: 100%;
}
</style>
