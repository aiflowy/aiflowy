<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { ArrowLeft } from '@element-plus/icons-vue';
import {
  ElButton,
  ElForm,
  ElFormItem,
  ElTable,
  ElTableColumn,
} from 'element-plus';

import DictSelect from '#/components/dict/DictSelect.vue';
import PageData from '#/components/page/PageData.vue';
import { $t } from '#/locales';
import { useDictStore } from '#/store';

const router = useRouter();
const $route = useRoute();
onMounted(() => {
  initDict();
});
const formRef = ref<FormInstance>();
const pageDataRef = ref();
const formInline = ref({
  status: '',
});
const dictStore = useDictStore();
function initDict() {
  dictStore.fetchDictionary('jobResult');
}
function search(formEl: FormInstance | undefined) {
  formEl?.validate((valid) => {
    if (valid) {
      pageDataRef.value.setQuery(formInline.value);
    }
  });
}
function reset(formEl: FormInstance | undefined) {
  formEl?.resetFields();
  pageDataRef.value.setQuery({});
}
</script>

<template>
  <div class="page-container">
    <div class="mb-3">
      <ElButton :icon="ArrowLeft" @click="router.back()">
        {{ $t('button.back') }}
      </ElButton>
    </div>
    <ElForm ref="formRef" :inline="true" :model="formInline">
      <ElFormItem :label="$t('sysJobLog.status')" prop="status">
        <DictSelect v-model="formInline.status" dict-code="jobResult" />
      </ElFormItem>
      <ElFormItem>
        <ElButton @click="search(formRef)" type="primary">
          {{ $t('button.query') }}
        </ElButton>
        <ElButton @click="reset(formRef)">
          {{ $t('button.reset') }}
        </ElButton>
      </ElFormItem>
    </ElForm>
    <div class="handle-div"></div>
    <PageData
      ref="pageDataRef"
      page-url="/api/v1/sysJobLog/page"
      :extra-query-params="{
        jobId: $route.query.jobId,
      }"
      :page-size="10"
    >
      <template #default="{ pageList }">
        <ElTable :data="pageList" border>
          <ElTableColumn prop="jobName" :label="$t('sysJobLog.jobName')">
            <template #default="{ row }">
              {{ row.jobName }}
            </template>
          </ElTableColumn>
          <ElTableColumn
            show-overflow-tooltip
            prop="jobParams"
            :label="$t('sysJobLog.jobParams')"
          >
            <template #default="{ row }">
              {{ row.jobParams }}
            </template>
          </ElTableColumn>
          <ElTableColumn
            show-overflow-tooltip
            prop="jobResult"
            :label="$t('sysJobLog.jobResult')"
          >
            <template #default="{ row }">
              {{ row.jobResult }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="errorInfo" :label="$t('sysJobLog.errorInfo')">
            <template #default="{ row }">
              {{ row.errorInfo }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="status" :label="$t('sysJobLog.status')">
            <template #default="{ row }">
              {{ dictStore.getDictLabel('jobResult', row.status) }}
            </template>
          </ElTableColumn>
          <ElTableColumn
            width="180"
            prop="startTime"
            :label="$t('sysJobLog.startTime')"
          >
            <template #default="{ row }">
              {{ row.startTime }}
            </template>
          </ElTableColumn>
          <ElTableColumn
            width="180"
            prop="endTime"
            :label="$t('sysJobLog.endTime')"
          >
            <template #default="{ row }">
              {{ row.endTime }}
            </template>
          </ElTableColumn>
        </ElTable>
      </template>
    </PageData>
  </div>
</template>

<style scoped></style>
