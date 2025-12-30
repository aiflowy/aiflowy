<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { ref } from 'vue';

import { IconifyIcon } from '@aiflowy/icons';
import { tryit } from '@aiflowy/utils';

import { MoreFilled, View } from '@element-plus/icons-vue';
import {
  ElButton,
  ElDropdown,
  ElDropdownItem,
  ElDropdownMenu,
  ElForm,
  ElFormItem,
  ElIcon,
  ElInput,
  ElSelect,
  ElTable,
  ElTableColumn,
  ElTag,
} from 'element-plus';

import { api } from '#/api/request';
import DictSelect from '#/components/dict/DictSelect.vue';
import PageData from '#/components/page/PageData.vue';
import { $t } from '#/locales';
import { router } from '#/router';

const feedbackTypeOptions = [
  {
    value: 1,
    label: $t('sysFeedback.functionalFailure'),
  },
  {
    value: 2,
    label: $t('sysFeedback.optimizationSuggestions'),
  },
  {
    value: 3,
    label: $t('sysFeedback.accountIssue'),
  },
  {
    value: 4,
    label: $t('sysFeedback.other'),
  },
];
const statusType: Record<number, any> = {
  0: {
    label: $t('sysFeedback.notViewed'),
    type: 'warning',
  },
  1: {
    label: $t('sysFeedback.viewed'),
    type: 'primary',
  },
  2: {
    label: $t('sysFeedback.processed'),
    type: 'success',
  },
  3: {
    label: $t('sysFeedback.closed/Invalid'),
    type: 'info',
  },
};

const formRef = ref<FormInstance>();
const formData = ref({
  feedbackType: '',
  status: '',
  feedbackContent: '',
});
const pageDataRef = ref();

function search(formEl?: FormInstance) {
  formEl?.validate((valid) => {
    if (valid) {
      pageDataRef.value.setQuery(formData.value);
    }
  });
}
function reset(formEl?: FormInstance) {
  formEl?.resetFields();
  pageDataRef.value.setQuery({});
}
function getFeedbackType(type: number) {
  const option = feedbackTypeOptions.find((option) => option.value === type);
  return option?.label;
}
async function markStatus(row: any, status: number) {
  const [, res] = await tryit(
    api.post('/api/v1/sysUserFeedback/update', { ...row, status }),
  );

  if (res && res.errorCode === 0) {
    pageDataRef.value.setQuery({});
  }
}
</script>

<template>
  <div class="flex h-full flex-col gap-1.5 p-6">
    <ElForm ref="formRef" inline :model="formData">
      <ElFormItem prop="feedbackType" class="!mr-3">
        <ElSelect
          v-model="formData.feedbackType"
          :options="feedbackTypeOptions"
          :placeholder="$t('sysFeedback.feedbackType')"
          clearable
          filterable
          immediate
        />
      </ElFormItem>
      <ElFormItem prop="status" class="!mr-3">
        <DictSelect
          v-model="formData.status"
          dict-code="feedbackType"
          :placeholder="$t('sysFeedback.processingStatus')"
        />
      </ElFormItem>
      <ElFormItem prop="feedbackContent" class="!mr-3">
        <ElInput
          v-model="formData.feedbackContent"
          :placeholder="$t('common.searchPlaceholder')"
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

    <div class="bg-background border-border flex-1 rounded-lg border p-5">
      <PageData
        ref="pageDataRef"
        page-url="/api/v1/sysUserFeedback/page"
        :page-size="10"
      >
        <template #default="{ pageList }">
          <ElTable border show-overflow-tooltip :data="pageList">
            <ElTableColumn prop="id" label="ID">
              <template #default="{ row }">
                {{ row.id }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              prop="feedbackType"
              :label="$t('sysFeedback.category')"
            >
              <template #default="{ row }">
                {{ getFeedbackType(row.feedbackType) }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              prop="feedbackContent"
              :label="$t('sysFeedback.description')"
            >
              <template #default="{ row }">
                {{ row.feedbackContent }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              prop="contactInfo"
              :label="$t('sysFeedback.contactInformation')"
            >
              <template #default="{ row }">
                {{ row.contactInfo }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              prop="created"
              :label="$t('sysFeedback.submittedAt')"
            >
              <template #default="{ row }">
                {{ row.created }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              prop="status"
              :label="$t('sysFeedback.processingStatus')"
            >
              <template #default="{ row }">
                <ElTag :type="statusType[(row.status as number) ?? 0].type">
                  {{ statusType[row.status ?? 0].label }}
                </ElTag>
              </template>
            </ElTableColumn>
            <ElTableColumn
              :label="$t('common.handle')"
              width="90"
              align="right"
            >
              <template #default="{ row }">
                <div class="flex items-center gap-3">
                  <ElButton
                    link
                    type="primary"
                    @click="router.push(`/sys/sysFeedback/${row.id}`)"
                  >
                    {{ $t('button.view') }}
                  </ElButton>

                  <ElDropdown>
                    <ElButton link :icon="MoreFilled" />

                    <template #dropdown>
                      <ElDropdownMenu>
                        <ElDropdownItem
                          :icon="View"
                          :disabled="(row.status ?? 0) >= 1"
                          @click="markStatus(row, 1)"
                        >
                          {{ $t('button.markAsRead') }}
                        </ElDropdownItem>
                        <ElDropdownItem
                          :disabled="(row.status ?? 0) >= 2"
                          @click="markStatus(row, 2)"
                        >
                          <ElIcon>
                            <IconifyIcon icon="svg:resolved" />
                          </ElIcon>
                          {{ $t('button.markAsResolved') }}
                        </ElDropdownItem>
                      </ElDropdownMenu>
                    </template>
                  </ElDropdown>
                </div>
              </template>
            </ElTableColumn>
          </ElTable>
        </template>
      </PageData>
    </div>
  </div>
</template>
