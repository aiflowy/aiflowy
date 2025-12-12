<script setup lang="ts">
import type { FormInstance } from 'element-plus';

import { markRaw, onMounted, ref } from 'vue';

import {
  CaretRight,
  CircleCloseFilled,
  Delete,
  Edit,
  More,
  Plus,
  Tickets,
} from '@element-plus/icons-vue';
import {
  ElButton,
  ElDropdown,
  ElDropdownItem,
  ElDropdownMenu,
  ElIcon,
  ElMessage,
  ElMessageBox,
  ElTable,
  ElTableColumn,
} from 'element-plus';

import { api } from '#/api/request';
import HeaderSearch from '#/components/headerSearch/HeaderSearch.vue';
import PageData from '#/components/page/PageData.vue';
import { $t } from '#/locales';
import { router } from '#/router';
import { useDictStore } from '#/store';

import SysJobModal from './SysJobModal.vue';

onMounted(() => {
  initDict();
});

const pageDataRef = ref();
const saveDialog = ref();
const dictStore = useDictStore();
const headerButtons = [
  {
    key: 'create',
    text: $t('button.add'),
    icon: markRaw(Plus),
    type: 'primary',
    data: { action: 'create' },
    permission: '/api/v1/sysRole/save',
  },
];

function initDict() {
  dictStore.fetchDictionary('jobType');
  dictStore.fetchDictionary('jobStatus');
  dictStore.fetchDictionary('yesOrNo');
  dictStore.fetchDictionary('misfirePolicy');
}
const handleSearch = (params: string) => {
  pageDataRef.value.setQuery({ jobName: params, isQueryOr: true });
};
function reset(formEl?: FormInstance) {
  formEl?.resetFields();
  pageDataRef.value.setQuery({});
}
function showDialog(row: any) {
  saveDialog.value.openDialog({ ...row });
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
          .post('/api/v1/sysJob/remove', { id: row.id })
          .then((res) => {
            instance.confirmButtonLoading = false;
            if (res.errorCode === 0) {
              ElMessage.success(res.message);
              reset();
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
function start(row: any) {
  ElMessageBox.confirm($t('message.startAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('message.ok'),
    cancelButtonText: $t('message.cancel'),
    type: 'warning',
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        instance.confirmButtonLoading = true;
        api.get(`/api/v1/sysJob/start?id=${row.id}`).then((res) => {
          instance.confirmButtonLoading = false;
          if (res.errorCode === 0) {
            ElMessage.success(res.message);
            reset();
            done();
          }
        });
      } else {
        done();
      }
    },
  });
}
function stop(row: any) {
  ElMessageBox.confirm($t('message.stopAlert'), $t('message.noticeTitle'), {
    confirmButtonText: $t('message.ok'),
    cancelButtonText: $t('message.cancel'),
    type: 'warning',
    beforeClose: (action, instance, done) => {
      if (action === 'confirm') {
        instance.confirmButtonLoading = true;
        api.get(`/api/v1/sysJob/stop?id=${row.id}`).then((res) => {
          instance.confirmButtonLoading = false;
          if (res.errorCode === 0) {
            ElMessage.success(res.message);
            reset();
            done();
          }
        });
      } else {
        done();
      }
    },
  });
}
function toLogPage(row: any) {
  router.push({
    name: 'SysJobLog',
    query: {
      jobId: row.id,
    },
  });
}
</script>

<template>
  <div class="flex h-full flex-col gap-6 p-6">
    <SysJobModal ref="saveDialog" @reload="reset" />
    <HeaderSearch
      :buttons="headerButtons"
      @search="handleSearch"
      @button-click="showDialog({})"
    />

    <div class="bg-background flex-1 rounded-lg p-5">
      <PageData
        ref="pageDataRef"
        page-url="/api/v1/sysJob/page"
        :page-size="10"
      >
        <template #default="{ pageList }">
          <ElTable :data="pageList" border>
            <ElTableColumn prop="jobName" :label="$t('sysJob.jobName')">
              <template #default="{ row }">
                {{ row.jobName }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="jobType" :label="$t('sysJob.jobType')">
              <template #default="{ row }">
                {{ dictStore.getDictLabel('jobType', row.jobType) }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              prop="cronExpression"
              :label="$t('sysJob.cronExpression')"
            >
              <template #default="{ row }">
                {{ row.cronExpression }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              prop="allowConcurrent"
              :label="$t('sysJob.allowConcurrent')"
            >
              <template #default="{ row }">
                {{ dictStore.getDictLabel('yesOrNo', row.allowConcurrent) }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              prop="misfirePolicy"
              :label="$t('sysJob.misfirePolicy')"
            >
              <template #default="{ row }">
                {{ dictStore.getDictLabel('misfirePolicy', row.misfirePolicy) }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="status" :label="$t('sysJob.status')">
              <template #default="{ row }">
                {{ dictStore.getDictLabel('jobStatus', row.status) }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="created" :label="$t('sysJob.created')">
              <template #default="{ row }">
                {{ row.created }}
              </template>
            </ElTableColumn>
            <ElTableColumn prop="remark" :label="$t('sysJob.remark')">
              <template #default="{ row }">
                {{ row.remark }}
              </template>
            </ElTableColumn>
            <ElTableColumn
              :label="$t('common.handle')"
              width="80"
              align="center"
            >
              <template #default="{ row }">
                <ElDropdown>
                  <ElButton link>
                    <ElIcon>
                      <More />
                    </ElIcon>
                  </ElButton>

                  <template #dropdown>
                    <ElDropdownMenu>
                      <div v-access:code="'/api/v1/sysJob/save'">
                        <ElDropdownItem @click="start(row)">
                          <ElButton :icon="CaretRight" link>
                            {{ $t('button.start') }}
                          </ElButton>
                        </ElDropdownItem>
                      </div>
                      <div
                        v-if="row.status === 1"
                        v-access:code="'/api/v1/sysJob/save'"
                      >
                        <ElDropdownItem @click="stop(row)">
                          <ElButton :icon="CircleCloseFilled" link>
                            {{ $t('button.stop') }}
                          </ElButton>
                        </ElDropdownItem>
                      </div>
                      <div v-access:code="'/api/v1/sysJob/query'">
                        <ElDropdownItem @click="toLogPage(row)">
                          <ElButton :icon="Tickets" link>
                            {{ $t('button.log') }}
                          </ElButton>
                        </ElDropdownItem>
                      </div>
                      <div v-access:code="'/api/v1/sysJob/save'">
                        <ElDropdownItem @click="showDialog(row)">
                          <ElButton :icon="Edit" link>
                            {{ $t('button.edit') }}
                          </ElButton>
                        </ElDropdownItem>
                      </div>
                      <div v-access:code="'/api/v1/sysJob/remove'">
                        <ElDropdownItem @click="remove(row)">
                          <ElButton type="danger" :icon="Delete" link>
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
  </div>
</template>

<style scoped></style>
