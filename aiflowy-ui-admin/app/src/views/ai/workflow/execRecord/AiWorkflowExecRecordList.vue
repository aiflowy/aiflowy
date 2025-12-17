<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import {
  ArrowLeft,
  DeleteFilled,
  MoreFilled,
  Operation,
} from '@element-plus/icons-vue';
import {
  ElButton,
  ElDropdown,
  ElDropdownItem,
  ElDropdownMenu,
  ElForm,
  ElFormItem,
  ElIcon,
  ElInput,
  ElMessage,
  ElMessageBox,
  ElTable,
  ElTableColumn,
  ElTag,
} from 'element-plus';

import { api } from '#/api/request';
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
  execKey: '',
});
const dictStore = useDictStore();
function initDict() {
  dictStore.fetchDictionary('dataStatus');
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
function remove(row: any) {
  ElMessageBox.confirm($t('message.deleteAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('message.ok'),
    cancelButtonText: $t('message.cancel'),
    type: 'warning',
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        instance.confirmButtonLoading = true;
        api
          .get('/api/v1/aiWorkflowExecRecord/del', { params: { id: row.id } })
          .then((res) => {
            instance.confirmButtonLoading = false;
            if (res.errorCode === 0) {
              ElMessage.success(res.message);
              reset(formRef.value);
              done();
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
function toStepPage(row: any) {
  router.push({
    name: 'RecordStep',
    query: {
      recordId: row.id,
    },
  });
}
function getTagType(row: any) {
  switch (row.status) {
    case 1: {
      return 'primary';
    }
    case 5: {
      return 'warning';
    }
    case 10: {
      return 'danger';
    }
    case 20: {
      return 'success';
    }
    case 21: {
      return 'danger';
    }
    default: {
      return 'info';
    }
  }
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
      <ElFormItem :label="$t('aiWorkflowExecRecord.execKey')" prop="execKey">
        <ElInput
          v-model="formInline.execKey"
          :placeholder="$t('aiWorkflowExecRecord.execKey')"
        />
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
      page-url="/api/v1/aiWorkflowExecRecord/page"
      :page-size="10"
      :extra-query-params="{
        workflowId: $route.query.workflowId,
      }"
    >
      <template #default="{ pageList }">
        <ElTable :data="pageList" border>
          <ElTableColumn
            prop="execKey"
            show-overflow-tooltip
            :label="$t('aiWorkflowExecRecord.execKey')"
          >
            <template #default="{ row }">
              {{ row.execKey }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="title" :label="$t('aiWorkflowExecRecord.title')">
            <template #default="{ row }">
              {{ row.title }}
            </template>
          </ElTableColumn>
          <ElTableColumn
            prop="description"
            :label="$t('aiWorkflowExecRecord.description')"
          >
            <template #default="{ row }">
              {{ row.description }}
            </template>
          </ElTableColumn>
          <ElTableColumn
            show-overflow-tooltip
            prop="input"
            :label="$t('aiWorkflowExecRecord.input')"
          >
            <template #default="{ row }">
              {{ row.input }}
            </template>
          </ElTableColumn>
          <ElTableColumn
            show-overflow-tooltip
            prop="output"
            :label="$t('aiWorkflowExecRecord.output')"
          >
            <template #default="{ row }">
              {{ row.output }}
            </template>
          </ElTableColumn>
          <ElTableColumn
            prop="startTime"
            :label="$t('aiWorkflowExecRecord.execTime')"
          >
            <template #default="{ row }">
              {{ row.execTime || '-' }} ms
            </template>
          </ElTableColumn>
          <ElTableColumn
            prop="status"
            :label="$t('aiWorkflowExecRecord.status')"
          >
            <template #default="{ row }">
              <ElTag :type="getTagType(row)">
                {{ $t(`aiWorkflowExecRecord.status${row.status}`) }}
              </ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn
            show-overflow-tooltip
            prop="errorInfo"
            :label="$t('aiWorkflowExecRecord.errorInfo')"
          >
            <template #default="{ row }">
              {{ row.errorInfo }}
            </template>
          </ElTableColumn>
          <ElTableColumn :label="$t('common.handle')" width="80" align="center">
            <template #default="{ row }">
              <ElDropdown>
                <ElButton link>
                  <ElIcon>
                    <MoreFilled />
                  </ElIcon>
                </ElButton>
                <template #dropdown>
                  <ElDropdownMenu>
                    <div v-access:code="'/api/v1/aiWorkflow/save'">
                      <ElDropdownItem @click="toStepPage(row)">
                        <ElButton :icon="Operation" link>
                          {{ $t('aiWorkflowRecordStep.moduleName') }}
                        </ElButton>
                      </ElDropdownItem>
                    </div>
                    <div v-access:code="'/api/v1/aiWorkflow/save'">
                      <ElDropdownItem @click="remove(row)">
                        <ElButton type="danger" :icon="DeleteFilled" link>
                          {{ $t('button.delete') }}
                        </ElButton>
                      </ElDropdownItem>
                    </div>
                  </ElDropdownMenu>
                </template>
              </ElDropdown>
            </template>
          </ElTableColumn>
        </ElTable>
      </template>
    </PageData>
  </div>
</template>

<style scoped></style>
